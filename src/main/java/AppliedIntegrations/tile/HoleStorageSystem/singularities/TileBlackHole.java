package AppliedIntegrations.tile.HoleStorageSystem.singularities;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketMassChange;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.BlackHoleSystem.IPylon;
import AppliedIntegrations.api.BlackHoleSystem.ISingularity;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.EnergyList;
import AppliedIntegrations.grid.Mana.AEManaStack;
import AppliedIntegrations.grid.Mana.ManaList;
import AppliedIntegrations.tile.HoleStorageSystem.Anomalies.AnomalyEnum;
import AppliedIntegrations.tile.HoleStorageSystem.TimeHandler;
import AppliedIntegrations.tile.entities.EntitySingularity;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AEPartLocation;
import appeng.fluids.util.AEFluidStack;
import appeng.fluids.util.FluidList;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

/**
 * @Author Azazell
 */
public class TileBlackHole extends TileEntity implements ITickable, ISingularity {
	public static LinkedHashMap<Long, Float> sizeFactor = new LinkedHashMap<>();
	private static LinkedHashMap<Short, Byte> typeFactor = new LinkedHashMap<>();

	private final double gravitationalConst = 6.7;

	public long mass = (int) Math.ceil(Math.random() * 16384);
	public int MASS_ADDED = 10;

	public ItemList storedItems = new ItemList();
	public FluidList storedFluids = new FluidList();
	public EnergyList storedEnergies = new EnergyList();
	public ManaList storedMana = new ManaList();

	private TimeHandler blockDestroyHandler = new TimeHandler();

	private TimeHandler anomalyTriggerHandler = new TimeHandler();

	private Random randomAnomaly = new Random();

	private float lastGrowth = 1;

	private List<IPylon> listeners = new ArrayList<>();

	private EntitySingularity entangledHoleEntity = null;
	public TileWhiteHole entangledHole;
	static {
		// Size factor filling
		sizeFactor.put(81280L, 1.2F);
		sizeFactor.put(325120L, 1.3F);
		sizeFactor.put(1300480L, 1.4F);
		sizeFactor.put(5201920L, 1.5F);
		sizeFactor.put(10403840L, 1.6F);
		sizeFactor.put(83230720L, 1.7F);
		sizeFactor.put(332922880L, 1.8F);
		sizeFactor.put(1331691520L, 2F);
		sizeFactor.put(5326766080L, 3F);
		sizeFactor.put(21307064320L, 5F);
		sizeFactor.put(85228257280L, 7F);
		// Easter egg value is hidden, try to find it!)

		// Type factor filling
		typeFactor.put((short) 16, (byte) 2);
		typeFactor.put((short) 32, (byte) 3);
		typeFactor.put((short) 64, (byte) 5);
		typeFactor.put((short) 128, (byte) 6);
		typeFactor.put((short) 256, (byte) 7);
		typeFactor.put((short) 512, (byte) 8);
		typeFactor.put((short) 1024, (byte) 9);
		typeFactor.put((short) 4096, (byte) 10);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		for (IPylon pylon : listeners) {
			pylon.setSingularity(null);
		}
	}

	@Override
	public void update() {
		if (Platform.isServer()) {
			modulateBlockGravity();
		}

		// Trigger anomaly every 20 seconds or when growth factor changes
		if (getGrowthFactor() != lastGrowth || anomalyTriggerHandler.hasTimePassed(world, 20)) {
			onGrowthFactorChange();
			lastGrowth = getGrowthFactor();
		}

		// Check if we have linked entity.
		if (entangledHoleEntity != null && entangledHoleEntity.isDead && entangledHoleEntity.getBornSingularity() != null) {
			entangledHole = (TileWhiteHole) entangledHoleEntity.getBornSingularity();
			entangledHoleEntity = null;

			// Don't send entangle packet since it's already sent by white hole at this moment
		}

		//modulateLivingGravity();
	}

	// Pull all blocks in range
	private void modulateBlockGravity() {
		if (blockDestroyHandler.hasTimePassed(world, 1)) {
			double range = getMaxDestructionRange();
			for (BlockPos b : getBlocksInRadius(range)) {
				createItemBlock(b, world.getBlockState(b).getBlock());
			}
		}
	}

	/* Factor of size (how many items stored in):
	   1. 1к - 81280 - *1.2
	   2. 4k - 325 120 - *1.3
	   3. 16к - 1 300 480 - *1.4
	   4. 64k - 5 201 920 - *1.5
	   5. 256k - 10 403 840 - *1.6
	   6. 1024к - 83 230 720 - *1.7
	   7. 4096k - 332 922 880 - *1.8
	   8. 16 384к - 1 331 691 520 - *2
	   9. 65 536 - 5 326 766 080 - *3
	   9. 262 144к - 21 307 064 320 - *5
	   10. 1 048 576к - 85 228 257 280 - *7
	   11. easter-egg value ;) - *21
	  Factor of types (how many types stored in):
	   1. 16 - *2
	   2. 32 - *3
	   3. 64 - *5
	   4. 128 - *6
	   5. 256 - *7
	   6. 512 - *8
	   7. 1024 - *9
	   8. 2048 - *10
   */
	private Float getGrowthFactor() {
		Float sizeFactorVal = 1F;
		Byte typeFactorVal = 1;

		for (Long value : sizeFactor.keySet()) {
			if (mass >= value) {
				sizeFactorVal = sizeFactor.get(value);
			} else {
				break;
			}
		}

		// Iterate over size factor values until mass will be less than value
        /*for (Short value : typeFactor.keySet()){
            // Check if mass greater or equal
            if(mass >= value)
                // Set factor
                typeFactorVal = typeFactor.get(value);
            else
                // Lvl up, what talent you want?
                // - optimisation
                break;
        }*/

		return sizeFactorVal * typeFactorVal;
	}

	private void onGrowthFactorChange() {
		// Get randomAnomaly anomaly number
		AnomalyEnum anomaly = AnomalyEnum.values()[randomAnomaly.nextInt(AnomalyEnum.values().length)];

		// Trigger anomaly
		//anomaly.action.accept(this);
	}

	public double getMaxDestructionRange() {
		return Math.cbrt(Math.cbrt(mass)) * getGrowthFactor();
	}

	public List<BlockPos> getBlocksInRadius(double radius) {
		// list of positions
		List<BlockPos> blockPositions = new ArrayList<>();

		// Stage #1: Create "iteration square" and find block break candidates for next break stage
		for (int x = -(int) radius; x < radius; x++) {
			for (int y = -(int) radius; y < radius; y++) {
				for (int z = -(int) radius; z < radius; z++) {
					BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
					if (x == pos.getX() && pos.getY() == y && pos.getZ() == z) {
						continue;
					}

					Block b = world.getBlockState(blockPos).getBlock();
					if (b instanceof BlockAir) {
						continue;
					}

					if (b instanceof ISingularity) {
						continue;
					}

					if (b.getBlockHardness(b.getDefaultState(), world, pos) == -1) {
						continue;
					}

					// Check if block is ME pylon
                    /*if (b instanceof BlockMEPylon) {
                        // Provided this, as singularity to pylon
                        TileMEPylon pylon = (TileMEPylon)world.getTileEntity(blockPos);

                        // Check if pylon not already operating black/white hole
                        if(pylon != null && !pylon.hasSingularity()) {
                            // Set tile to this
                            pylon.singularity = this;

                            // Update cell array
                            pylon.postCellInventoryEvent();

                            // Add pylon to listener, to make now invalidate() method should delete #this# from listener
                            listeners.add(pylon);
                        }

                        continue;
                    }*/

					if (crossesRadius(blockPos, pos, getBlackHoleRadius())) {
						// Delete this object forever
						world.setBlockToAir(blockPos);
						addStack(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(new ItemStack(world.getBlockState(pos).getBlock())), Actionable.MODULATE);
					}

					if (crossesRadius(blockPos, pos, radius)) {
						blockPositions.add(blockPos);
					}
				}
			}
		}

		return blockPositions;
	}

	private void createItemBlock(BlockPos pos, Block b) {
		b.breakBlock(world, pos, b.getDefaultState());
		world.setBlockState(pos, Blocks.AIR.getDefaultState());
		//world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(b)));
	}

	private boolean crossesRadius(BlockPos posA, BlockPos posB, double radius) {
		return Math.sqrt(posA.distanceSq(posB)) <= radius;
	}

	public double getBlackHoleRadius() {
		// Real life formula 2GM/c^2 is suitable for getting radius of black hole, if just return mass,
		// then radius will grow instantly. But double cube root is also nice ;3
		double lightSpeed = 3;
		return Math.max(Math.cbrt(Math.cbrt(2 * gravitationalConst * mass / Math.pow(lightSpeed, 2))), 0.3);
	}

	@Override
	public void addMass(long l) {
		NetworkHandler.sendToAllInRange(new PacketMassChange(this, this.getPos()), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
		mass += l;
	}

	@Override
	public IAEStack<?> addStack(IAEStack<?> stack, Actionable actionable) {
		if (stack == null) {
			return null;
		}

		if (stack instanceof AEItemStack) {
			storedItems.add((AEItemStack) stack);
		} else if (stack instanceof AEFluidStack) {
			storedFluids.add((AEFluidStack) stack);
		} else if (stack instanceof AEManaStack) {
			storedMana.add((AEManaStack) stack);
		} else if (stack instanceof AEEnergyStack) {
			storedEnergies.add((AEEnergyStack) stack);
		}

		for (IPylon pylon : listeners) {
			pylon.setDrain(true);
		}

		addMass(stack.getStackSize() * MASS_ADDED);
		return null;
	}

	@Override
	public IItemList<?> getList(IStorageChannel chan) {
		if (chan == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
			return storedItems;
		}
		if (chan == AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class)) {
			return storedFluids;
		}
		if (AIConfig.enableEnergyFeatures && chan == AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class)) {
			return storedEnergies;
		}
		if (AIConfig.enableManaFeatures && chan == AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class) && Loader.isModLoaded("botania")) {
			return storedMana;
		}

		return null;
	}

	@Override
	@SideOnly(CLIENT)
	public void setMassFromServer(long mass) {
		this.mass = mass;
	}

	@Override
	public long getMass() {
		return mass;
	}

	@Override
	public boolean isEntangled() {
		return entangledHole != null;
	}

	@Override
	public void setEntangledHoleEntity(EntitySingularity singularity) {
		AILog.chatLog("Setting entangled singularity to " + singularity.toString());
		entangledHoleEntity = singularity;
	}

	@Override
	public void addListener(IPylon pylon) {

		listeners.add(pylon);
	}

	// returns forge-friendly representation of gravitation radius
	public AxisAlignedBB getAxisAABB(int modulateRadius) {
		// Square with x1,y1,z1 as negative modulate radius and coords
		// x2, y2, z2 as positive modulate radius and coords
		return new AxisAlignedBB(getPos().getX() - (double) modulateRadius, getPos().getY() - (double) modulateRadius, getPos().getZ() - (double) modulateRadius, getPos().getX() + (double) modulateRadius, getPos().getY() + (double) modulateRadius, getPos().getZ() + (double) modulateRadius);
	}

	// Destroys entity if it crossed getBlackHoleRadius()
	private boolean destroyWithCondition(Entity entity) {
		// Get position vector
		Vec3d pos = entity.getPositionVector();

		if (pos.distanceTo(new Vec3d(getPos())) <= getBlackHoleRadius()) {
			entity.setDead();
			world.removeEntity(entity);
			addMass(MASS_ADDED);
			markDirty();
			return true;
		}
		return false;
	}

	// Returns gravity force between two vectors, assuming posB mass is 1
	private double getGravityForceBetween(Vec3d posA, Vec3d posB) {
		double radiusSquare = Math.pow(posA.squareDistanceTo(posB), 2);

		return (gravitationalConst * mass * 0.1 / radiusSquare);
	}

	// ~~Called when radius is growing too much~~
	private void destroySingularity() {
		world.setBlockState(pos, Blocks.AIR.getDefaultState());
	}

	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand) {
		return false;
	}

	@Override
	public BlockPos getHostPos() {
		return pos;
	}

	@Override
	public World getHostWorld() {
		return world;
	}

	@Override
	public AEPartLocation getHostSide() {
		return AEPartLocation.INTERNAL;
	}
}

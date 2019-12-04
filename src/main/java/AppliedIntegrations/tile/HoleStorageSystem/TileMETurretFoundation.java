package AppliedIntegrations.tile.HoleStorageSystem;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketVectorSync;
import AppliedIntegrations.Utils.VectorUtils;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.entities.EntitySingularity;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AEPartLocation;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.item.ItemList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static AppliedIntegrations.Items.NetworkCard.KEY_SUB;
import static java.util.Collections.singletonList;
import static java.util.EnumSet.of;
import static net.minecraft.util.EnumFacing.DOWN;

/**
 * @Author Azazell
 */
public class TileMETurretFoundation extends AITile implements ICellContainer {
	public enum Ammo {
		MatterBall(1, AEApi.instance().definitions().materials().matterBall().maybeStack(1).get()),
		Singularity(25, AEApi.instance().definitions().materials().singularity().maybeStack(1).get());

		final TimeHandler cooldownHandler = new TimeHandler();
		int cooldown;
		ItemStack stack;

		Ammo(int cooldown, ItemStack stack) {
			this.cooldown = cooldown;
			this.stack = stack;
		}

		public static Ammo fromStack(IAEItemStack input) {
			// Get first ammo compared by item and metadata of stack to item and metadata of input
			for (Ammo ammo : values()) {
				final ItemStack stack = ammo.getStack();
				final ItemStack definition = input.getDefinition();
				if (stack.getItem().equals(definition.getItem()) && stack.getMetadata() == definition.getMetadata()) {
					return ammo;
				}
			}

			return null;
		}

		private ItemStack getStack() {
			return stack;
		}

		public boolean hasCooldownPassed(World w) {
			return cooldownHandler.hasTimePassed(w, cooldown);
		}
	}

	// Directions for rendering turret tower
	private static final int HOLES_TRAJECTORY_ANGLE = 20;AEPartLocation e;
	private static final String KEY_STORED_AMMO_SIZE = "#STORED_AMMO_SIZE";
	private static final String KEY_DIRECTION_X = "#DIRECTION_X";
	private static final String KEY_DIRECTION_Y = "#DIRECTION_Y";
	private static final String KEY_DIRECTION_Z = "#DIRECTION_Z";
	private static final String KEY_AMMO_TYPE = "#AMMO_TYPE";

	// Order: Down -> Up -> North -> South -> West -> East -> Internal
	private static final double ANGLE_COS = Math.cos(45);
	private static final Vec3d[] BLACK_HOLE_VECTORS = new Vec3d[] {
	new Vec3d(ANGLE_COS, -1, 0),
	new Vec3d(ANGLE_COS, +1, 0),
	new Vec3d(ANGLE_COS, 0, -1),
	new Vec3d(ANGLE_COS, 0, +1),
	new Vec3d(-1, 0, ANGLE_COS),
	new Vec3d(+1, 0, ANGLE_COS),
	new Vec3d(0,0,0)};

	private static final Vec3d[] WHITE_HOLE_VECTORS = new Vec3d[] {
	new Vec3d(-ANGLE_COS, -1, 0),
	new Vec3d(-ANGLE_COS, +1, 0),
	new Vec3d(-ANGLE_COS, 0, -1),
	new Vec3d(-ANGLE_COS, 0, +1),
	new Vec3d(-1, 0, -ANGLE_COS),
	new Vec3d(+1, 0, -ANGLE_COS),
	new Vec3d(0,0,0)};

	public Vec3d direction = VectorUtils.getFractionalVector(getHostPos());
	public Vec3d blackHolePos = Vec3d.ZERO;
	public Vec3d whiteHolePos = Vec3d.ZERO;


	public Ammo ammo = Ammo.MatterBall;
	private ItemList storedAmmo = new ItemList();

	public TileMETurretFoundation(){
		super();
		this.getProxy().setValidSides(of(DOWN));
	}

	public boolean activate(EnumHand hand, EntityPlayer p) {
		// Only call when player clicking with right hand
		if (hand == EnumHand.MAIN_HAND) {
			if (Platform.isServer()) {
				setDirection(new Vec3d(p.posX, p.posY, p.posZ), p.isSneaking());
				return true;
			}
		}

		return false;
	}

	private void setDirection(Vec3d pos, boolean inverse) {
		// Normalize vector to unit vector. Make direction relative to our pos
		this.direction = VectorUtils.getUnitVector(pos.subtract(VectorUtils.getFractionalVector(getHostPos())));

		// Calculate black/white hole positions from facing of player to our pos
		AEPartLocation facing = VectorUtils.getVectorFacing(direction);

		// For each facing we have predefined direction case
		final int id = facing.ordinal();
		this.blackHolePos = inverse ? BLACK_HOLE_VECTORS[id] : WHITE_HOLE_VECTORS[id];
		this.whiteHolePos = inverse ? WHITE_HOLE_VECTORS[id] : BLACK_HOLE_VECTORS[id];

		// Notify client
		NetworkHandler.sendToAllInRange(new PacketVectorSync(this.direction, this.blackHolePos, this.whiteHolePos, this.ammo, this.getPos()),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 64));
	}

	private IAEItemStack getStoredAmmo(Ammo ammo) {
		return storedAmmo.findPrecise(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(ammo.stack));
	}

	@MENetworkEventSubscribe
	public void updateChannels(final MENetworkChannelsChanged changedChannels) {
		postGridEvent(new MENetworkCellArrayUpdate());
	}

	@MENetworkEventSubscribe
	public final void setPower(final MENetworkPowerStatusChange event) {
		postGridEvent(new MENetworkCellArrayUpdate());
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		for (int i = 0; i < compound.getInteger(KEY_STORED_AMMO_SIZE); i++) {
			storedAmmo.add(AEItemStack.fromNBT(compound.getCompoundTag(KEY_SUB + i)));
		}

		setDirection(new Vec3d(compound.getDouble(KEY_DIRECTION_X), compound.getDouble(KEY_DIRECTION_Y), compound.getDouble(KEY_DIRECTION_Z)), false);
		ammo = Ammo.values()[compound.getInteger(KEY_AMMO_TYPE)];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		// Separate compound for each stack
		int amount = 0;
		final Iterator<IAEItemStack> iterator = storedAmmo.iterator();
		for (int i = 0; i < storedAmmo.size(); i++) {
			if (iterator.hasNext()) {
				IAEItemStack stack = iterator.next();

				NBTTagCompound stackCompound = new NBTTagCompound();
				stack.writeToNBT(stackCompound);
				compound.setTag(KEY_SUB + i, stackCompound);
				amount = i;
			} else {
				break;
			}
		}

		compound.setInteger(KEY_STORED_AMMO_SIZE, amount);
		compound.setInteger(KEY_AMMO_TYPE, ammo.ordinal());
		compound.setDouble(KEY_DIRECTION_X, direction.x);
		compound.setDouble(KEY_DIRECTION_Y, direction.y);
		compound.setDouble(KEY_DIRECTION_Z, direction.z);
		return super.writeToNBT(compound);
	}

	@Override
	public void createProxyNode() {
		super.createProxyNode();
	}

	@Override
	public void update() {
		super.update();

		// Only shoot materballs. Singularities are for storage system
		if (!ammo.hasCooldownPassed(world)) {
			return;
		}

		// Don't function without ammo
		IAEItemStack storageEntry = getStoredAmmo(ammo);
		if (storageEntry == null) {
			return;
		}

		final World hostWorld = getHostWorld();
		if (ammo == Ammo.Singularity) {
			// Singularity storage system starts here. We need even amount of singularities for storage system
			if (hostWorld.isBlockPowered(getHostPos()) && storageEntry.getStackSize() >= 2) {
				// Add our unit vector as velocity to both entities
				final EntitySingularity blackSingularity = new EntitySingularity(hostWorld, getHostPos().add(VectorUtils.toIntegerVector(blackHolePos)), BlocksEnum.BlackHole);
				final EntitySingularity whiteSingularity = new EntitySingularity(hostWorld, getHostPos().add(VectorUtils.toIntegerVector(whiteHolePos)), BlocksEnum.WhiteHole);
				blackSingularity.addVelocity(blackHolePos.x, blackHolePos.y, blackHolePos.z);
				whiteSingularity.addVelocity(whiteHolePos.x, whiteHolePos.y, whiteHolePos.z);

				// Linking entities
				blackSingularity.setLinked(whiteSingularity);
				whiteSingularity.setLinked(blackSingularity);

				hostWorld.spawnEntity(blackSingularity);
				hostWorld.spawnEntity(whiteSingularity);
				storageEntry.setStackSize(storageEntry.getStackSize() - 2);
			}
		} else {
			// Scan for entities in range
			List<EntityLivingBase> ents = hostWorld.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.add(-10, -10, -10), pos.add(10, 10, 10)));
			for (EntityLivingBase ent : ents) {
				// Don't attack players
				if (ent instanceof EntityPlayer) {
					continue;
				}

				// Attacking entity
				setDirection(new Vec3d(ent.posX, ent.posY, ent.posZ), false);
				ent.attackEntityFrom(DamageSource.GENERIC, 1F);
				storageEntry.setStackSize(storageEntry.getStackSize() - 1);
				return;
			}
		}
	}

	@Override
	public void blinkCell(int i) {

	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> iStorageChannel) {
		if (!getGridNode().isActive()) {
			return new ArrayList<>();
		}

		if (iStorageChannel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)) {
			return singletonList(new IMEInventoryHandler<IAEItemStack>() {
				@Override
				public IAEItemStack injectItems(IAEItemStack input, Actionable actionable, IActionSource iActionSource) {
					// Check not null
					if (input == null) {
						return null;
					}

					// Check stack size
					if (input.getStackSize() == 0) {
						return null;
					}

					// Check can accept
					if (!canAccept(input)) {
						return input;
					}

					// Modulate inject
					if (actionable == Actionable.MODULATE) {
						// Add stack
						ammo = Ammo.fromStack(input);
						storedAmmo.add(input);
					}

					return null;
				}

				@Override
				public IAEItemStack extractItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
					return null;
				}

				@Override
				public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
					return storedAmmo;
				}

				@Override
				public IStorageChannel<IAEItemStack> getChannel() {
					return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
				}

				@Override
				public AccessRestriction getAccess() {
					return AccessRestriction.READ_WRITE;
				}

				@Override
				public boolean isPrioritized(IAEItemStack iaeItemStack) {
					return false;
				}

				@Override
				public boolean canAccept(IAEItemStack iaeItemStack) {
					// Get item stack's item
					Item item = iaeItemStack.getItem();

					// Get optional items
					Optional<Item> optionalMatterBall = AEApi.instance().definitions().materials().matterBall().maybeItem();
					Optional<Item> optionalBlackHoleBall = AEApi.instance().definitions().materials().singularity().maybeItem();

					return (optionalBlackHoleBall.isPresent() && item == optionalBlackHoleBall.get()) ||
							(optionalMatterBall.isPresent()) && item == optionalMatterBall.get();
				}

				@Override
				public int getPriority() {
					// TODO: 2019-03-25 Add priority
					return 0;
				}

				@Override
				public int getSlot() {
					// Ignored
					return 0;
				}

				@Override
				public boolean validForPass(int i) {
					// Ignored
					return true;
				}
			});
		}

		return new ArrayList<>();
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {

	}
}
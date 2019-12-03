package AppliedIntegrations.tile.HoleStorageSystem;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketVectorSync;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.entities.EntityBlackHole;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.util.Platform;
import appeng.util.item.ItemList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	public TileMETurretFoundation(){
		super();
		this.getProxy().setValidSides(of(DOWN));
	}

	// Directions for rendering turret tower
	private int holesTrajectoryAngle = 20;
	public BlockPos direction = new BlockPos(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
	public BlockPos blackHolePos = addAngleToVector(direction, holesTrajectoryAngle, EnumFacing.Axis.Y);
	public BlockPos whiteHolePos = addAngleToVector(direction, -holesTrajectoryAngle, EnumFacing.Axis.Y);

	public Ammo ammo = Ammo.MatterBall;
	private ItemList storedAmmo = new ItemList();

	public static BlockPos addAngleToVector(BlockPos pos, int angle, EnumFacing.Axis aroundAxis) {
		final double sin = Math.sin(angle);
		final double cos = Math.cos(angle);

		// Rotating position around X, Y or Z using following formulas:
		/*
		X--------------------------
	    |         x        |   |x'|
	    | y cos θ − z sin θ| = |y'|
	    | y sin θ + z cos θ|   |z'|

	    Y--------------------------
	    | x cos θ + z sin θ|   |x'|
		|        y         | = |y'|
		|−x sin θ + z cos θ|   |z'|

		Z--------------------------
		| x cos θ − y sin θ|   |x'|
		| x sin θ + y cos θ| = |y'|
		|         z        |   |z'|
		 */
		if (aroundAxis == EnumFacing.Axis.X) {
			return new BlockPos(pos.getX(), pos.getY() * cos - pos.getZ() * sin, pos.getY() * sin + pos.getZ() * cos);
		} else if (aroundAxis == EnumFacing.Axis.Y) {
			return new BlockPos(pos.getX() * cos + pos.getZ() * sin, pos.getY(), -pos.getX() * sin + pos.getZ() * cos);
		} else if (aroundAxis == EnumFacing.Axis.Z) {
			return new BlockPos(pos.getX() * cos - pos.getY() * sin, pos.getX() * sin + pos.getY() * cos, pos.getZ());
		}

		return pos;
	}

	public boolean activate(EnumHand hand, EntityPlayer p) {
		// Only call when player clicking with right hand
		if (hand == EnumHand.MAIN_HAND) {
			// Call only on server
			if (Platform.isServer()) {
				setDirection(p.getPosition());
				return true;
			}
		}

		return false;
	}

	private void setDirection(BlockPos pos) {
		// Normalize vector. Make direction relative to our pos +0.5
		this.direction = pos.subtract(getHostPos());
		this.blackHolePos = addAngleToVector(direction, holesTrajectoryAngle, EnumFacing.Axis.Y);
		this.whiteHolePos = addAngleToVector(direction, -holesTrajectoryAngle, EnumFacing.Axis.Y);

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

	@Override
	public void createProxyNode() {
		super.createProxyNode();
		postGridEvent(new MENetworkCellArrayUpdate());
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
			if (hostWorld.isBlockPowered(getHostPos()) && storageEntry.getStackSize() % 2 == 0) {
				// Add 1/10 velocity from final destination
				final EntityBlackHole entity = new EntityBlackHole(hostWorld, getHostPos());
				entity.addVelocity(blackHolePos.getX() / 10f, blackHolePos.getY() / 10f, blackHolePos.getZ() / 10f);
				hostWorld.spawnEntity(entity);
				storageEntry.setStackSize(storageEntry.getStackSize() - 1);
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
				setDirection(ent.getPosition());
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
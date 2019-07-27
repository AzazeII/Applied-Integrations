package AppliedIntegrations.Parts.Interaction;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Inventory.Manager.UpgradeInventoryManager;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.me.helpers.MachineSource;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;

import static appeng.api.networking.ticking.TickRateModulation.SAME;

/**
 * @Author Azazell
 */
public class PartInteractionPlane extends AIPart implements IGridTickable, UpgradeInventoryManager.IUpgradeInventoryManagerHost {
	private WeakReference<FakePlayer> fakePlayer;
	private final static int MAX_FILTER_SIZE = 9;
	private static final String KEY_FILTER_INVENTORY = "#FILTER_INVENTORY_KEY";
	private static final String KEY_UPGRADE_INVENTORY = "#UPGRADE_INVENTORY_KEY";
	private static final String KEY_OPERATED_BLOCK = "#OPERATED_BLOCK_KEY";
	public AIGridNodeInventory filterInventory = new AIGridNodeInventory("Interaction Plane Filter", MAX_FILTER_SIZE, 1);
	public UpgradeInventoryManager upgradeInventoryManager =  new UpgradeInventoryManager(this, "Interaction Plane Upgrade Inventory", 4);
	private BlockPos operatedBlock;
	private UUID uniIdentifier;

	public PartInteractionPlane() {
		super(PartEnum.InteractionPlane);
	}

	private void createFakePlayer() {
		World hostWorld = getHostWorld();

		if (hostWorld instanceof WorldServer) {
			// Generate UUID if no UUID exists already
			if (this.uniIdentifier == null) {
				this.uniIdentifier = UUID.randomUUID();

				IBlockState state = hostWorld.getBlockState(this.getHostPos());
				hostWorld.notifyBlockUpdate(getHostPos(), state, state, 3);
			}

			// Generate fake player
			GameProfile fakeProfile = new GameProfile(this.uniIdentifier, AppliedIntegrations.modid + "fake_player_interaction_plane");

			try {
				fakePlayer = new WeakReference<>(FakePlayerFactory.get((WorldServer) hostWorld, fakeProfile));
			} catch(Exception e) {
				fakePlayer = null;
				return;
			}

			if (fakePlayer.get() == null) {
				fakePlayer = null;
				return;
			}

			// Configure fake player
			FakePlayer fakePlayer = Objects.requireNonNull(this.fakePlayer.get());

			fakePlayer.onGround = true;
			fakePlayer.connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(),
					new NetworkManager(EnumPacketDirection.SERVERBOUND),
					fakePlayer) {
				@SuppressWarnings("rawtypes")
				@Override
				public void sendPacket(@Nonnull Packet packetIn) {}
			};

			fakePlayer.setSilent(true);
		}
	}

	@Override
	public void doSync(int filterSize, boolean redstoneControlled, int upgradeSpeedCount) {}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);

		// Read inventory content
		this.filterInventory.readFromNBT(data.getTagList(KEY_FILTER_INVENTORY, 10));
		this.upgradeInventoryManager.upgradeInventory.readFromNBT(data.getTagList(KEY_UPGRADE_INVENTORY, 10));

		// Read operated block
		this.operatedBlock = BlockPos.fromLong(data.getLong(KEY_OPERATED_BLOCK));
	}

	@Override
	public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {
		super.writeToNBT(data, saveType);

		// Write inventory content
		data.setTag(KEY_FILTER_INVENTORY, this.filterInventory.writeToNBT());
		data.setTag(KEY_UPGRADE_INVENTORY, this.upgradeInventoryManager.upgradeInventory.writeToNBT());

		// Write operated block
		data.setLong(KEY_OPERATED_BLOCK, this.operatedBlock.toLong());
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Activation logic is server sided
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiInteraction, player, getHostSide(), getHostTile().getPos());
			}
		}
		return true;
	}

	@Override
	public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos pos, BlockPos changedPos) {
		super.onNeighborChanged(iBlockAccess, pos, changedPos);

		// Select block pos
		if (getHostPos().offset(getHostSide().getFacing()).equals(changedPos)) {
			operatedBlock = changedPos;
		}
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered()) {
			if (this.isActive()) {
				return PartModelEnum.INTERACTION_HAS_CHANNEL;
			} else {
				return PartModelEnum.INTERACTION_ON;
			}
		}
		return PartModelEnum.INTERACTION_OFF;
	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() {
		return null;
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		// Interface-like boxes
		bch.addBox(2.0D, 2.0D, 15.0D, 14.0D, 14.0D, 16.0D);
		bch.addBox(4.0D, 4.0D, 14.0D, 12.0D, 12.0D, 15.0D);
		bch.addBox(5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 14.0D);
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Override
	public float getCableConnectionLength(AECableType cable) {
		return 2F;
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode node, int ticksSinceLastCall) {
		if (operatedBlock == null) {
			return SAME;
		}

		// Create new fake player for this run
		createFakePlayer();

		if (fakePlayer == null) {
			return SAME;
		}

		// Try to extract filtered item(s) from ME inventory and use it on operated tile and then inject output items(if any)
		FakePlayer player = Objects.requireNonNull(fakePlayer.get());

		for (ItemStack stack : filterInventory.slots) {
			// Don't operate with empty stack unless there is inverter card
			if (stack.isEmpty()) {
				continue;
			}

			player.setHeldItem(EnumHand.MAIN_HAND, stack.copy());

			IStorageGrid cache = node.getGrid().getCache(IStorageGrid.class);
			IMEMonitor<IAEItemStack> inventory = cache.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
			AEItemStack input = AEItemStack.fromItemStack(player.getHeldItemMainhand());
			IAEItemStack extracted = inventory.extractItems(input, Actionable.MODULATE, new MachineSource(this));

			if (extracted != null) {
				// Click on block
				ItemStack itemStack = player.getHeldItem(EnumHand.MAIN_HAND);
				player.interactionManager.processRightClickBlock(player, getHostWorld(), itemStack, EnumHand.MAIN_HAND, operatedBlock, EnumFacing.UP, .5F, .5F, .5F);

				IAEItemStack aeStack = AEItemStack.fromItemStack(player.getHeldItem(EnumHand.MAIN_HAND));

				if (aeStack == null || aeStack.getDefinition().isEmpty()) {
					return SAME;
				}

				// Modulate item injection into ME system
				IAEItemStack notInjected = inventory.injectItems(aeStack, Actionable.MODULATE, new MachineSource(this));

				// Drop not injected item stack
				if (notInjected != null) {
					getHostWorld().spawnEntity(new EntityItem(getHostWorld(),
							0.5 + getHostPos().getX(),
							0.5 + getHostPos().getY(),
							0.2 + getHostPos().getZ(),
							notInjected.getDefinition().copy()));
				}
			}
		}

		return SAME;
	}

	@Nonnull
	@Override
	public TickingRequest getTickingRequest(@Nonnull IGridNode node) {
		return new TickingRequest(10, 10, false, false);
	}
}

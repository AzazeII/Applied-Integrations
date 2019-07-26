package AppliedIntegrations.Parts.Interaction;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
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
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumActionResult;
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

/**
 * @Author Azazell
 */
public class PartInteractionPlane extends AIPart implements IGridTickable {
	private final static int MAX_FILTER_SIZE = 9;
	private WeakReference<FakePlayer> fakePlayer;
	public AIGridNodeInventory filterInventory = new AIGridNodeInventory("Interaction Plane Filter", MAX_FILTER_SIZE, 1);
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
			GameProfile fake_profile = new GameProfile(this.uniIdentifier, AppliedIntegrations.modid + "fake_player_interaction_plane");

			try {
				fakePlayer = new WeakReference<>(FakePlayerFactory.get((WorldServer) hostWorld, fake_profile));
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
				public void sendPacket(@Nonnull Packet packetIn) { }
			};

			fakePlayer.setSilent(true);
		}
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Activation logic is server sided
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				// Open GUI
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiInteraction, player, getHostSide(), getHostTile().getPos());
			}
		}
		return true;
	}

	@Override
	public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos pos, BlockPos changedPos) {
		super.onNeighborChanged(iBlockAccess, pos, changedPos);

		// Select block pos
		if (pos == getHostPos().offset(getHostSide().getFacing())) {
			operatedBlock = pos;
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
		bch.addBox(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
		bch.addBox(5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 14.0D);

		// Additional boxes
		bch.addBox(2.0D, 8.0D, 14.0D, 14.0D, 8.0D, 16.0D);
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
			return TickRateModulation.SAME;
		}

		// Create new fake player for this run
		createFakePlayer();

		if (fakePlayer == null) {
			return TickRateModulation.SAME;
		}

		// Try to extract filtered item(s) from ME inventory and use it on operated tile
		FakePlayer player = Objects.requireNonNull(fakePlayer.get());

		for (ItemStack stack : filterInventory.slots) {
			player.setHeldItem(EnumHand.MAIN_HAND, stack);

			// Click on block
			ItemStack itemStack = Objects.requireNonNull(fakePlayer.get()).getHeldItemMainhand();

			EnumActionResult result = player.interactionManager.processRightClickBlock(
					player, getHostWorld(),
					itemStack, EnumHand.MAIN_HAND, operatedBlock, EnumFacing.UP, .5F, .5F, .5F);

			if (result != EnumActionResult.FAIL) {
				// Don't do anything if item stack didn't changed
				boolean equal = ItemStack.areItemStacksEqual(itemStack, player.getHeldItemMainhand());

				if (!equal && !player.getHeldItemMainhand().isEmpty()) {
					// Modulate item injection into ME system
					IStorageGrid cache = node.getGrid().getCache(IStorageGrid.class);
					AEItemStack input = AEItemStack.fromItemStack(player.getHeldItemMainhand());
					IAEItemStack notInjected = cache.getInventory(AEApi.instance().storage().getStorageChannel(
							IItemStorageChannel.class)).injectItems(input, Actionable.MODULATE, new MachineSource(this));

					// Drop not injected item stack
					if (notInjected != null) {
						getHostWorld().spawnEntity( new EntityItem( getHostWorld(),
								0.5 + getHostPos().getX(),
								0.5 + getHostPos().getY(),
								0.2 + getHostPos().getZ(), notInjected.getDefinition().copy()));
					}
				}
			}
		}

		return TickRateModulation.SAME;
	}

	@Nonnull
	@Override
	public TickingRequest getTickingRequest(@Nonnull IGridNode node) {
		return new TickingRequest(4, 4, false, false);
	}
}

package AppliedIntegrations.Parts.Interaction;


import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.UUID;

import static net.minecraft.util.EnumHand.MAIN_HAND;

/**
 * @Author Azazell
 */
public class InteractionPlaneHandler implements IMEInventoryHandler<IAEItemStack> {
	private BlockPos pos;

	private PartInteractionPlane host;

	private GameProfile fakeProfile = new GameProfile(UUID.randomUUID(), "ME Interaction Plane");

	public InteractionPlaneHandler(PartInteractionPlane host) {
		this.host = host;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public AccessRestriction getAccess() {
		return AccessRestriction.WRITE;
	}

	@Override
	public boolean isPrioritized(IAEItemStack input) {
		return false;
	}

	@Override
	public boolean canAccept(IAEItemStack input) {
		return true;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int getSlot() {
		return 0;
	}

	@Override
	public boolean validForPass(int i) {
		return true;
	}

	@Override
	public IAEItemStack injectItems(IAEItemStack input, Actionable type, IActionSource src) {
		// Check if action source is machine
		if (src.machine().isPresent()) {
			// Check if source machine instance of host
			if (src.machine().get() instanceof PartInteractionPlane){
				// Ignore, to avoid chain calling, sinc8e interaction plane is also injecting items on callback
				return input;
			}
		}

		// Check if input is null or empty
		if (input == null || input.isMeaningful()) {
			return input;
		}

		// Check if handled block is null
		if (pos == null) {
			return input;
		}

		try {
			// Try to inject this stack into network
			IAEItemStack simulation = host.getProxy().getStorage().getInventory(getChannel()).injectItems(input, Actionable.SIMULATE, src);

			// Check if it's modulation
			if (type == Actionable.MODULATE) {
				// Get stack from input
				ItemStack stack = simulation.getDefinition();

				// Create fake player
				FakePlayer player = new FakePlayer((WorldServer) host.getWorld(), fakeProfile);

				// Set held item for player
				player.setHeldItem(MAIN_HAND, stack);

				// Get block and click on block with stack
				host.getWorld().getBlockState(pos).getBlock().onBlockActivated(host.getWorld(), host.getPositionVector(), host.getWorld().getBlockState(pos),
						player, MAIN_HAND, host.getSide().getFacing(), 0, 0, 0);

				// Return ae stack from stack
				return input.setStackSize(input.getStackSize() - stack.getCount());
			}

			// Simulation value
			return simulation;
		} catch (GridAccessException ignored){

		}

		return input;
	}

	@Override
	public IAEItemStack extractItems(IAEItemStack request, Actionable mode, IActionSource src) {
		// Ignored
		return null;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out) {
		// Ignored
		return out;
	}

	@Override
	public IStorageChannel<IAEItemStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
	}
}

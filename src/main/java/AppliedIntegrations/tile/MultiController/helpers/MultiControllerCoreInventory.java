package AppliedIntegrations.tile.MultiController.helpers;


import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

public class MultiControllerCoreInventory implements IMEInventoryHandler<IAEItemStack> {
	private final IItemList<IAEItemStack> itemList = getChannel().createList();
	private final TileMultiControllerCore host;

	public MultiControllerCoreInventory(TileMultiControllerCore host) {
		this.host = host;
	}

	@Override
	public AccessRestriction getAccess() {
		return AccessRestriction.READ_WRITE;
	}

	@Override
	public boolean isPrioritized(IAEItemStack input) {
		return false;
	}

	@Override
	public boolean canAccept(IAEItemStack input) {
		return input.getItem() instanceof NetworkCard;
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
	public IAEItemStack injectItems(IAEItemStack input, Actionable action, IActionSource src) {
		// Check if we can accept given input
		if( this.canAccept( input ) ) {
			// Check if action is simulate
			if( action == Actionable.SIMULATE ) {
				// Everything consumed
				return null;
			}


			// Add input to list
			itemList.add( input );

			// Notify host
			host.getCardManager().onInventoryChanged();

			// Everything consumed
			return null;
		}

		// Nothing consumed
		return input;
	}

	@Override
	public IAEItemStack extractItems(IAEItemStack request, Actionable mode, IActionSource src) {
		// Try to find target in existing list
		final IAEItemStack target = itemList.findPrecise( request );

		// Check not null
		if( target != null ) {
			// Create copy of target
			final IAEItemStack output = target.copy();

			// Check if action is simulate
			if( mode == Actionable.SIMULATE ) {
				// Everything extracted
				return output;
			}

			// Remove any stack size from target stack
			target.setStackSize( 0 );

			// Notify host
			host.getCardManager().onCardRemove(request.createItemStack());

			// Everything extracted
			return output;
		}

		// Nothing extracted
		return null;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out) {
		// Iterate for each stack in item list
		for (IAEItemStack stack : itemList) {
			// Add stack to out
			out.add(stack);
		}

		return out;
	}

	@Override
	public IStorageChannel<IAEItemStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
	}
}

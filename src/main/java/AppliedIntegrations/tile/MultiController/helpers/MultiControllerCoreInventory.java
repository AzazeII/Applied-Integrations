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

/**
 * @Author Azazell
 * Inner inventory of MultiController core that contains cards
 */
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
		if( this.canAccept( input ) ) {
			if( action == Actionable.SIMULATE ) {
				return null;
			}


			itemList.add( input );
			host.getCardManager().onInventoryChanged();
			return null;
		}

		return input;
	}

	@Override
	public IAEItemStack extractItems(IAEItemStack request, Actionable mode, IActionSource src) {
		final IAEItemStack target = itemList.findPrecise( request );
		if( target != null ) {
			final IAEItemStack output = target.copy();
			if( mode == Actionable.SIMULATE ) {
				return output;
			}

			target.setStackSize( 0 );
			host.getCardManager().onCardRemove(request.createItemStack());
			return output;
		}

		return null;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> out) {
		for (IAEItemStack stack : itemList) {
			out.add(stack);
		}

		return out;
	}

	@Override
	public IStorageChannel<IAEItemStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
	}
}

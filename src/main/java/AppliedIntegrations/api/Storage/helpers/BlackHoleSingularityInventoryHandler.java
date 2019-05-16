package AppliedIntegrations.api.Storage.helpers;


import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public abstract class BlackHoleSingularityInventoryHandler<T extends IAEStack<T>> extends SingularityInventoryHandler<T> {
	@Override
	public final AccessRestriction getAccess() {

		return AccessRestriction.READ_WRITE;
	}

	@Override
	public final boolean canAccept(T t) {

		return true;
	}

	@Override
	public final T extractItems(T t, Actionable actionable, IActionSource iActionSource) {

		return null;
	}

	@Override
	public final IItemList<T> getAvailableItems(IItemList<T> iItemList) {
		// Add no items
		return iItemList;
	}
}

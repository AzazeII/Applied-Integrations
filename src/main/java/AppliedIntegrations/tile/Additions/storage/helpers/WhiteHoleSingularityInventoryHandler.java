package AppliedIntegrations.tile.Additions.storage.helpers;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

public abstract class WhiteHoleSingularityInventoryHandler<T extends IAEStack<T>> extends SingularityInventoryHandler<T> {
    @Override
    public final AccessRestriction getAccess() {
        return AccessRestriction.READ;
    }

    @Override
    public final boolean canAccept(T iaeStack) {
        return false;
    }

    @Override
    public final T injectItems(T iaeStack, Actionable actionable, IActionSource iActionSource) {
        return iaeStack;
    }
}

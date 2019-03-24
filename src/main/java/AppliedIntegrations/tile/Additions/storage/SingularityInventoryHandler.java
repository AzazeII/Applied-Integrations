package AppliedIntegrations.tile.Additions.storage;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

public abstract class SingularityInventoryHandler<T extends IAEStack<T>> implements IMEInventoryHandler<T> {
    @Override
    public boolean isPrioritized(T t) {
        return false;
    }

    @Override
    public boolean canAccept(T t) {
        return getAccess() == AccessRestriction.WRITE
                || getAccess() == AccessRestriction.READ_WRITE;
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
}

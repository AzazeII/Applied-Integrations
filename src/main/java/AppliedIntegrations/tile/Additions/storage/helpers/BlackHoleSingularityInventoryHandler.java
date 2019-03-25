package AppliedIntegrations.tile.Additions.storage.helpers;

import AppliedIntegrations.API.ISingularity;
import AppliedIntegrations.Utils.AILog;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class BlackHoleSingularityInventoryHandler<T extends IAEStack<T>> extends SingularityInventoryHandler<T> {

    public BlackHoleSingularityInventoryHandler(){

    }

    public BlackHoleSingularityInventoryHandler(ISingularity iSingularity){
        // Set singularity
        singularity = iSingularity;
    }

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

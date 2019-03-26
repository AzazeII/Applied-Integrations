package AppliedIntegrations.API.Storage.helpers;

import AppliedIntegrations.API.ISingularity;
import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

abstract class SingularityInventoryHandler<T extends IAEStack<T>> implements IMEInventoryHandler<T> {

    // Singularity operated
    public ISingularity singularity;

    public SingularityInventoryHandler(){

    }

    @Override
    public boolean isPrioritized(T t) {
        return false;
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

    public final void setSingularity(ISingularity singularity){
        this.singularity = singularity;
    }
}

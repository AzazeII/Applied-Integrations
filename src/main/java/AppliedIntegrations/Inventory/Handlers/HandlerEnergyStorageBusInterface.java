package AppliedIntegrations.Inventory.Handlers;

import AppliedIntegrations.API.IEnergyInterface;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyStorageChannel;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.*;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public class HandlerEnergyStorageBusInterface
        implements IMEInventoryHandler<IAEEnergyStack> {

    private final IEnergyInterface iEnergyInterface;

    public HandlerEnergyStorageBusInterface(IEnergyInterface iEnergyInterface){
        this.iEnergyInterface = iEnergyInterface;
    }

    @Override
    public AccessRestriction getAccess() {
        return AccessRestriction.READ_WRITE;
    }

    @Override
    public boolean isPrioritized(IAEEnergyStack iaeEnergyStack) {
        return false;
    }

    @Override
    public boolean canAccept(IAEEnergyStack iaeEnergyStack) {
        return true;
    }

    @Override
    public int getPriority() {
        // TODO: 2019-03-27 priority
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
    public IAEEnergyStack injectItems(IAEEnergyStack iaeEnergyStack, Actionable actionable, IActionSource iActionSource) {
        return iEnergyInterface.getOuterGridInventory().injectItems(iaeEnergyStack, actionable, iActionSource);
    }

    @Override
    public IAEEnergyStack extractItems(IAEEnergyStack iaeEnergyStack, Actionable actionable, IActionSource iActionSource) {
        return iEnergyInterface.getOuterGridInventory().extractItems(iaeEnergyStack, actionable, iActionSource);
    }

    @Override
    public IItemList<IAEEnergyStack> getAvailableItems(IItemList<IAEEnergyStack> iItemList) {
        return iEnergyInterface.getOuterGridInventory().getAvailableItems(iItemList);
    }

    @Override
    public IStorageChannel<IAEEnergyStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
    }
}
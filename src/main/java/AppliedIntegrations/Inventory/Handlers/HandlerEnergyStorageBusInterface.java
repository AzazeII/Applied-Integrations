package AppliedIntegrations.Inventory.Handlers;

import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyStorageChannel;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.*;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public class HandlerEnergyStorageBusInterface
        implements IMEMonitorHandlerReceiver<IAEEnergyStack>, IMEInventoryHandler<IAEEnergyStack>
{
    @Override
    public boolean isValid(Object o) {
        return false;
    }

    @Override
    public void postChange(IBaseMonitor<IAEEnergyStack> iBaseMonitor, Iterable<IAEEnergyStack> iterable, IActionSource iActionSource) {

    }

    @Override
    public void onListUpdate() {

    }

    @Override
    public AccessRestriction getAccess() {
        return null;
    }

    @Override
    public boolean isPrioritized(IAEEnergyStack iaeEnergyStack) {
        return false;
    }

    @Override
    public boolean canAccept(IAEEnergyStack iaeEnergyStack) {
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
        return false;
    }

    @Override
    public IAEEnergyStack injectItems(IAEEnergyStack iaeEnergyStack, Actionable actionable, IActionSource iActionSource) {
        return null;
    }

    @Override
    public IAEEnergyStack extractItems(IAEEnergyStack iaeEnergyStack, Actionable actionable, IActionSource iActionSource) {
        return null;
    }

    @Override
    public IItemList<IAEEnergyStack> getAvailableItems(IItemList<IAEEnergyStack> iItemList) {

        return null;
    }

    @Override
    public IStorageChannel<IAEEnergyStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
    }
}
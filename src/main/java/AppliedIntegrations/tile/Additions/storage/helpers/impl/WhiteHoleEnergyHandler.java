package AppliedIntegrations.tile.Additions.storage.helpers.impl;

import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyStorageChannel;
import AppliedIntegrations.API.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

import static appeng.api.config.Actionable.MODULATE;

public class WhiteHoleEnergyHandler extends WhiteHoleSingularityInventoryHandler<IAEEnergyStack> {
    @Override
    public IAEEnergyStack extractItems(IAEEnergyStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        // Check if there is singularity
        if(singularity != null){
            // Modulate extraction
            if(actionable == MODULATE){
                // Remove data from storage list
                singularity.addStack(iaeItemStack);
                // Return null, as all items was extracted
                return iaeItemStack;
            }
        }
        return null;
    }

    @Override
    public IItemList<IAEEnergyStack> getAvailableItems(IItemList<IAEEnergyStack> iItemList) {
        // Check if there is singularity
        if(singularity != null){
            // Iterate over all items in stack
            for(IAEStack<?> stack : singularity.getList(IAEEnergyStack.class)){
                // Add stack to already existed
                iItemList.add((IAEEnergyStack)stack);
            }
        }
        return iItemList;
    }

    @Override
    public IStorageChannel<IAEEnergyStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
    }
}

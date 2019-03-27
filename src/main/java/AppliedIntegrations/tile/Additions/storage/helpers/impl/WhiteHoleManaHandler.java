package AppliedIntegrations.tile.Additions.storage.helpers.impl;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaStorageChannel;
import AppliedIntegrations.API.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

import static appeng.api.config.Actionable.MODULATE;

public class WhiteHoleManaHandler extends WhiteHoleSingularityInventoryHandler<IAEManaStack> {
    @Override
    public IAEManaStack extractItems(IAEManaStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        // Check if there is singularity
        if(singularity != null){
            // Remove data from storage list, and return extracted amount
            return (IAEManaStack)singularity.addStack(iaeItemStack, actionable);
        }
        return null;
    }

    @Override
    public IItemList<IAEManaStack> getAvailableItems(IItemList<IAEManaStack> iItemList) {
        // Check if there is singularity
        if(singularity != null){
            // Iterate over all items in stack
            for(IAEStack<?> stack : singularity.getList(getChannel())){
                // Add stack to already existed
                iItemList.add((IAEManaStack)stack);
            }
        }
        return iItemList;
    }

    @Override
    public IStorageChannel<IAEManaStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
    }
}

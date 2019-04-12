package AppliedIntegrations.Tile.HoleStorageSystem.storage.helpers.impl;

import AppliedIntegrations.API.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public class WhiteHoleItemHandler extends WhiteHoleSingularityInventoryHandler<IAEItemStack> {
    @Override
    public IAEItemStack extractItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        // Check if there is singularity
        if(singularity != null){
            // Remove data from storage list, and return extracted amount
            return (IAEItemStack)singularity.addStack(iaeItemStack, actionable);
        }
        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
        // Check if there is singularity
        if(singularity != null){
            // Iterate over all items in stack
            for(IAEStack<?> stack : singularity.getList(getChannel())){
                // Add stack to already existed
                iItemList.add((IAEItemStack)stack);
            }
        }
        return iItemList;
    }

    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }
}

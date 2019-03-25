package AppliedIntegrations.tile.Additions.storage.helpers.impl;

import AppliedIntegrations.tile.Additions.storage.helpers.WhiteHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;

import static appeng.api.config.Actionable.MODULATE;

public class WhiteHoleItemHandler extends WhiteHoleSingularityInventoryHandler<IAEItemStack> {
    @Override
    public IAEItemStack extractItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        // Check if there is singularity
        if(singularity != null){
            // Modulate extraction
            if(actionable == MODULATE){
                // Remove mass for each item in stack
                singularity.addMass(iaeItemStack.getStackSize() * 10);
                // Remove data from storage list
                singularity.addStack(iaeItemStack);
                // Return null, as all items was extracted
                return iaeItemStack;
            }
        }
        return null;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(IItemList<IAEItemStack> iItemList) {
        // Check if there is singularity
        if(singularity != null){
            // Iterate over all items in stack
            for(IAEStack<?> stack : singularity.getList(IAEItemStack.class)){
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

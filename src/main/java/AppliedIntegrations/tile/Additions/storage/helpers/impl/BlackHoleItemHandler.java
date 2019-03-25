package AppliedIntegrations.tile.Additions.storage.helpers.impl;

import AppliedIntegrations.API.ISingularity;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.Additions.storage.helpers.BlackHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;

import static appeng.api.config.Actionable.MODULATE;

public class BlackHoleItemHandler extends BlackHoleSingularityInventoryHandler<IAEItemStack> {

    public BlackHoleItemHandler(ISingularity singularity){
        super(singularity);
    }

    @Override
    public IAEItemStack injectItems(IAEItemStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        // Check if there is singularity
        if(singularity != null){
            // Modulate extraction
            if(actionable == MODULATE){
                // Add mass for each item in stack
                singularity.addMass(iaeItemStack.getStackSize() * 10);
                // Add data to storage list
                singularity.addStack(iaeItemStack);
            }
            // Return null, as all items was extracted
            return null;
        }
        return iaeItemStack;
    }

    @Override
    public IStorageChannel<IAEItemStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }
}

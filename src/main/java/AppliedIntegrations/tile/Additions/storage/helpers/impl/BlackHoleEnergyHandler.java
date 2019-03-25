package AppliedIntegrations.tile.Additions.storage.helpers.impl;

import AppliedIntegrations.API.ISingularity;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyStorageChannel;
import AppliedIntegrations.tile.Additions.storage.helpers.BlackHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;

import static appeng.api.config.Actionable.MODULATE;

public class BlackHoleEnergyHandler extends BlackHoleSingularityInventoryHandler<IAEEnergyStack> {

    public BlackHoleEnergyHandler(ISingularity singularity){
        super(singularity);
    }
    @Override
    public IAEEnergyStack injectItems(IAEEnergyStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        // Check if there is singularity
        if(singularity != null){
            // Modulate extraction
            if(actionable == MODULATE){
                // Add mass for each item in stack
                singularity.addMass(iaeItemStack.getStackSize() * 10);
                // Add data to storage list
                singularity.addStack(iaeItemStack);
            }
            // Return null, as all items was injected
            return null;
        }
        return iaeItemStack;
    }

    @Override
    public IStorageChannel<IAEEnergyStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
    }
}

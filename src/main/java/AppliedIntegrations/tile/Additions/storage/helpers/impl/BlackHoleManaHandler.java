package AppliedIntegrations.tile.Additions.storage.helpers.impl;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaStorageChannel;
import AppliedIntegrations.API.ISingularity;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.Additions.storage.helpers.BlackHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;

import static appeng.api.config.Actionable.MODULATE;

public class BlackHoleManaHandler extends BlackHoleSingularityInventoryHandler<IAEManaStack> {

    public BlackHoleManaHandler(ISingularity singularity){
        super(singularity);
    }

    @Override
    public IAEManaStack injectItems(IAEManaStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        AILog.info("Trying to inject items");
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
    public IStorageChannel<IAEManaStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
    }
}

package AppliedIntegrations.Tile.HoleStorageSystem.storage.helpers.impl;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaStorageChannel;
import AppliedIntegrations.API.Storage.helpers.BlackHoleSingularityInventoryHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IStorageChannel;

import static appeng.api.config.Actionable.MODULATE;

/**
 * @Author Azazell
 */
public class BlackHoleManaHandler extends BlackHoleSingularityInventoryHandler<IAEManaStack> {

    @Override
    public IAEManaStack injectItems(IAEManaStack iaeItemStack, Actionable actionable, IActionSource iActionSource) {
        // Check if there is singularity
        if(singularity != null){
            // Modulate extraction
            if(actionable == MODULATE){
                // Add data to storage list
                singularity.addStack(iaeItemStack, actionable);
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

package AppliedIntegrations;

import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Storage.helpers.BlackHoleSingularityInventoryHandler;
import AppliedIntegrations.api.Storage.helpers.WhiteHoleSingularityInventoryHandler;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import appeng.api.storage.IStorageChannel;

/**
 * @Author Azazell
 */
public class ApiInstance extends AIApi {
    private static AIApi instance;

    @Override
    public void addHandlersForMEPylon(Class<? extends BlackHoleSingularityInventoryHandler<?>> handlerClassA, Class<? extends WhiteHoleSingularityInventoryHandler<?>> handlerClassB,
                                      IStorageChannel chan) {
        TileMEPylon.addBlackHoleHandler(handlerClassA, chan);
        TileMEPylon.addWhiteHoleHandler(handlerClassB, chan);
    }

    public static AIApi staticInstance() {
        // Check not null
        if(instance == null)
            // Update instance
            instance = new ApiInstance();

        // Return instance
        return instance;
    }
}

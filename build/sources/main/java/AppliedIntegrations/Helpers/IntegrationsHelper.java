package AppliedIntegrations.Helpers;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import net.minecraftforge.fml.common.Loader;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.*;

public class IntegrationsHelper {
    public static IntegrationsHelper instance = new IntegrationsHelper();

    public boolean isLoaded(LiquidAIEnergy energy){
        if (energy == RF)
            // always true, as RF initialized as FE
            return true;
        if (energy == EU)
            return Loader.isModLoaded("ic2");
        if (energy == J)
            return Loader.isModLoaded("mekanism");
        if (energy == Ember)
            return Loader.isModLoaded("embers");
        if (energy == TESLA)
            return Loader.isModLoaded("tesla");
        return false;
    }
}

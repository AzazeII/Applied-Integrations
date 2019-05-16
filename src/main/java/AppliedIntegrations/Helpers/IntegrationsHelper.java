package AppliedIntegrations.Helpers;

import AppliedIntegrations.Integration.AstralSorcery.IAstralIntegrated;
import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Integration.Embers.IEmberIntegrated;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import net.minecraftforge.fml.common.Loader;

import static AppliedIntegrations.grid.Implementation.AIEnergy.*;

/**
 * @Author Azazell
 */
public class IntegrationsHelper {
	public static IntegrationsHelper instance = new IntegrationsHelper();

	public boolean isLoaded(LiquidAIEnergy energy) {
		if (energy == RF)
		// always true, since RF initialized as FE
		{
			return true;
		}
		if (energy == EU) {
			return Loader.isModLoaded("ic2");
		}
		if (energy == J) {
			return Loader.isModLoaded("mekanism");
		}
		if (energy == Ember) {
			return Loader.isModLoaded("embers");
		}
		if (energy == TESLA) {
			return Loader.isModLoaded("tesla");
		}
		return false;
	}

	public boolean isObjectIntegrated(Object obj) {
		return obj instanceof IBotaniaIntegrated || obj instanceof IEmberIntegrated || obj instanceof IAstralIntegrated;
	}
}

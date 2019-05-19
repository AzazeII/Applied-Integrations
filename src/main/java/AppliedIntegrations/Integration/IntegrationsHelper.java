package AppliedIntegrations.Integration;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import hellfirepvp.astralsorcery.common.registry.RegistryBlocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import teamroots.embers.RegistryManager;
import vazkii.botania.common.block.ModBlocks;

import java.util.Objects;

import static AppliedIntegrations.grid.Implementation.AIEnergy.*;

/**
 * @Author Azazell
 */
public class IntegrationsHelper {
	public static IntegrationsHelper instance = new IntegrationsHelper();

	public boolean isLoaded(LiquidAIEnergy energy) {

		if (energy == RF) {
			// always true, since RF initialized as FE
			return true;
		}
		if (energy == EU) {
			return Loader.isModLoaded("ic2");
		}
		if (energy == J) {
			return Loader.isModLoaded("mekanism");
		}
		if (energy == TESLA) {
			return Loader.isModLoaded("tesla");
		}
		return false;
	}

	public void registerTunnelTypes() {
		// Get api
		final AIApi api = Objects.requireNonNull(AIApi.instance());

		// Check if botania loaded and mana features enabled
		if (Loader.isModLoaded("botania") && AIConfig.enableManaFeatures) {
			api.addTunnelAsStack(Item.getItemFromBlock(ModBlocks.dreamwood), ItemEnum.ITEMP2PMANA.getStack());
		}

		// Check if embers loaded and ember features enabled
		if (Loader.isModLoaded("embers") && AIConfig.enableEmberFeatures) {
			api.addTunnelAsStack(RegistryManager.shard_ember, ItemEnum.ITEMP2PEMBER.getStack());
		}

		// Check if astral sorcery loaded and starlight features enabled
		if (Loader.isModLoaded("astralsorcery") && AIConfig.enableStarlightFeatures) {
			api.addTunnelAsStack(Item.getItemFromBlock(RegistryBlocks.customNameItemBlocksToRegister.get(3)), ItemEnum.ITEMP2PSTARLIGHT.getStack());
		}

		// Check if xnet loaded and xnet features enabled
		if (Loader.isModLoaded("xnet") && AIConfig.enableXnetFeatures) {
			//api.addTunnelAsStack(, ItemEnum.ITEMP2PXNET.getStack());
		}
	}
}

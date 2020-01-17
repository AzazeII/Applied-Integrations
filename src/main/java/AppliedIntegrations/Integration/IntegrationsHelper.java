package AppliedIntegrations.Integration;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
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

	public boolean isLoaded(LiquidAIEnergy energy, boolean trueRFCheck) {

		if (energy == RF) {
			if (!trueRFCheck) {
				// always true, since RF initialized as FE
				return true;
			} else {
				return Loader.isModLoaded("redstoneflux");
			}
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
		final AIApi api = Objects.requireNonNull(AIApi.instance());
		if (Loader.isModLoaded("botania") && AIConfig.enableManaFeatures) {
			api.addTunnelAsStack(Item.getItemFromBlock(ModBlocks.dreamwood), ItemEnum.ITEMP2PMANA.getStack());
		}

		if (Loader.isModLoaded("embers") && AIConfig.enableEmberFeatures) {
			api.addTunnelAsStack(RegistryManager.shard_ember, ItemEnum.ITEMP2PEMBER.getStack());
		}

		if (Loader.isModLoaded("bloodmagic") && AIConfig.enableWillFeatures) {
			api.addTunnelAsStack(RegistrarBloodMagicItems.SOUL_GEM, ItemEnum.ITEMP2PWILL.getStack());
		}

		if (Loader.isModLoaded("astralsorcery") && AIConfig.enableStarlightFeatures) {
			//api.addTunnelAsStack(Item.getItemFromBlock(RegistryBlocks.customNameItemBlocksToRegister.get(3)), ItemEnum.ITEMP2PSTARLIGHT.getStack());
		}

		if (Loader.isModLoaded("xnet") && AIConfig.enableXnetFeatures) {
			//api.addTunnelAsStack(, ItemEnum.ITEMP2PXNET.getStack());
		}
	}
}

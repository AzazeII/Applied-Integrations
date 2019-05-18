package AppliedIntegrations.Proxy;


import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Integration.AstralSorcery.AstralLoader;
import AppliedIntegrations.Integration.BloodMagic.BloodMagicLoader;
import AppliedIntegrations.Integration.Botania.BotaniaLoader;
import AppliedIntegrations.Integration.Embers.EmberLoader;
import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.grid.EnergyStorageChannel;
import AppliedIntegrations.tile.TileEnum;
import appeng.api.AEApi;
import appeng.api.movable.IMovableRegistry;
import appeng.api.recipes.IRecipeLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Author Azazell
 */
public class CommonProxy {

	private class InternalRecipeLoader implements IRecipeLoader {

		@Override
		public BufferedReader getFile(String path) throws Exception {

			InputStream resourceAsStream = getClass().getResourceAsStream("/assets/appliedintegrations/recipes/" + path);
			InputStreamReader reader = new InputStreamReader(resourceAsStream, "UTF-8");
			return new BufferedReader(reader);
		}
	}

	public void SidedPreInit() {
		ItemEnum.register();

		BlocksEnum.register();

		NetworkHandler.registerServerPackets();

		if (AIConfig.enableEnergyFeatures)	{
			// Register channel
			AEApi.instance().storage().registerStorageChannel(IEnergyStorageChannel.class, new EnergyStorageChannel());
		}

		if (Loader.isModLoaded("botania") && AIConfig.enableManaFeatures) {
			BotaniaLoader.preInit();
		}
		if (Loader.isModLoaded("embers") && AIConfig.enableEmberFeatures) {
			EmberLoader.preInit();
		}
		if (Loader.isModLoaded("astralsorcery") && AIConfig.enableStarlightFeatures) {
			AstralLoader.preInit();
		}
		if (Loader.isModLoaded("bloodmagic") && AIConfig.enableWillFeatures) {
			BloodMagicLoader.preInit();
		}
	}

	public void SidedInit(FMLInitializationEvent init) {
		IntegrationsHelper.instance.registerTunnelTypes();

		if (Loader.isModLoaded("botania") && AIConfig.enableManaFeatures) {
			BotaniaLoader.init();
		}
		if (Loader.isModLoaded("embers") && AIConfig.enableEmberFeatures) {
			EmberLoader.init();
		}
		if (Loader.isModLoaded("astralsorcery") && AIConfig.enableStarlightFeatures) {
			AstralLoader.init();
		}
		if (Loader.isModLoaded("bloodmagic") && AIConfig.enableWillFeatures) {
			BloodMagicLoader.init();
		}
	}

	public void SidedPostInit() {

	}

	/**
	 * Adds tile entities to the AE2 SpatialIO whitelist
	 */
	public void registerSpatialIOMovables() {

		IMovableRegistry movableRegistry = AEApi.instance().registries().movable();
		for (TileEnum tile : TileEnum.values()) {
			if (tile.enabled) {
				movableRegistry.whiteListTileEntity(tile.clazz);
			}
		}
	}

	public EntityPlayer getPlayerEntity(MessageContext ctx) {

		return ctx.getServerHandler().player;
	}
}
package AppliedIntegrations.Integration.AstralSorcery;


import AppliedIntegrations.AIConfig;
import appeng.api.AEApi;
import net.minecraftforge.fml.common.Loader;

/**
 * @Author Azazell
 */
public class AstralLoader {
	public static void preInit() {}

	public static void init() {
		AEApi.instance().partHelper().registerNewLayer(StarlightLayer.class.getName(), StarlightLayer.class.getName());
	}

	public static boolean enableStarlight() {
		return Loader.isModLoaded("astralsorcery") && AIConfig.enableStarlightFeatures;
	}
}

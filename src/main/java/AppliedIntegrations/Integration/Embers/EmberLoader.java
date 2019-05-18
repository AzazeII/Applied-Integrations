package AppliedIntegrations.Integration.Embers;


import AppliedIntegrations.AIConfig;
import net.minecraftforge.fml.common.Loader;

/**
 * @Author Azazell
 */
public class EmberLoader {
	public static void preInit() {
	}

	public static void init() {
	}

	public static boolean enableEmber() {
		return Loader.isModLoaded("embers") && AIConfig.enableEmberFeatures;
	}
}

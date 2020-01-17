package AppliedIntegrations.Integration.XNet;


import AppliedIntegrations.AIConfig;
import net.minecraftforge.fml.common.Loader;

/**
 * @Author Azazell
 */
public class XnetLoader {
	public static boolean enableXnet() {
		return Loader.isModLoaded("xnet") && AIConfig.enableXnetFeatures;
	}
}

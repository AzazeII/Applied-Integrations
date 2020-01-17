package AppliedIntegrations.Integration.BloodMagic;


import AppliedIntegrations.AIConfig;
import net.minecraftforge.fml.common.Loader;

/**
 * @Author Azazell
 */
public class BloodMagicLoader {
	public static void preInit() {

	}

	public static void init() {

	}

	public static boolean enableWill() {
		return Loader.isModLoaded("bloodmagic") && AIConfig.enableWillFeatures;
	}
}

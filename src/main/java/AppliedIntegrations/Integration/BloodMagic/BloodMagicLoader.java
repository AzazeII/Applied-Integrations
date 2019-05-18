package AppliedIntegrations.Integration.BloodMagic;


import AppliedIntegrations.AIConfig;
import appeng.api.AEApi;
import net.minecraftforge.fml.common.Loader;

public class BloodMagicLoader {
	public static void preInit() {

	}

	public static void init() {
		AEApi.instance().partHelper().registerNewLayer(WillLayer.class.getName(), WillLayer.class.getName());
	}

	public static boolean enableWill() {
		return Loader.isModLoaded("bloodmagic") && AIConfig.enableWillFeatures;
	}
}

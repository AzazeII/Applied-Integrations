package AppliedIntegrations;


import appeng.api.config.IncludeExclude;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class AIConfig {
	private static final String CATEGORY_FEATURES = "Features";
	private static final String CATEGORY_TILES = "tile entities";
	private static final String CATEGORY_PROPERTIES = "Properties";

	public static boolean enableWebServer;
	public static boolean enableEnergyFeatures;
	public static boolean enableManaFeatures;
	public static boolean enableEmberFeatures;
	public static boolean enableStarlightFeatures;
	public static boolean enableBlackHoleStorage;
	public static boolean enableMEServer;
	public static boolean enableLogicBus;
	public static int interfaceMaxStorage;
	public static IncludeExclude defaultListMode;

	public static int webUIPort;
	public static int maxPylonDistance;
	public static double pylonDrain;

	public static boolean enableXnetFeatures;
	public static boolean enableWillFeatures;
	public static boolean enableInteractionPart;

	private static Configuration config = null;

	public static void preInit() {
		File config = new File(Loader.instance().getConfigDir(), AppliedIntegrations.modid + ".cfg");
		AIConfig.config = new Configuration(config);
		syncFromFiles();
	}

	public static void syncFromFiles() {
		config.load();

		List<String> featuresOrder = new ArrayList<>();
		List<String> tileOrder = new ArrayList<>();
		List<String> propertiesOrder = new ArrayList<>();

		enableWebServer = (Boolean) addProperty(CATEGORY_FEATURES, "EnableWebServer", false, "Default: false; If set to true, then all web UI features will be enabled; Used only on client side", featuresOrder);
		enableEnergyFeatures = (Boolean) addProperty(CATEGORY_FEATURES, "EnableEnergyFeatures", true, "Default: true; If set to true, then all energy features will be enabled. Not recommended to disable, as it is core feature", featuresOrder);
		enableManaFeatures = (Boolean) addProperty(CATEGORY_FEATURES, "EnableManaFeatures", false, "Default: false; If set to true, then all mana features will be enabled.", featuresOrder);
		enableInteractionPart = (Boolean) addProperty(CATEGORY_FEATURES, "Enable Interaction Part", true, "Default: true; If enable, then interaction bus will be available in game", featuresOrder);
		enableEmberFeatures = (Boolean) addProperty(CATEGORY_FEATURES, "EnableEmberFeatures", true, "Default: true; If set to true, then ember p2p tunnel will be available.", featuresOrder);
		enableStarlightFeatures = (Boolean) addProperty(CATEGORY_FEATURES, "EnableStarlightTunnel", true, "Default: true; If set to true, then starlight p2p tunnel will be available.", featuresOrder);
		enableXnetFeatures = (Boolean) addProperty(CATEGORY_FEATURES, "EnableXnetTunnel", true, "Default: true; If set to true, then xnet p2p tunnel will be available.", featuresOrder);
		enableWillFeatures = (Boolean) addProperty(CATEGORY_FEATURES, "EnableWillTunnel", true, "Default: true; If set to true, then will p2p tunnel (demonic) will(you get :) ) be available.", featuresOrder);
		enableBlackHoleStorage = (Boolean) addProperty(CATEGORY_TILES, "EnableBlackHoleStorageSystem", true, "Default: true (only in alpha); If set to true, then all black/white hole storage system blocks will be available in game.", tileOrder);
		enableMEServer = (Boolean) addProperty(CATEGORY_TILES, "EnableMEMC", true, "Default: true; If set to true, then ME multi-controller, blocks will be available in game.", tileOrder);
		enableLogicBus = (Boolean) addProperty(CATEGORY_TILES, "EnableLogicBus", true, "Default: true; If set to true, then Logic bus blocks will be available in game.", tileOrder);
		interfaceMaxStorage = (Integer) addProperty(CATEGORY_PROPERTIES, "InterfaceStorage", 1000000, "Default: 1 000 000 RF; Max capacity of ME Energy interface in RF units (all other units is depend on RF capacity).", propertiesOrder);
		maxPylonDistance = (Integer) addProperty(CATEGORY_PROPERTIES, "PylonDistance", 97, "Default: 97; Max range of ME Pylon's beam", propertiesOrder);
		pylonDrain = (Double) addProperty(CATEGORY_PROPERTIES, "PylonDrainPerBlock", 20.0D, "Default: 20.0D; Active(used only when matter transmitted) energy drain per block of ME pylon's beam. Limit is: " + "10000", propertiesOrder);
		webUIPort = (Integer) addProperty(CATEGORY_PROPERTIES, "WebUI Port", 8000, "Default: 8000; Port for web UI of network topology", propertiesOrder);
		defaultListMode = (Boolean) addProperty(CATEGORY_PROPERTIES, "Default Security Terminal List Mode", false, "Default: False (blacklist); If true, then default mode is server security terminal GUI will be white list", propertiesOrder) ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST;

		config.setCategoryPropertyOrder(CATEGORY_FEATURES, featuresOrder);
		config.setCategoryPropertyOrder(CATEGORY_TILES, tileOrder);
		config.setCategoryPropertyOrder(CATEGORY_PROPERTIES, propertiesOrder);

		if (config.hasChanged()) {
			config.save();
		}
	}

	private static Object addProperty(String category, String key, Object defaultVal, String comment,
	                                  List<String> order) {
		Property property = null;
		if (defaultVal instanceof Boolean) {
			property = config.get(category, key, (Boolean) defaultVal);
		}
		if (defaultVal instanceof Integer) {
			property = config.get(category, key, (Integer) defaultVal);
		}
		if (defaultVal instanceof Double) {
			property = config.get(category, key, (Double) defaultVal);
		}

		if (property == null) {
			return new Property(null, (String) null, Property.Type.STRING);
		}

		property.setComment(comment);
		order.add(property.getName());

		if (defaultVal instanceof Boolean) {
			return property.getBoolean();
		}
		if (defaultVal instanceof Integer) {
			return property.getInt();
		}
		if (defaultVal instanceof Double) {
			return property.getDouble();
		}

		return 0;
	}

	public static void preInitClient() {

	}

	public static Configuration getConfig() {
		return config;
	}
}

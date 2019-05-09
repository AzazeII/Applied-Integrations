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
    private static Configuration config = null;


    private static final String CATEGORY_FEATURES = "Features";

    public static boolean enableWebServer;
    public static boolean enableEnergyFeatures; // #1
    public static boolean enableManaFeatures; // #2
    public static boolean enableEmberFeatures; // #3
    public static boolean enableStarlightFeatures; // #4

    private static final String CATEGORY_TILES = "tile entities";

    public static boolean enableBlackHoleStorage; // #5
    public static boolean enableMEServer; // #6
    public static boolean enableLogicBus; // #7

    private static final String CATEGORY_PROPERTIES = "Properties";

    public static int interfaceMaxStorage; // #8
    public static IncludeExclude defaultListMode; // #9

    public static int webUIPort;
    public static int maxPylonDistance;
    public static double pylonDrain;


    // Called only on server
    public static void preInit(){
        // Get config file
        File config = new File(Loader.instance().getConfigDir(), AppliedIntegrations.modid+".cfg");

        // Update configurations
        AIConfig.config = new Configuration(config);

        // Sync config
        syncFromFiles();
    }

    // Called only on client
    public static void preInitClient(){

    }

    public static Configuration getConfig(){
        return config;
    }

    private static Object addProperty(String category, String key, Object defaultVal, String comment, List<String> order){
        // Init variable
        Property property = null;

        // Check for def. val signature
        if (defaultVal instanceof Boolean)
            property = config.get(category, key, (Boolean)defaultVal);
        if (defaultVal instanceof Integer)
            property = config.get(category, key, (Integer)defaultVal);
        if (defaultVal instanceof Double)
            property = config.get(category, key, (Double)defaultVal);

        if (property == null)
            return new Property(null, (String) null, Property.Type.STRING);

        // Set comment
        property.setComment(comment);
        // Add to oder
        order.add(property.getName());

        // Check for def. val signature
        if (defaultVal instanceof Boolean)
            return property.getBoolean();
        if (defaultVal instanceof Integer)
            return property.getInt();
        if (defaultVal instanceof Double)
            return property.getDouble();

        return 0;
    }

    public static void syncFromFiles(){
        // Sync
        config.load();

        // Create order list #1
        List<String> featuresOrder = new ArrayList<>();

        // Create order list #2
        List<String> tileOrder = new ArrayList<>();

        // Create order list #3
        List<String> propertiesOrder = new ArrayList<>();

        // Add every property
        // Web server
        enableWebServer = (Boolean)addProperty(CATEGORY_FEATURES, "EnableWebServer", false,
                "Default: false; If set to true, then all web UI features will be enabled; Used only on client side",
                featuresOrder);

        // Energy parts/tiles/items
        enableEnergyFeatures = (Boolean)addProperty(CATEGORY_FEATURES, "EnableEnergyFeatures", true,
                "Default: true; If set to true, then all energy features will be enabled. Not recommended to disable, as it is core feature",
                featuresOrder);

        // Mana parts/tile/items
        enableManaFeatures = (Boolean)addProperty(CATEGORY_FEATURES, "EnableManaFeatures", false,
                "Default: true; If set to true, then all mana features will be enabled.",
                featuresOrder);

        // Ember capability for energy parts and p2p tunnel
        enableEmberFeatures = (Boolean)addProperty(CATEGORY_FEATURES, "EnableEmberFeatures", false,
                "Default: false; If set to true, then all ember features will be enabled.",
                featuresOrder);

        // p2p tunnel starlight
        enableStarlightFeatures = (Boolean)addProperty(CATEGORY_FEATURES, "EnableStarlightTunnel", true,
                "Default: true; If set to true, then starlight p2p tunnel will be available.",
                featuresOrder);

        // Black/white hole storage
        enableBlackHoleStorage = (Boolean)addProperty(CATEGORY_TILES, "EnableBlackHoleStorageSystem", true,
                "Default: true (only in alpha); If set to true, then all black/white hole storage system blocks will be available in game.",
                tileOrder);

        // ME Server
        enableMEServer = (Boolean)addProperty(CATEGORY_TILES, "EnableMEServer", true,
                "Default: true; If set to true, then ME Server blocks will be available in game.",
                tileOrder);

        // Logic bus
        enableLogicBus = (Boolean)addProperty(CATEGORY_TILES, "EnableLogicBus", true,
                "Default: true; If set to true, then Logic bus blocks will be available in game.",
                tileOrder);

        // Max storage of ME energy interface
        interfaceMaxStorage = (Integer)addProperty(CATEGORY_PROPERTIES, "InterfaceStorage", 1000000,
                "Default: 1 000 000 RF; Max capacity of ME Energy interface in RF units (all other units is depend on RF capacity).",
                propertiesOrder);

        // Max distance of pylon
        maxPylonDistance = (Integer)addProperty(CATEGORY_PROPERTIES, "PylonDistance", 97,
                "Default: 97; Max range of ME Pylon's beam",
                propertiesOrder);

        // Energy drain per block of pylon
        pylonDrain = (Double)addProperty(CATEGORY_PROPERTIES, "PylonDrainPerBlock", 20.0D,
                "Default: 20.0D; Active(used only when matter transmitted) energy drain per block of ME pylon's beam. Limit is: " +
                        "10000",
                propertiesOrder);

        webUIPort = (Integer)addProperty(CATEGORY_PROPERTIES, "WebUI Port", 8000,
                "Default: 8000; Port for web UI of network topology",
                propertiesOrder);

        defaultListMode = (Boolean) addProperty(CATEGORY_PROPERTIES, "Default Security Terminal List Mode", false,
                "Default: False (blacklist); If true, then default mode is server security terminal GUI will be white list",
                propertiesOrder) ? IncludeExclude.BLACKLIST : IncludeExclude.WHITELIST;

        // Set order
        config.setCategoryPropertyOrder(CATEGORY_FEATURES, featuresOrder);
        config.setCategoryPropertyOrder(CATEGORY_TILES, tileOrder);
        config.setCategoryPropertyOrder(CATEGORY_PROPERTIES, propertiesOrder);

        // Save if changed
        if(config.hasChanged())
            config.save();
    }
}

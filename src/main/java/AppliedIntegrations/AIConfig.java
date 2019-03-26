package AppliedIntegrations;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AIConfig {
    private static Configuration config = null;


    private static final String CATEGORY_FEATURES = "Features";

    public static boolean enableEnergyFeatures = true; // #1
    public static boolean enableManaFeatures = true; // #2
    public static boolean enablEmberFeatures = true; // #3
    public static boolean enableStarlightFeatures = true; // #4

    private static final String CATEGORY_TILES = "Tile entities";

    public static boolean enableBlackHoleStorage = true; // #5
    public static boolean enableMEServer = true; // #6
    public static boolean enableLogicBus = true; // #7

    private static final String CATEGORY_PROPERTIES = "Properties";
    public static int interfaceMaxStorage = 1000000; // #8


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

    public static void syncFromFiles(){
        // Sync
        config.load();

        // Create energy feature property
        Property propertyEnergyFeatures = config.get(CATEGORY_FEATURES, "EnableEnergyFeatures", true);

        // Add comment
        propertyEnergyFeatures.setComment("Default: true; If set to true, then all energy features will be enabled. Not recommended to disable, as it is core feature");


        // Create mana feature property
        Property propertyManaFeatures = config.get(CATEGORY_FEATURES, "EnableManaFeatures", true);

        // Add comment
        propertyManaFeatures.setComment("Default: true; If set to true, then all mana features will be enabled.");


        // Create ember feature property
        Property propertyEmberFeatures = config.get(CATEGORY_FEATURES, "EnableEmberFeatures", false);

        // Add comment
        propertyEmberFeatures.setComment("Default: false; If set to true, then all ember features will be enabled.");


        // Create starlight p2p tunnel property
        Property propertyStarlightTunnel = config.get(CATEGORY_FEATURES, "EnableStarlightTunnel", true);

        // Add comment
        propertyStarlightTunnel.setComment("Default: true; If set to true, then starlight p2p tunnel will be available.");


        // Create black/white hole storage system property
        Property propertyBlackHoleStorageSystem = config.get(CATEGORY_TILES, "EnableBlackHoleStorageSystem", true);

        // Add comment
        propertyBlackHoleStorageSystem.setComment("Default: true (only in alpha); If set to true, then all black/white storage system blocks will be available in game.");


        // Create me server property
        Property propertyMEServer = config.get(CATEGORY_TILES, "EnableMEServer", true);

        // Add comment
        propertyMEServer.setComment("Default: true; If set to true, then ME Server blocks will be available in game.");


        // Create me server property
        Property propertyLogicBus = config.get(CATEGORY_TILES, "EnableLogicBus", true);

        // Add comment
        propertyLogicBus.setComment("Default: true; If set to true, then Logic bus blocks will be available in game.");


        // Create interface storage property
        Property propertyInterfaceStorage = config.get(CATEGORY_PROPERTIES, "InterfaceStorage", 1000000);

        // Add comment
        propertyInterfaceStorage.setComment("Default: 1 000 000 RF; Max capacity of ME Energy interface in RF units (all other units is depend on RF capacity).");


        // Create order list #1
        List<String> featuresOrder = new ArrayList<>();

        // Create order list #2
        List<String> tileOrder = new ArrayList<>();

        // Create order list #3
        List<String> propertiesOrder = new ArrayList<>();

        // Sort all properties
        featuresOrder.add(propertyEnergyFeatures.getName());
        featuresOrder.add(propertyManaFeatures.getName());
        featuresOrder.add(propertyEmberFeatures.getName());
        featuresOrder.add(propertyStarlightTunnel.getName());

        tileOrder.add(propertyBlackHoleStorageSystem.getName());
        tileOrder.add(propertyMEServer.getName());
        tileOrder.add(propertyLogicBus.getName());

        propertiesOrder.add(propertyInterfaceStorage.getName());

        // Set actual values
        enableEnergyFeatures = propertyEnergyFeatures.getBoolean();
        enableManaFeatures = propertyManaFeatures.getBoolean();
        enablEmberFeatures = propertyEmberFeatures.getBoolean();
        enableStarlightFeatures = propertyStarlightTunnel.getBoolean();

        enableBlackHoleStorage = propertyBlackHoleStorageSystem.getBoolean();
        enableMEServer = propertyMEServer.getBoolean();
        enableLogicBus = propertyLogicBus.getBoolean();

        interfaceMaxStorage = propertyInterfaceStorage.getInt();

        // Set order
        config.setCategoryPropertyOrder(CATEGORY_FEATURES, featuresOrder);
        config.setCategoryPropertyOrder(CATEGORY_TILES, tileOrder);
        config.setCategoryPropertyOrder(CATEGORY_PROPERTIES, propertiesOrder);

        // Save if changed
        if(config.hasChanged())
            config.save();
    }
}

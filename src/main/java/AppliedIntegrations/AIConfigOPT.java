package AppliedIntegrations;

import AppliedIntegrations.Utils.AILog;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class AIConfigOPT {

    public static int interfaceMaxStorage;
    public static boolean loadMeTurret;

    public static int IMS_Default = 1000000;
    public static final String IMS_Name = "Max storage of energy interface in RF units, all other units is determined by RF";
    public static final String METURRET = "Does ME Turret and tile singularity enabled?";

    public static int interfaceMaxTransfer;

    public static int IObaseTransfer;
    public static int IOaddTransfer;
    public static boolean advancedStorage;
    public static boolean botaniaIntegrations;

    public static void syncMe(){
        // Register event bus
        FMLCommonHandler.instance().bus().register(AppliedIntegrations.instance);
        // Get category
        final String category = AppliedIntegrations.AIConfig.CATEGORY_GENERAL + AppliedIntegrations.AIConfig.CATEGORY_SPLITTER + "Parameters";
        // Add category
        AppliedIntegrations.AIConfig.addCustomCategoryComment(category,"Parameters of machines in mod");

        // Sync interface max storage
        interfaceMaxStorage = AppliedIntegrations.AIConfig.get(category, IMS_Name,IMS_Default).getInt();
        // Sync me turret loaded
        loadMeTurret = AppliedIntegrations.AIConfig.get(category, METURRET, false).getBoolean();

        if(AppliedIntegrations.AIConfig.hasChanged()){
            AppliedIntegrations.AIConfig.save();
        }
    }


}

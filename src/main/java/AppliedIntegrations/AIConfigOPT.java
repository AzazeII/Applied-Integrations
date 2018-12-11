package AppliedIntegrations;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Configuration;
import scala.App;

import java.io.File;

public class AIConfigOPT {

    public static int interfaceMaxStorage;
    public static int IMS_Default = 1000000;
    public static final String IMS_Name = "Max storage of energy interface in RF units, all other units is determined by RF";

    public static int interfaceMaxTransfer;

    public static int IObaseTransfer;
    public static int IOaddTransfer;
    public static boolean advancedStorage;
    public static boolean botaniaIntegrations;

    public static void syncMe(){
        FMLCommonHandler.instance().bus().register(AppliedIntegrations.instance);
        final String param = AppliedIntegrations.AIConfig.CATEGORY_GENERAL + AppliedIntegrations.AIConfig.CATEGORY_SPLITTER + "Parameters";
        AppliedIntegrations.AIConfig.addCustomCategoryComment(param,"Parameters of machines in mod");
        interfaceMaxStorage = AppliedIntegrations.AIConfig.get(param, IMS_Name,IMS_Default).getInt();

        if(AppliedIntegrations.AIConfig.hasChanged()){
            AppliedIntegrations.AIConfig.save();
        }
    }


}

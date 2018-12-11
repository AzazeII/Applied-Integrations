package AppliedIntegrations.Gui;

import AppliedIntegrations.AppliedIntegrations;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import java.util.List;

public class AIConfigGui extends GuiConfig {

    public AIConfigGui(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(AppliedIntegrations.AIConfig.
                getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                "AppliedIntegrations",false, false,
                GuiConfig.getAbridgedConfigPath(AppliedIntegrations.AIConfig.toString()));
    }
}

package AppliedIntegrations.Gui;

import AppliedIntegrations.AppliedIntegrations;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class AIConfigGui extends GuiConfig {

    public AIConfigGui(GuiScreen parentScreen) {
        super(parentScreen, new ConfigElement(AppliedIntegrations.AIConfig.
                getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                "AppliedIntegrations",false, false,
                GuiConfig.getAbridgedConfigPath(AppliedIntegrations.AIConfig.toString()));
    }
}

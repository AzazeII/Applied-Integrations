package AppliedIntegrations.Gui.Buttons;

import AppliedIntegrations.Gui.Widgets.AIWidget;
import appeng.api.config.IncludeExclude;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiListTypeButton extends AIGuiButton{

    private IncludeExclude mode = IncludeExclude.BLACKLIST;

    public GuiListTypeButton(int ID, int xPosition, int yPosition, int width, int height, String text) {
        super(ID, xPosition, yPosition, width, height, text);
    }

    @Override
    public void getTooltip(List<String> tip) {
        // Add state
        tip.add("");
    }

    public void toggleMode() {
        // Simply check if mode is black list and change to **opposite**
        if (mode == IncludeExclude.BLACKLIST) // **(1)
            mode = IncludeExclude.WHITELIST; // **(2)
        else  // **(3)
            mode = IncludeExclude.BLACKLIST; // **(4)
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        // Disable lighting
        GL11.glDisable( GL11.GL_LIGHTING );

        // Full white
        GL11.glColor3f( 1.0F, 1.0F, 1.0F );

        // Bind to the gui texture
        Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png" ) );

        // Draw background of button
        drawTexturedModalRect( x, y, backgroundU, backgroundV, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE );

        // Draw foreground of button
        drawTexturedModalRect( x, y-2, 16 * (2 + mode.ordinal()), 16 * 8 , AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE );

        // Re-enable lighting
        GL11.glEnable( GL11.GL_LIGHTING );
    }
}

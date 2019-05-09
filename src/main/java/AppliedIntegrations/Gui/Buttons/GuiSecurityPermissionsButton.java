package AppliedIntegrations.Gui.Buttons;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import appeng.api.config.SecurityPermissions;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiSecurityPermissionsButton extends AIGuiButton {
    private SecurityPermissions currentPermissions = SecurityPermissions.INJECT;

    public GuiSecurityPermissionsButton(int ID, int xPosition, int yPosition, int width, int height, String text) {
        super(ID, xPosition, yPosition, width, height, text);
    }

    private int getV() {
        // Y of security permissions stroke
        return 11 * 16;
    }

    private int getU() {
        return 16 * currentPermissions.ordinal();
    }


    // Mode cycle with next scheme:
    //  Inject => Extract => Craft =>|
    //     |<=========================
    public void cycleMode() {
        // Check for last mode in cycle
        if (currentPermissions == SecurityPermissions.CRAFT)
            // Make first
            currentPermissions = SecurityPermissions.INJECT;
        else
            // Make next
            currentPermissions = SecurityPermissions.values()[currentPermissions.ordinal() + 1];
    }

    @Override
    public void getTooltip(List<String> tip) {
        // Add name
        tip.add("Security Permissions");

        // Add current value
        tip.add(I18n.format(currentPermissions.getUnlocalizedTip()));
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
        drawTexturedModalRect( x, y-2, getU(), getV() , AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE );

        // Re-enable lighting
        GL11.glEnable( GL11.GL_LIGHTING );
    }
}

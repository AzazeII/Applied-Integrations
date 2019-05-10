package AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons;

import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import appeng.api.config.SecurityPermissions;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static appeng.api.config.SecurityPermissions.*;

public class GuiSecurityPermissionsButton extends GuiServerButton {
    private SecurityPermissions currentPermissions = INJECT;
    private static final List<SecurityPermissions> allowedPermissions = new ArrayList<>();

    static {
        allowedPermissions.add(INJECT);
        allowedPermissions.add(EXTRACT);
        allowedPermissions.add(CRAFT);
    }

    public GuiSecurityPermissionsButton(GuiServerTerminal terminal, int ID, int xPosition, int yPosition, int width, int height, String text) {
        super(terminal, ID, xPosition, yPosition, width, height, text);
    }

    private int getV() {
        // Y of security permissions stroke
        return 11 * 16;
    }


    private int getU() {
        return 16 * currentPermissions.ordinal();
    }

    public static List<SecurityPermissions> getPermissionList() {
        return allowedPermissions;
    }

    public SecurityPermissions getCurrentPermissions() {
        return currentPermissions;
    }


    // Mode cycle with next scheme:
    //  Inject => Extract => Craft =>|
    //     |<=========================
    public void cycleMode() {
        // Check for last mode in cycle
        if (currentPermissions == CRAFT)
            // Make first
            currentPermissions = INJECT;
        else
            // Make next
            currentPermissions = SecurityPermissions.values()[currentPermissions.ordinal() + 1];
    }

    @Override
    public void getTooltip(List<String> tip) {
        // Check if container has no network tool in slot
        if (!host.isCardValid())
            return;

        // Add name as header
        tip.add(I18n.format(currentPermissions.getUnlocalizedName()));

        // Add current value
        tip.add(I18n.format(currentPermissions.getUnlocalizedTip()));
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        // Check if host GUI has no card
        if (!host.isCardValid())
            return;

        // Disable lighting
        GL11.glDisable( GL11.GL_LIGHTING );

        // Full white
        GL11.glColor3f( 1.0F, 1.0F, 1.0F );

        // Bind to the gui texture
        Minecraft.getMinecraft().renderEngine.bindTexture( new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png" ) );

        // Draw background of button
        drawTexturedModalRect( x, y, backgroundU, backgroundV, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE - 2 );

        // Draw foreground of button
        drawTexturedModalRect( x, y, getU(), getV() , AIWidget.WIDGET_SIZE - 2, AIWidget.WIDGET_SIZE - 2 );

        // Re-enable lighting
        GL11.glEnable( GL11.GL_LIGHTING );
    }
}

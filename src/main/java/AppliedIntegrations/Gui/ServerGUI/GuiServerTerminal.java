package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.Container.tile.Server.ContainerServerTerminal;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Buttons.GuiSecurityPermissionsButton;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.client.gui.implementations.GuiSecurityStation;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


// TODO Rewrite this GUI, now it will be similar to normal security terminal.
// Instead of biometric cards it will accept networks cards.
/**
 * @Author Azazell
 */
public class GuiServerTerminal extends AIBaseGui {

    private static final int GUI_WIDTH = 192;
    private static final int GUI_HEIGH = 256;

    private GuiSecurityPermissionsButton securityPermissionButton;
    private ResourceLocation texture = new ResourceLocation(AppEng.MOD_ID , "textures/guis/security_station.png");

    public TileServerCore mInstance;
    public EntityPlayer player;

    public GuiServerTerminal(ContainerServerTerminal container, EntityPlayer player) {
        super(container, player);

        this.player = player;
    }

    @Override
    public ISyncHost getSyncHost() {
        return mInstance;
    }

    @Override
    public void setSyncHost(ISyncHost host) {
        if(host instanceof TileServerCore)
            mInstance = (TileServerCore)host;
    }

    @Override
    public void initGui() {
        super.initGui();

        // Add new toggle button to button list
        buttonList.add(securityPermissionButton = new GuiSecurityPermissionsButton( 0, this.guiLeft - 18, this.guiTop + 8, 16, 16, ""));
    }

    @Override
    public void onButtonClicked(final GuiButton btn, final int mouseButton) {
        // Check if button is security permissions button
        if (btn == securityPermissionButton){
            // Cycle mode of button
            securityPermissionButton.cycleMode();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY) {
        // Pass call to default function
        drawDefaultBackground();

        // Set color
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Bind our texture
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        // Draw bottom(below cell bar) texture
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGH);
    }

    @Override
    protected void drawGuiContainerForegroundLayer( final int mouseX, final int mouseY ) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        // Draw gui strings
        this.fontRenderer.drawString("Server Security Terminal", 8, 6, 4210752); // (Name)
        this.fontRenderer.drawString("Network Card Editor", 8, this.ySize - 96 - 32, 4210752); // (Editor)
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752); // (Player inv.)
    }
}
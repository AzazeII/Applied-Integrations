package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.Server.ContainerMEServer;
import AppliedIntegrations.Gui.AIBaseGui;
import appeng.core.localization.GuiText;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @Author Azazell
 */
public class GuiMEServer extends AIBaseGui {
    private static final ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server/server_storage.png");

    public GuiMEServer(ContainerMEServer container, EntityPlayer p) {
        super(container, p);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop-15, 0, 0, 210, 200);
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        // Draw string
        this.fontRenderer.drawString("ME Server Drive", 9, -12, 4210752); // (Server drive inv)
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 18, 4210752); // (Player inv.)

    }


    @Override
    public ISyncHost getSyncHost() {
        return null;
    }

    @Override
    public void setSyncHost(ISyncHost host) {

    }
}

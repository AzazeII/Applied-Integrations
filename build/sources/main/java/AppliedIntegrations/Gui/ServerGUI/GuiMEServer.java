package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.Server.ContainerMEServer;
import AppliedIntegrations.Gui.AIBaseGui;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiMEServer extends AIBaseGui {
    private static final ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/Server/ServerStorage.png");

    public GuiMEServer(ContainerMEServer container) {
        super(container);
    }

    @Override
    public AIContainer getNodeContainer() {
        return null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop-15, 0, 0, 200, 200);
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRenderer.drawString("ME Server Drive", 9, -12, 4210752);
    }
}

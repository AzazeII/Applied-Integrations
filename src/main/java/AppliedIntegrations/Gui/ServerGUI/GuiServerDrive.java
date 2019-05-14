package AppliedIntegrations.Gui.ServerGUI;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.api.ISyncHost;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiServerDrive extends AIBaseGui {
    private static final ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/server_cell_storage.png");

    public GuiServerDrive(Container container, EntityPlayer player) {
        super(container, player);
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
    }


    @Override
    public ISyncHost getSyncHost() {
        return null;
    }

    @Override
    public void setSyncHost(ISyncHost host) {

    }
}

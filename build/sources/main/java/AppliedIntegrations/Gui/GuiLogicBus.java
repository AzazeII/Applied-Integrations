package AppliedIntegrations.Gui;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.ContainerLogicBus;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.tile.LogicBus.TileLogicBusCore;
import AppliedIntegrations.tile.TileEnergyInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

public class GuiLogicBus extends AIBaseGui{

    public final ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/logic_bus.png");

    public GuiLogicBus(EntityPlayer player, TileLogicBusCore maybeCore, ContainerLogicBus container) {
        super(container);
    }

    @Override
    public AIContainer getNodeContainer() {
        return null;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        // Call super (literally)
        drawDefaultBackground();
        // Set neutral color
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Bind correct texture
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        // Draw main gui background
        drawTexturedModalRect(this.guiLeft, this.guiTop-41, 0, 0, this.xSize, this.ySize + 90);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Drawing Name
        this.fontRenderer.drawString(I18n.translateToLocal("ME Logic Bus Pattern Storage"), 9, 3-41, 4210752);
    }
}
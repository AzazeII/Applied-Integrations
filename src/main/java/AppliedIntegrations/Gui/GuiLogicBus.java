package AppliedIntegrations.Gui;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.tile.ContainerLogicBus;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.LogicBus.TileLogicBusCore;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

/**
 * @Author Azazell
 */

public class GuiLogicBus extends AIGui {
	public final ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/logic_bus.png");

	public GuiLogicBus(EntityPlayer player, TileLogicBusCore maybeCore, ContainerLogicBus container) {

		super(container, player);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		drawTexturedModalRect(this.guiLeft, this.guiTop - 41, 0, 0, this.xSize, this.ySize + 90);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(I18n.translateToLocal("ME Logic Bus Pattern Storage"), 9, 3 - 41, 4210752);
	}

	@Override
	public ISyncHost getSyncHost() {
		return null;
	}

	@Override
	public void setSyncHost(ISyncHost host) {

	}
}

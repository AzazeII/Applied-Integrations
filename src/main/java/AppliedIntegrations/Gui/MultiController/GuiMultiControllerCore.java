package AppliedIntegrations.Gui.MultiController;


import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerCore;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Widgets.WidgetScrollbar;
import AppliedIntegrations.api.ISyncHost;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * @Author Azazell
 */
public class GuiMultiControllerCore extends AIBaseGui {
	private static final ResourceLocation texture = new ResourceLocation(AppEng.MOD_ID, "textures/guis/terminal.png");

	private final WidgetScrollbar scroll = new WidgetScrollbar(this, 175, 18);

	public GuiMultiControllerCore(ContainerMultiControllerCore container, EntityPlayer p) {
		super(container, p);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		// Draw default
		drawDefaultBackground();

		// Set color
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		// Bind texture
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		// Draw texture
		drawTexturedModalRect(this.guiLeft, this.guiTop - 15, 0, 0, 195, 200);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		// Draw string
		this.fontRenderer.drawString(GuiText.inventory.getLocal(), 7, this.ySize - 108, 4210752); // (Player inv.)
		this.fontRenderer.drawString("ME Network Card Drive", 7, -12, 4210752); // (Server drive inv)

		// Draw scroll bar
		scroll.drawWidget();
	}


	@Override
	public ISyncHost getSyncHost() {
		return null;
	}

	@Override
	public void setSyncHost(ISyncHost host) {

	}
}

package AppliedIntegrations.Gui.Part;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerInteractionPlane;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Parts.Interaction.PartInteractionPlane;
import AppliedIntegrations.api.ISyncHost;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.opengl.GL11;

/**
 * @Author Azazell
 */
public class GuiInteractionPlane extends AIGui {
	private static final ResourceLocation texture = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/interaction.bus.png");
	private PartInteractionPlane interaction;

	public GuiInteractionPlane(ContainerInteractionPlane container, EntityPlayer player, PartInteractionPlane interaction) {
		super(container, player);
		this.interaction = interaction;
	}

	@Override
	public ISyncHost getSyncHost() {
		return interaction;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		this.interaction = (PartInteractionPlane) host;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		// Render bound texture
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize + 75);
		drawTexturedModalRect(this.guiLeft + GUI_MAIN_WIDTH, this.guiTop, GUI_MAIN_WIDTH, 0, GUI_UPGRADES_WIDTH, GuiEnergyIO.GUI_UPGRADES_HEIGHT);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);

		this.fontRenderer.drawString(I18n.translateToLocal("ME Interaction Plane"), 9, 3, 4210752);
	}
}

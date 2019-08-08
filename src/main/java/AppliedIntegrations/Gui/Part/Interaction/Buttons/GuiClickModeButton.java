package AppliedIntegrations.Gui.Part.Interaction.Buttons;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerInteractionBus;
import AppliedIntegrations.Gui.Buttons.AIGuiButton;
import AppliedIntegrations.Gui.Part.Interaction.GuiInteractionBus;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketClickModeClientToServer;
import AppliedIntegrations.Parts.Interaction.PartInteraction;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @Author Azazell
 */
public class GuiClickModeButton extends AIGuiButton {
	private static final ResourceLocation states = new ResourceLocation(AppliedIntegrations.modid, "textures/gui/states.png");
	private final PartInteraction bus;
	private final ContainerInteractionBus container;

	public GuiClickModeButton(GuiInteractionBus owner, int ID, int xPosition, int yPosition, int width, int height, String text) {
		super(ID, xPosition, yPosition, width, height, text);
		this.bus = owner.interaction;
		this.container = owner.getContainer();
	}

	public void cycleMode() {
		// Toggle click mode
		if (container.mode == ClickMode.CLICK) {
			container.mode = ClickMode.SHIFT_CLICK;
		} else {
			container.mode = ClickMode.CLICK;
		}

		NetworkHandler.sendToServer(new PacketClickModeClientToServer(container.mode, bus));
	}

	@Override
	public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		// Draw background
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png"));
		drawTexturedModalRect(x, y, backgroundU, backgroundV, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE - 2);

		// Draw foreground
		Minecraft.getMinecraft().renderEngine.bindTexture(states);
		drawTexturedModalRect(x, y, 16 * 3, (container.mode == ClickMode.SHIFT_CLICK ? 16 : 0), 16, 16);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void getTooltip(List<String> tooltip) {
		tooltip.add("Interaction bus click mode");
		tooltip.add(container.mode.tip);
	}
}

package AppliedIntegrations.Gui.MultiController.SubGui.Buttons;


import AppliedIntegrations.Gui.MultiController.GuiMultiControllerTerminal;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import appeng.api.config.IncludeExclude;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiListTypeButton extends GuiMultiControllerButton {

	private IncludeExclude mode;

	public GuiListTypeButton(GuiMultiControllerTerminal terminal, int ID, int xPosition, int yPosition, int width, int height, String text) {

		super(terminal, ID, xPosition, yPosition, width, height, text);
	}

	@Override
	public void getTooltip(List<String> tip) {
		// Check if container has no network tool in slot
		if (!host.isCardValid()) {
			return;
		}

		// Add header
		tip.add("List Mode");

		// Add state
		tip.add(mode.name());
	}

	public void toggleMode() {
		// Simply check if mode is black list and change to **opposite**
		if (mode == IncludeExclude.BLACKLIST) { // **(1)
			mode = IncludeExclude.WHITELIST; // **(2)
		} else { // **(3)
			mode = IncludeExclude.BLACKLIST; // **(4)
		}

		// Notify host
		host.setIncludeExcludeMode(mode);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		// Check if host GUI has no card
		if (!host.isCardValid()) {
			return;
		}

		// Update current mode
		mode = host.getIncludeExcludeMode();

		// Disable lighting
		GL11.glDisable(GL11.GL_LIGHTING);

		// Full white
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		// Bind to the gui texture
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png"));

		// Draw background of button
		drawTexturedModalRect(x, y, backgroundU, backgroundV, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE - 2);

		// Draw foreground of button
		drawTexturedModalRect(x, y, getU(), getV(), AIWidget.WIDGET_SIZE - 2, AIWidget.WIDGET_SIZE - 2);

		// Re-enable lighting
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private int getU() {

		return 16 * (2 + mode.ordinal());
	}

	private int getV() {

		return 16 * 8;
	}
}

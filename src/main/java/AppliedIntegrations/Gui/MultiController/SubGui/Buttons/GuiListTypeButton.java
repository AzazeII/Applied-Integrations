package AppliedIntegrations.Gui.MultiController.SubGui.Buttons;


import AppliedIntegrations.Gui.MultiController.GuiMultiControllerTerminal;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import appeng.api.config.IncludeExclude;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @Author Azazell
 */
public class GuiListTypeButton extends GuiMultiControllerButton {
	private IncludeExclude mode;

	public GuiListTypeButton(GuiMultiControllerTerminal terminal, int ID, int xPosition, int yPosition, int width, int height, String text) {
		super(terminal, ID, xPosition, yPosition, width, height, text);
	}

	@Override
	public void getTooltip(List<String> tip) {
		if (!host.isCardValid()) {
			return;
		}

		tip.add("List Mode");
		tip.add(mode.name());
	}

	public void toggleMode() {
		if (mode == IncludeExclude.BLACKLIST) {
			mode = IncludeExclude.WHITELIST;
		} else {
			mode = IncludeExclude.BLACKLIST;
		}

		host.setIncludeExcludeMode(mode);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!host.isCardValid()) {
			return;
		}

		mode = host.getIncludeExcludeMode();

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png"));

		drawTexturedModalRect(x, y, backgroundU, backgroundV, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE - 2);
		drawTexturedModalRect(x, y, getU(), getV(), AIWidget.WIDGET_SIZE - 2, AIWidget.WIDGET_SIZE - 2);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private int getU() {
		return 16 * (2 + mode.ordinal());
	}

	private int getV() {
		return 16 * 8;
	}
}

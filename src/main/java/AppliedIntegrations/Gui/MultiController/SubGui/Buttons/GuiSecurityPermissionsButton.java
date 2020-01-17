package AppliedIntegrations.Gui.MultiController.SubGui.Buttons;


import AppliedIntegrations.Gui.MultiController.GuiMultiControllerTerminal;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import appeng.api.config.SecurityPermissions;
import appeng.core.AppEng;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static appeng.api.config.SecurityPermissions.*;

/**
 * @Author Azazell
 */
public class GuiSecurityPermissionsButton extends GuiMultiControllerButton {
	private static final List<SecurityPermissions> allowedPermissions = new ArrayList<>();

	private SecurityPermissions currentPermissions = INJECT;

	static {
		allowedPermissions.add(INJECT);
		allowedPermissions.add(EXTRACT);
		allowedPermissions.add(CRAFT);
	}

	public GuiSecurityPermissionsButton(GuiMultiControllerTerminal terminal, int ID, int xPosition, int yPosition, int width, int height, String text) {
		super(terminal, ID, xPosition, yPosition, width, height, text);
	}

	public static List<SecurityPermissions> getPermissionList() {
		return allowedPermissions;
	}

	public SecurityPermissions getCurrentPermissions() {

		return currentPermissions;
	}

	// Inject => Extract => Craft
	public void cycleMode() {
		if (currentPermissions == CRAFT) {
			currentPermissions = INJECT;
		} else {
			currentPermissions = SecurityPermissions.values()[currentPermissions.ordinal() + 1];
		}
	}

	@Override
	public void getTooltip(List<String> tip) {
		if (!host.isCardValid()) {
			return;
		}

		tip.add(I18n.format(currentPermissions.getUnlocalizedName()));
		tip.add(I18n.format(currentPermissions.getUnlocalizedTip()));
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!host.isCardValid()) {
			return;
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppEng.MOD_ID, "textures/guis/states.png"));
		drawTexturedModalRect(x, y, backgroundU, backgroundV, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE - 2);
		drawTexturedModalRect(x, y, getU(), getV(), AIWidget.WIDGET_SIZE - 2, AIWidget.WIDGET_SIZE - 2);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private int getU() {
		return 16 * currentPermissions.ordinal();
	}

	private int getV() {
		return 11 * 16;
	}
}

package AppliedIntegrations.Gui.Widgets;

import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public abstract class AIWidget extends Gui {
	/**
	 * The width and height of the energy slot!
	 */
	public static final int WIDGET_SIZE = 18;

	protected int xPosition;

	protected int yPosition;

	protected IWidgetHost hostGUI;

	public AIWidget(final IWidgetHost hostGUI, final int xPos, final int yPos) {
		this.hostGUI = hostGUI;

		this.xPosition = xPos;

		this.yPosition = yPos;
	}

	public void drawMouseHoverUnderlay() {
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		this.drawGradientRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + 17, this.yPosition + 17, 0x80FFFFFF, 0x80FFFFFF);

		GL11.glEnable(GL11.GL_LIGHTING);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public abstract void drawWidget();

	public abstract void getTooltip(List<String> tooltip);

	public boolean isMouseOverWidget(final int mouseX, final int mouseY) {
		return AIGuiHelper.INSTANCE.isPointInGuiRegion(this.yPosition, this.xPosition, AIWidget.WIDGET_SIZE - 1, AIWidget.WIDGET_SIZE - 1, mouseX, mouseY, this.hostGUI.getLeft(), this.hostGUI.getTop());
	}

	/**
	 * Called when the mouse is clicked on the widget.
	 */
	public abstract void onMouseClicked();
}

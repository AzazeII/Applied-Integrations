package AppliedIntegrations.Gui.Widgets;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import appeng.client.gui.widgets.IScrollSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class WidgetScrollbar extends AIWidget implements IScrollSource {
	private int currentScroll;

	// Minimum scroll value for widget
	private int minScroll = 0;

	// Maximum scroll value for widget
	private int maxScroll;
	
	public WidgetScrollbar(IWidgetHost hostGUI, int xPos, int yPos) {
		super(hostGUI, xPos, yPos);
	}

	private void applyRange() {
		this.currentScroll = Math.max( Math.min( this.currentScroll, maxScroll ), minScroll );
	}

	public void onWheel(int diff) {
		// Calculate difference
		diff = Math.max( Math.min( -diff, 1 ), -1 );

		// Change current scroll value
		currentScroll += diff;

		// See function name
		applyRange();
	}

	@Override
	public void drawWidget() {
		// Disable lighting
		GL11.glDisable(GL11.GL_LIGHTING);

		// Full white
		GL11.glColor3f(1.0F, 1.0F, 1.0F);

		// Bind texture of scrollbar in creative tab
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("gui/container/creative_inventory/tabs.png"));

		// Nullify color
		GlStateManager.color( 1.0f, 1.0f, 1.0f, 1.0f );

		// Draw bound texture
		drawTexturedModalRect(xPosition, yPosition, 244, 0, 12, 15);

		// Re-enable lighting
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	public void setMaxScroll(int maxScroll) {
		this.maxScroll = maxScroll;
	}

	@Override
	public void getTooltip(List<String> tooltip) {
		// Ignored
	}

	@Override
	public int getCurrentScroll() {
		return currentScroll;
	}
}

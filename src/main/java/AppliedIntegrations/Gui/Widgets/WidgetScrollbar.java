package AppliedIntegrations.Gui.Widgets;


import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import appeng.client.gui.widgets.IScrollSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

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
		// Bind texture of scrollbar in creative tab
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("minecraft",
				"gui/container/creative_inventory/tabs.png"));

		// Nullify color
		GlStateManager.color( 1.0f, 1.0f, 1.0f, 1.0f );

		// Check not zero
		if (maxScroll - minScroll != 0) {
			// Calculate offset for drawing
			final int offset = (this.currentScroll - this.minScroll) / (maxScroll - minScroll);

			// Draw bound texture
			drawTexturedModalRect(xPosition, offset + yPosition, 232, 0, 12, 15);
		} else {
			// Draw bound texture
			drawTexturedModalRect(xPosition, yPosition, 244, 0, 12, 15);
		}
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

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
	private int minScroll;

	// Maximum scroll value for widget
	private int maxScroll;

	public WidgetScrollbar(IWidgetHost hostGUI, int xPos, int yPos) {
		super(hostGUI, xPos, yPos);
	}

	@Override
	public void drawWidget() {
		// Bind texture of scrollbar in creative tab
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("minecraft",
				"gui/container/creative_inventory/tabs.png"));

		// Nullify color
		GlStateManager.color( 1.0f, 1.0f, 1.0f, 1.0f );

		// Draw texture
		drawTexturedModalRect( xPosition, yPosition, 232, 0, 12, 15 );
	}

	@Override
	public void getTooltip(List<String> tooltip) {
		// Ignored
	}

	private void applyRange() {
		this.currentScroll = Math.max( Math.min( this.currentScroll, maxScroll ), minScroll );
	}

	public void onWheel(int diff) {
		// Calculate difference
		diff = Math.max( Math.min( -diff, 1 ), -1 );

		// Change current scroll
		currentScroll += diff;

		// See function name
		applyRange();
	}

	@Override
	public int getCurrentScroll() {
		return currentScroll;
	}
}

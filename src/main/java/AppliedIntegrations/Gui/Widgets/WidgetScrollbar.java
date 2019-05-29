package AppliedIntegrations.Gui.Widgets;


import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class WidgetScrollbar extends AIWidget {
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
		drawTexturedModalRect( xPosition, yPosition, 244, 0, 12, 15 );
	}

	@Override
	public void getTooltip(List<String> tooltip) {
		// Ignored
	}
}

package AppliedIntegrations.Gui.Widgets;


import AppliedIntegrations.Gui.AIBaseGui;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiScrollbar;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAIScrollbar extends GuiScrollbar {
	// Draw wrapper for AI guis
	public void draw(AIBaseGui g) {
		super.draw(new AEBaseGui(g.inventorySlots) {
			@Override
			public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
				// Pass call to given GUI
				g.drawTexturedModalRect(x, y, textureX, textureY, width, height);
			}

			@Override
			public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
				// Ignored
			}

			@Override
			public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
				// Ignored
			}

			@Override
			protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
				// Ignored
			}
		});
	}
}

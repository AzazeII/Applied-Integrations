package AppliedIntegrations.Gui.Widgets;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.IEnergySelectorGui;
import AppliedIntegrations.api.Storage.EnergyStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class WidgetEnergySelector extends EnergyWidget {
	// True by default
	private boolean shouldRender = true;

	public WidgetEnergySelector(IEnergySelectorGui hostGUI, int xPos, int yPos) {

		super(hostGUI, xPos, yPos);
	}

	@Override
	public void onMouseClicked(@Nonnull EnergyStack energy) {
		// Get widget host
		IEnergySelectorGui selector = (IEnergySelectorGui) hostGUI;

		// Update energy of selector
		selector.setSelectedEnergy(energy.getEnergy());

		// Update energy amount of selector
		selector.setAmount(energy.getStackSize());
	}

	@Override
	public void drawWidget() {
		if (shouldRender) {
			// Disable lighting
			GL11.glDisable(GL11.GL_LIGHTING);

			// Full white
			GL11.glColor3f(1.0F, 1.0F, 1.0F);

			// Bind to the gui texture
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.io.bus.png"));

			// Draw this slot just like the center slot of the gui
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 79, 39, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE);

			// Check not null
			if (getCurrentStack() != null) {
				// Draw the Energy
				this.drawEnergy();

				// Check if energy of stack isn't null and this is currently selected widget
				if (getCurrentStack().getEnergy() != null && ((IEnergySelectorGui) hostGUI).getSelectedEnergy() == getCurrentStack().getEnergy()) {
					// Bind to the gui texture
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppliedIntegrations.modid, "textures/gui/slots/selection.png"));

					// Size of overlay edges
					final int size = 1;

					// Draw edges of selection
					this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, size, WIDGET_SIZE); // (1)
					this.drawTexturedModalRect(this.xPosition + WIDGET_SIZE - size, this.yPosition, 0, 0, size, WIDGET_SIZE); // (2)
					this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, WIDGET_SIZE, size); // (3)
					this.drawTexturedModalRect(this.xPosition, this.yPosition + WIDGET_SIZE - size, 0, 0, WIDGET_SIZE, size); // (4)
				}
			}

			// Re-enable lighting
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}
}

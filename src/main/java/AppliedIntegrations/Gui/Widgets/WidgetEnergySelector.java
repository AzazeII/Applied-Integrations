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
	private boolean shouldRender = true;

	public WidgetEnergySelector(IEnergySelectorGui hostGUI, int xPos, int yPos) {

		super(hostGUI, xPos, yPos);
	}

	@Override
	public void onMouseClicked(@Nonnull EnergyStack energy) {
		IEnergySelectorGui selector = (IEnergySelectorGui) hostGUI;
		selector.setSelectedEnergy(energy.getEnergy());
		selector.setAmount(energy.getStackSize());
	}

	@Override
	public void drawWidget() {
		if (shouldRender) {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor3f(1.0F, 1.0F, 1.0F);
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppliedIntegrations.modid, "textures/gui/energy.io.bus.png"));
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 79, 39, AIWidget.WIDGET_SIZE, AIWidget.WIDGET_SIZE);
			if (getCurrentStack() != null) {
				this.drawEnergy();
				if (getCurrentStack().getEnergy() != null && ((IEnergySelectorGui) hostGUI).getSelectedEnergy() == getCurrentStack().getEnergy()) {
					Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(AppliedIntegrations.modid, "textures/gui/slots/selection.png"));
					final int size = 1;

					this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, size, WIDGET_SIZE); // (1)
					this.drawTexturedModalRect(this.xPosition + WIDGET_SIZE - size, this.yPosition, 0, 0, size, WIDGET_SIZE); // (2)
					this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, WIDGET_SIZE, size); // (3)
					this.drawTexturedModalRect(this.xPosition, this.yPosition + WIDGET_SIZE - size, 0, 0, WIDGET_SIZE, size); // (4)
				}
			}

			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}
}

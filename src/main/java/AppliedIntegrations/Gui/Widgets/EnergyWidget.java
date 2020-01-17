package AppliedIntegrations.Gui.Widgets;


import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @Author Azazell
 */
public abstract class EnergyWidget extends AIWidget {
	private EnergyStack currentStack;

	public EnergyWidget(IWidgetHost hostGUI, int xPos, int yPos) {
		super(hostGUI, xPos, yPos);
	}

	public EnergyStack getCurrentStack() {
		return currentStack;
	}

	public void setCurrentStack(@Nonnull EnergyStack currentStack) {
		this.currentStack = currentStack;
	}

	protected void drawEnergy() {
		if (currentStack == null || currentStack.getEnergy() == null) {
			return;
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(currentStack.getEnergy().getImage());
		drawTexturedModalRect(this.xPosition + 1, this.yPosition + 1, 1, 1, 16, 16);
	}

	public abstract void onMouseClicked(EnergyStack stack);

	@Override
	public abstract void drawWidget();

	@Override
	public void getTooltip(List<String> tooltip) {
		if (currentStack == null || currentStack.getEnergy() == null) {
			return;
		}

		tooltip.add(currentStack.getEnergy().getEnergyName());
	}
}

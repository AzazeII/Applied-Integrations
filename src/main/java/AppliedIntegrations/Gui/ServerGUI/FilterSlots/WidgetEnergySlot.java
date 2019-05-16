package AppliedIntegrations.Gui.ServerGUI.FilterSlots;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Gui.Widgets.EnergyWidget;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketClientToServerFilter;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.grid.AEEnergyStack;
import appeng.api.storage.data.IAEStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
@SideOnly(Side.CLIENT)
public class WidgetEnergySlot extends EnergyWidget implements IChannelWidget<IAEEnergyStack> {

	public int id;

	public boolean shouldRender;

	public WidgetEnergySlot(final IWidgetHost hostGui, final int id, final int posX, final int posY, final boolean shouldRender) {

		super(hostGui, posX, posY);
		this.id = id;

		this.shouldRender = shouldRender;
	}

	@Override
	public void onMouseClicked(@Nonnull final EnergyStack stack) {
		// Check if slot is currently rendering
		if (!shouldRender) {
			return;
		}

		// Change stack
		setCurrentStack(stack);

		// Check not null
		if (hostGUI.getSyncHost() == null)
		// Return
		{
			return;
		}

		// Notify server
		NetworkHandler.sendToServer(new PacketClientToServerFilter(hostGUI.getSyncHost(), stack.getEnergy(), id));
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
			}

			// Re-enable lighting
			GL11.glEnable(GL11.GL_LIGHTING);
		}
	}

	@Override
	public IAEEnergyStack getAEStack() {
		// Check not null
		if (getCurrentStack() != null && getCurrentStack().getEnergy() != null) {
			return AEEnergyStack.fromStack(getCurrentStack());
		}
		return null;
	}


	@Override
	public void setAEStack(IAEStack<?> iaeStack) {
		// Check not null
		if (iaeStack == null) {
			setCurrentStack(new EnergyStack(null, 0));
		} else {
			setCurrentStack(((IAEEnergyStack) iaeStack).getStack());
		}
	}

	@Override
	public String getStackTip() {
		// Check not null
		if (getAEStack() != null) {
			return getAEStack().getEnergy().getEnergyName();
		}
		return "";
	}
}
package AppliedIntegrations.Gui.MultiController.FilterSlots;


import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.api.Storage.IChannelWidget;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEStack;
import appeng.fluids.client.gui.widgets.GuiFluidSlot;
import appeng.fluids.util.IAEFluidTank;
import net.minecraft.client.Minecraft;

/**
 * @Author Azazell
 * Implementation of IChannelWidget for fluid filtering. Pure slotFluidME, but implements interface channel widget
 */
public class WidgetFluidSlot extends GuiFluidSlot implements IChannelWidget<IAEFluidStack> {
	private final IWidgetHost host;

	public WidgetFluidSlot(IAEFluidTank fluids, int slot, int id, int x, int y, IWidgetHost host) {
		super(fluids, slot, id, x, y);
		this.host = host;
	}

	@Override
	public IAEFluidStack getAEStack() {
		return getFluidStack();
	}

	@Override
	public void setAEStack(IAEStack<?> iaeFluidStack) {
		setFluidStack((IAEFluidStack) iaeFluidStack);
	}

	@Override
	public String getStackTip() {
		if (getAEStack().getFluid() != null) {
			return getAEStack().getFluidStack().getLocalizedName();
		}
		return "";
	}

	@Override
	public void drawWidget() {
		drawContent(Minecraft.getMinecraft(), xPos(), yPos(), 0);
	}

	public boolean isMouseOverWidget(final int mouseX, final int mouseY) {
		return AIGuiHelper.INSTANCE.isPointInGuiRegion(this.yPos(), this.xPos(), AIWidget.WIDGET_SIZE - 1, AIWidget.WIDGET_SIZE - 1, mouseX, mouseY, this.host.getLeft(), this.host.getTop());
	}
}

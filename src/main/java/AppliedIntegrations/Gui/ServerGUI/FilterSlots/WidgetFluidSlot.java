package AppliedIntegrations.Gui.ServerGUI.FilterSlots;

import AppliedIntegrations.Gui.AIGuiHelper;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.api.Storage.IChannelWidget;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEStack;
import appeng.client.me.InternalFluidSlotME;
import appeng.client.me.SlotFluidME;
import appeng.fluids.client.gui.widgets.GuiFluidSlot;
import appeng.fluids.util.IAEFluidTank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

/**
 * @Author Azazell
 * Implementation of IChannelWidget for fluid filtering. Pure slotFluidME, but implements interface channel widget
 */
public class WidgetFluidSlot extends GuiFluidSlot implements IChannelWidget<IAEFluidStack> {
    public WidgetFluidSlot(IAEFluidTank fluids, int slot, int id, int x, int y) {
        super(fluids, slot, id, x, y);
    }

    @Override
    public IAEFluidStack getAEStack() {
        // Pass to parent class
        return getFluidStack();
    }

    @Override
    public String getStackTip() {
        // Check not null
        if (getAEStack().getFluid() != null)
            // Translate to local
            return I18n.format(getAEStack().getFluid().getUnlocalizedName());
        return "";
    }

    @Override
    public void setAEStack(IAEStack<?> iaeFluidStack) {
        // Pass to parent class
        setFluidStack((IAEFluidStack)iaeFluidStack);
    }

    @Override
    public void drawWidget() {
        // Pass to parent class
        drawContent(Minecraft.getMinecraft(), xPos(), yPos(), 0);
    }

    @Override
    public boolean isMouseOverWidget(int x, int y) {
        return false;
    }
}

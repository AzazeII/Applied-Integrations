package AppliedIntegrations.Gui.ServerGUI.FilterSlots;

import AppliedIntegrations.api.Storage.IChannelWidget;
import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.client.Minecraft;

import static appeng.api.config.Settings.STORAGE_FILTER;
import static appeng.api.config.StorageFilter.EXTRACTABLE_ONLY;

/**
 * @Author Azazell
 * Implementation of IChannelWidget for item filtering. Pure SlotItemME, but implements interface channel widget
 */
public class WidgetItemSlot extends GuiImgButton implements IChannelWidget<IAEItemStack> {

    private IAEItemStack stack;

    public WidgetItemSlot(int x, int y) {
        super(x, y, STORAGE_FILTER, EXTRACTABLE_ONLY);
    }

    @Override
    public IAEItemStack getAEStack() {
        return this.stack;
    }

    @Override
    public void setAEStack(IAEItemStack iaeItemStack) {
        this.stack = iaeItemStack;
    }

    @Override
    public void drawWidget() {
        // Pass to parent class
        drawButton(Minecraft.getMinecraft(), xPos(), yPos(), 0);
    }
}

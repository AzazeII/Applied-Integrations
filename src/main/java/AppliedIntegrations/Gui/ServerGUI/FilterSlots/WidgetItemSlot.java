package AppliedIntegrations.Gui.ServerGUI.FilterSlots;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.Gui.Widgets.AIWidget;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.api.Storage.IChannelWidget;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.client.gui.widgets.GuiImgButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static appeng.api.config.Settings.STORAGE_FILTER;
import static appeng.api.config.StorageFilter.EXTRACTABLE_ONLY;

/**
 * @Author Azazell
 * Implementation of IChannelWidget for item filtering
 */
public class WidgetItemSlot extends AIWidget implements IChannelWidget<IAEItemStack> {

    private IAEItemStack stack;

    private AIGridNodeInventory inv = new AIGridNodeInventory("Inner Slot Inventory", 1, 1);

    private Slot innerSlot = new Slot(inv, 0, 0, 0);

    public WidgetItemSlot(IWidgetHost host, int x, int y) {
        super(host, x, y);
    }

    private void drawStack() {
        // Get resource location of item in stack and bind
        Minecraft.getMinecraft().renderEngine.bindTexture(innerSlot.getBackgroundLocation());

        // Draw item
        drawTexturedModalRect(xPosition, yPosition, 0, 0, 16, 16);
    }

    @Override
    public String getStackTip() {
        // Check not null
        if (getAEStack().getItem() != null)
            // Translate name to local
            return I18n.format(getAEStack().getItem().getUnlocalizedName());
        return "";
    }

    @Override
    public IAEItemStack getAEStack() {
        return this.stack;
    }

    @Override
    public void setAEStack(IAEStack<?> iaeItemStack) {
        this.stack = (IAEItemStack)iaeItemStack;
    }

    @Override
    public void drawWidget() {
        // Disable lighting
        GL11.glDisable( GL11.GL_LIGHTING );

        // Full white
        GL11.glColor3f( 1.0F, 1.0F, 1.0F );

        // Check not null
        if( stack != null && stack.getItem() != null && stack.getStackSize() > 0) {
            // Set item in inner slot
            innerSlot.putStack(stack.createItemStack());

            // Draw item
            drawStack();
        }

        // Re-enable lighting
        GL11.glEnable( GL11.GL_LIGHTING );
    }

    @Override
    public void getTooltip(List<String> tooltip) {

    }

    @Override
    public boolean isMouseOverWidget(int x, int y) {
        return false;
    }

    @Override
    public void onMouseClicked() {

    }
}

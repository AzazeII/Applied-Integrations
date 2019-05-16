package AppliedIntegrations.Gui.ServerGUI.FilterSlots;


import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.AIGridNodeItemHandler;
import AppliedIntegrations.api.Storage.IChannelContainerWidget;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.container.slot.SlotFakeTypeOnly;
import appeng.util.item.AEItemStack;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @Author Azazell
 * Implementation of IChannelWidget for item filtering
 */
public class WidgetItemSlot implements IChannelContainerWidget<IAEItemStack> {
	private AIGridNodeInventory inv;

	private boolean visible;

	private SlotFakeTypeOnly innerSlot;

	public WidgetItemSlot(int x, int y) {

		this.inv = new AIGridNodeInventory("Inner Slot Inventory", 1, 1);
		this.innerSlot = new SlotFakeTypeOnly(new AIGridNodeItemHandler(inv), 0, x, y) {

			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {

				return WidgetItemSlot.this.visible;
			}
		};
	}

	@Override
	public IAEItemStack getAEStack() {

		return AEItemStack.fromItemStack(innerSlot.getStack());
	}

	@Override
	public void setAEStack(IAEStack<?> iaeItemStack) {
		// Check if stack is empty
		if (iaeItemStack == null) {
			// Nullify existing stack
			innerSlot.putStack(ItemStack.EMPTY);

			// Skip further function code
			return;
		}

		// Put stack from AE stack in slot
		innerSlot.putStack(((IAEItemStack) iaeItemStack).createItemStack());
	}

	@Override
	public String getStackTip() {

		return "";
	}

	// ------- Ignored Methods ------- //
	@Override
	public void drawWidget() {

	}

	@Override
	public boolean isMouseOverWidget(int x, int y) {

		return this.innerSlot.xPos == x && this.innerSlot.yPos == y;
	}

	@Override
	public Slot getSlotWrapper() {

		return innerSlot;
	}
	// ------- Ignored Methods ------- //

	@Override
	public void setVisible(boolean newState) {

		this.visible = newState;
	}
}

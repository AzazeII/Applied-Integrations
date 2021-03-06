package AppliedIntegrations.Gui.MultiController.FilterSlots;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Inventory.AIGridNodeItemHandler;
import AppliedIntegrations.api.Storage.IChannelContainerWidget;
import appeng.api.AEApi;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.container.slot.SlotFakeTypeOnly;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @Author Azazell
 * Implementation of IChannelWidget for item filtering
 */
public class WidgetItemSlot implements IChannelContainerWidget<IAEItemStack> {
	private SlotFakeTypeOnly innerSlot;
	private AIGridNodeInventory inv;
	private boolean visible;

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
		return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(innerSlot.getStack());
	}

	@Override
	public void setAEStack(IAEStack<?> iaeItemStack) {
		if (iaeItemStack == null) {
			innerSlot.putStack(ItemStack.EMPTY);
			return;
		}

		innerSlot.putStack(((IAEItemStack) iaeItemStack).createItemStack());
	}

	@Override
	public String getStackTip() {
		return "";
	}

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

	@Override
	public void setVisible(boolean newState) {
		this.visible = newState;
	}
}

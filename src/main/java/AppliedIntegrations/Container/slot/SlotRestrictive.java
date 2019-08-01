package AppliedIntegrations.Container.slot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public class SlotRestrictive extends SlotToggle {
	/**
	 * @see net.minecraft.inventory.Slot#Slot
	 */
	public SlotRestrictive(final IInventory inventory, final int index, final int x, final int y) {
		// Pass to super
		super(inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return this.inventory.isItemValidForSlot(getSlotIndex(), itemstack);
	}
}

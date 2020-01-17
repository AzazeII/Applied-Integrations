package AppliedIntegrations.Container.slot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public class SlotRestrictive extends SlotToggle {
	public SlotRestrictive(final IInventory inventory, final int index, final int x, final int y) {
		super(inventory, index, x, y);
	}

	@Override
	public boolean isItemValid(final ItemStack itemstack) {
		return this.inventory.isItemValidForSlot(getSlotIndex(), itemstack);
	}
}

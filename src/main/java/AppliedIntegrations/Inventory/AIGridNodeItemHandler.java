package AppliedIntegrations.Inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class AIGridNodeItemHandler implements IItemHandler {
	private final AIGridNodeInventory inner;

	public AIGridNodeItemHandler(AIGridNodeInventory inv) {
		this.inner = inv;
	}

	@Override
	public int getSlots() {
		// Pass to inner wrapper
		return inner.slots.length;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		// Pass to inner wrapper
		return inner.slots[slot];
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		// Pass to inner wrapper
		inner.setInventorySlotContents(slot, stack);

		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		// Pass to inner wrapper
		return inner.decrStackSize(slot, amount);
	}

	@Override
	public int getSlotLimit(int slot) {
		// Pass to inner wrapper
		return inner.getInventoryStackLimit();
	}
}

package AppliedIntegrations.Inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 * Delegate to AIGridNodeInventory
 */
public class AIGridNodeItemHandler implements IItemHandler {
	private final AIGridNodeInventory inner;

	public AIGridNodeItemHandler(AIGridNodeInventory inv) {
		this.inner = inv;
	}

	@Override
	public int getSlots() {
		return inner.slots.length;
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inner.slots[slot];
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		inner.setInventorySlotContents(slot, stack);

		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return inner.decrStackSize(slot, amount);
	}

	@Override
	public int getSlotLimit(int slot) {
		return inner.getInventoryStackLimit();
	}
}

package AppliedIntegrations.Container.slot;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class SlotFilter extends Slot {
	public SlotFilter(AIGridNodeInventory filterInventory, int index, int x, int y) {
		super(filterInventory, index, x, y);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		// Insert phantom item in slot
		this.putStack(stack.isEmpty() ? stack : stack.copy());
		return false;
	}

	@Nonnull
	@Override
	public ItemStack onTake(EntityPlayer thePlayer, @Nonnull ItemStack stack) {
		this.putStack(ItemStack.EMPTY);
		return super.onTake(thePlayer, stack);
	}


	@Nonnull
	@Override
	public ItemStack decrStackSize(int amount) {
		return ItemStack.EMPTY;
	}
}
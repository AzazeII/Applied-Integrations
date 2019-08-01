package AppliedIntegrations.Container.slot;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class SlotFilter extends SlotToggle {
	private boolean[] matrix;

	public SlotFilter(AIGridNodeInventory filterInventory, int index, int x, int y, boolean[] slotMatrix) {
		super(filterInventory, index, x, y);
		this.matrix = slotMatrix;
	}

	public void updateMatrix(boolean[] slotMatrix) {
		this.matrix = slotMatrix;
	}

	@SideOnly(Side.CLIENT)
	public boolean isEnabled() {
		return isEnabled && matrix[getSlotIndex()];
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
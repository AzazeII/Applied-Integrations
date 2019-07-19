package AppliedIntegrations.Inventory;
import AppliedIntegrations.api.IInventoryHost;
import net.minecraft.item.ItemStack;

import static net.minecraft.init.Items.AIR;

/**
 * @Author Azazell
 * Basically updated version of AIGridNodeInventory for containers with scroll
 */
public class AIGridNodeInventoryWithView extends AIGridNodeInventory{
	private class AIGridNodeViewInventory extends AIGridNodeInventory {
		int slotDiff;

		AIGridNodeViewInventory(String customName, int viewSize, int stackLimit, IInventoryHost receiver,
		                        AIGridNodeInventoryWithView fullInv) {
			super(customName, viewSize, stackLimit, receiver);
			this.slots = fullInv.slots;
		}

		@Override
		public void setInventorySlotContents(int slotId, ItemStack itemstack) {
			if (itemstack.getItem() != AIR && itemstack.getCount() > getInventoryStackLimit()) {
				itemstack.setCount(getInventoryStackLimit());
			}

			this.slots[slotId + slotDiff] = itemstack;

			markDirty();
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return slots[index + slotDiff];
		}

		@Override
		public ItemStack decrStackSize(int slotId, int amount) {
			slotId += slotDiff;

			if (this.slots[slotId].getItem() == AIR) {
				return null;
			}
			ItemStack itemstack;
			if (this.slots[slotId].getCount() <= amount) {
				itemstack = this.slots[slotId];
				this.slots[slotId] = new ItemStack(AIR);
				markDirty();
				return itemstack;
			} else {
				ItemStack temp = this.slots[slotId];
				itemstack = temp.splitStack(amount);
				this.slots[slotId] = temp;
				if (temp.getCount() == 0) {
					this.slots[slotId] = new ItemStack(AIR);
				} else {
					this.slots[slotId] = temp;
				}
				markDirty();
				return itemstack;
			}
		}
	}

	private final AIGridNodeViewInventory viewInventory;

	protected AIGridNodeInventoryWithView(String _customName, int _size, int viewSize, int _stackLimit,
	                                      IInventoryHost _receiver) {
		super(_customName, _size, _stackLimit, _receiver);
		this.viewInventory = new AIGridNodeViewInventory(_customName, viewSize, _stackLimit, _receiver, this);
	}

	public void updateView(int slotDifference) {
		viewInventory.slotDiff = slotDifference;
	}

	public AIGridNodeInventory getViewInventory() {
		return viewInventory;
	}
}

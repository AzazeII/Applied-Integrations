package AppliedIntegrations.Container;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import appeng.api.implementations.items.IUpgradeModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static net.minecraft.init.Items.AIR;

/**
 * @Author Azazell
 */
public abstract class ContainerWithUpgradeSlots extends ContainerWithPlayerInventory {
	private static int UPGRADE_Y_POSITION_MULTIPLIER = 18;

	protected boolean hasNetworkTool = false;

	private int firstToolSlotNumber = -1;
	private int lastToolSlotNumber = -1;
	private int firstUpgradeSlot = -1;
	private int lastUpgradeSlot = -1;

	public ContainerWithUpgradeSlots(final EntityPlayer player) {
		super(player);
	}

	protected void addUpgradeSlots(final AIGridNodeInventory upgradeInventory, final int count, final int xPosition, final int yPosition) {
		Slot upgradeSlot = null;

		for (int slotIndex = 0; slotIndex < count; slotIndex++) {
			upgradeSlot = new SlotRestrictive(upgradeInventory, slotIndex, xPosition, yPosition + (slotIndex * UPGRADE_Y_POSITION_MULTIPLIER)) {

				@SideOnly(Side.CLIENT)
				public String getSlotTexture() {
					// Here we return ID of atlas-bound texture. If it'll not be bound in atlas, texture on this id will not be actually shown in GUI
					return AppliedIntegrations.modid + ":gui/slots/upgradesloticon";
				}
			};

			this.addSlotToContainer(upgradeSlot);
			if (slotIndex == 0) {
				this.firstUpgradeSlot = upgradeSlot.slotNumber;
			}
		}

		if (upgradeSlot != null) {
			this.lastUpgradeSlot = upgradeSlot.slotNumber;
		}
	}

	public boolean hasNetworkTool() {
		return this.hasNetworkTool;
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber) {
		// Get the slot that was clicked on
		Slot slot = this.getSlotOrNull(slotNumber);
		boolean merged = false;

		// Did we get a slot, and does it have a valid item?
		if ((slot != null) && (slot.getHasStack())) {
			ItemStack slotStack = slot.getStack();

			// Was the slot clicked in the player or hotbar inventory?
			if (this.slotClickedWasInPlayerInventory(slotNumber) || this.slotClickedWasInHotbarInventory(slotNumber)) {
				merged = this.mergeSlotWithUpgrades(slotStack);

				if (!merged) {
					merged = this.mergeSlotWithNetworkTool(slotStack);
				}

				if (!merged) {
					merged = this.swapSlotInventoryHotbar(slotNumber, slotStack);
				}
			} else if (this.slotClickedWasInUpgrades(slotNumber)) {
				merged = this.mergeSlotWithNetworkTool(slotStack);

				if (!merged) {
					merged = this.mergeSlotWithPlayerInventory(slotStack);
				}
			} else if (this.hasNetworkTool && this.slotClickedWasInNetworkTool(slotNumber)) {
				merged = this.mergeSlotWithUpgrades(slotStack);

				if (!merged) {
					merged = this.mergeSlotWithPlayerInventory(slotStack);
				}
			}

			if (!merged) {
				return new ItemStack(AIR);
			}

			if (slotStack.getCount() == 0) {
				slot.putStack(new ItemStack(AIR));
			}

			slot.onSlotChanged();
			this.detectAndSendChanges();
		}

		return new ItemStack(AIR);
	}

	// @Return true, if slots was merged successfully
	protected boolean mergeSlotWithUpgrades(final ItemStack slotStack) {
		if (!(slotStack.getItem() instanceof IUpgradeModule)) {
			return false;
		}

		boolean didMerge = false;

		for (int index = this.firstUpgradeSlot; index <= this.lastUpgradeSlot; index++) {
			Slot upgradeSlot = this.inventorySlots.get(index);

			if ((upgradeSlot != null) && (!upgradeSlot.getHasStack())) {
				if (upgradeSlot.isItemValid(slotStack)) {
					ItemStack upgradeStack = slotStack.copy();
					upgradeStack.setCount(1);

					upgradeSlot.putStack(upgradeStack);
					slotStack.setCount(slotStack.getCount() - 1);
					didMerge = true;

					if (slotStack.getCount() == 0) {
						break;
					}
				}
			}
		}

		return didMerge;
	}

	// @Return true, if slots was merged successfully
	protected boolean mergeSlotWithNetworkTool(final ItemStack slotStack) {
		if (this.hasNetworkTool) {
			if (!(slotStack.getItem() instanceof IUpgradeModule)) {
				return false;
			}

			return this.mergeItemStack(slotStack, this.firstToolSlotNumber, this.lastToolSlotNumber + 1, false);
		}

		return false;
	}

	protected boolean slotClickedWasInUpgrades(final int slotNumber) {
		return (slotNumber >= this.firstUpgradeSlot) && (slotNumber <= this.lastUpgradeSlot);
	}

	// Check if slot clicked from network tool inventory
	protected boolean slotClickedWasInNetworkTool(final int slotNumber) {
		return this.hasNetworkTool && (slotNumber >= this.firstToolSlotNumber) && (slotNumber <= this.lastToolSlotNumber);
	}
}

package AppliedIntegrations.Container;
import AppliedIntegrations.Container.slot.SlotToggle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public abstract class ContainerWithPlayerInventory extends AIContainer {
	protected static final int SLOT_SIZE = 18;

	private static final int INVENTORY_X_OFFSET = 8;
	private static int ROWS = 3;
	private static int COLUMNS = 9;
	protected final SlotToggle[] hotbarSlots = new SlotToggle[ContainerWithPlayerInventory.COLUMNS];
	protected final SlotToggle[] playerSlots = new SlotToggle[ContainerWithPlayerInventory.COLUMNS * ContainerWithPlayerInventory.ROWS];

	public ContainerWithPlayerInventory(final EntityPlayer player) {
		super(player);
	}

	// Attempt to move items from hotbar to main inventory. Returns true, if operation was successful
	protected final boolean swapSlotInventoryHotbar(final int slotNumber, final ItemStack slotStack) {
		if (this.slotClickedWasInHotbarInventory(slotNumber)) {
			return this.mergeSlotWithPlayerInventory(slotStack);
		} else if (this.slotClickedWasInPlayerInventory(slotNumber)) {
			return this.mergeSlotWithHotbarInventory(slotStack);
		}

		return false;
	}

	// @return True, if slot is from hotbar inv
	protected final boolean slotClickedWasInHotbarInventory(final int slotNumber) {
		return (slotNumber >= this.hotbarSlots[0].slotNumber)
				&& (slotNumber <= this.hotbarSlots[ContainerWithPlayerInventory.COLUMNS - 1].slotNumber);
	}

	// Try to merge stack with player slots
	protected final boolean mergeSlotWithPlayerInventory(final ItemStack slotStack) {
		return this.mergeItemStack(slotStack, this.playerSlots[0].slotNumber,
				this.playerSlots[(ContainerWithPlayerInventory.COLUMNS * ContainerWithPlayerInventory.ROWS) - 1].slotNumber + 1, false);
	}

	// Check if slot clicked is slot from player's inventory
	protected final boolean slotClickedWasInPlayerInventory(final int slotNumber) {
		return (slotNumber >= this.playerSlots[0].slotNumber) &&
				(slotNumber <= this.playerSlots[(ContainerWithPlayerInventory.COLUMNS * ContainerWithPlayerInventory.ROWS) - 1].slotNumber);
	}

	// Try to merge stack with player slots
	protected final boolean mergeSlotWithHotbarInventory(final ItemStack slotStack) {
		return this.mergeItemStack(slotStack, this.hotbarSlots[0].slotNumber,
				this.hotbarSlots[ContainerWithPlayerInventory.COLUMNS - 1].slotNumber + 1, false);
	}

	/**
	 * Binds the player inventory to this container.
	 *
	 * @param playerInventory Inventory to bind
	 *                        The Y position offset for the slots
	 * @param hotbarPositionY The Y position offset for hotbar slots
	 */
	public final void bindPlayerInventory(final IInventory playerInventory, final int inventoryOffsetY, final int hotbarPositionY) {
		// Hotbar inventory
		for (int column = 0; column < ContainerWithPlayerInventory.COLUMNS; column++) {
			this.hotbarSlots[column] = new SlotToggle(playerInventory, column, ContainerWithPlayerInventory.INVENTORY_X_OFFSET + (column * ContainerWithPlayerInventory.SLOT_SIZE), hotbarPositionY);
			this.addSlotToContainer(this.hotbarSlots[column]);
		}

		// Main inventory
		for (int row = 0; row < ContainerWithPlayerInventory.ROWS; row++) {
			for (int column = 0; column < ContainerWithPlayerInventory.COLUMNS; column++) {
				int index = column + (row * ContainerWithPlayerInventory.COLUMNS);
				this.playerSlots[index] = new SlotToggle(playerInventory, ContainerWithPlayerInventory.COLUMNS + index, ContainerWithPlayerInventory.INVENTORY_X_OFFSET + (column * ContainerWithPlayerInventory.SLOT_SIZE), (row * ContainerWithPlayerInventory.SLOT_SIZE) + inventoryOffsetY);
				this.addSlotToContainer(this.playerSlots[index]);
			}
		}
	}
}

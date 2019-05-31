package AppliedIntegrations.Container.tile.MultiController;


import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class ContainerMultiControllerCore extends ContainerWithPlayerInventory {
	private static final int CARD_SLOT_ROWS = 5;
	private static final int CARD_SLOT_COLUMNS = 9;

	public ContainerMultiControllerCore(EntityPlayer player, TileMultiControllerCore master) {
		super(player);

		// Bind card slots
		this.addCardSlots(master.cardInv);

		// Add listener
		master.listeners.add(this);

		// Bind player slots
		super.bindPlayerInventory(player.inventory, 108, 162);
	}

	private void addCardSlots(AIGridNodeInventory cardInv) {
		// Create counter
		int i = 0;

		// Iterate for Y
		for (int y = 0; y < CARD_SLOT_ROWS; y++) {
			// Iterate for X
			for (int x = 0; x < CARD_SLOT_COLUMNS; x++) {
				// Check not null
				if (cardInv != null) {
					// Add ME server slot
					this.addSlotToContainer(new SlotRestrictive(cardInv, i, 9 + x * 18, y * 18 + 3) {
						// Override icon getter for this slot
						@SideOnly(Side.CLIENT)
						public String getSlotTexture() {

							return AppliedIntegrations.modid + ":gui/slots/network_card_slot";
						}
					});

					// Add to counter
					i++;
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer p) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber) {
		return ItemStack.EMPTY;
	}
}

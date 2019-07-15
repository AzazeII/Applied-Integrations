package AppliedIntegrations.Container.tile.MultiController;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.MultiController.PacketScrollSync;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Azazell
 */
public class ContainerMultiControllerCore extends ContainerWithPlayerInventory {
	private static final int CARD_SLOT_ROWS = 5;
	private static final int CARD_SLOT_COLUMNS = 9;
	private int slotDifference = 0;
	private List<ItemStack> itemStackList = new ArrayList<>();
	private TileMultiControllerCore master;

	public ContainerMultiControllerCore(EntityPlayer player, TileMultiControllerCore master) {
		super(player);

		// Bind card slots
		this.addCardSlots(master.cardInv);

		this.master = master;

		// Add listener
		this.master.listeners.add(this);

		// Fill stack list with card inventory slots
		this.itemStackList.addAll(Arrays.asList(master.cardInv.slots));

		// Bind player slots
		super.bindPlayerInventory(player.inventory, 107, 165);
	}

	public void scrollTo(int slotScroll) {
		this.slotDifference += (slotScroll == 0 ? 0 : (slotScroll < 0 ? -CARD_SLOT_COLUMNS : CARD_SLOT_COLUMNS));
		NetworkHandler.sendToServer(new PacketScrollSync(slotDifference, master));
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
					// Add ME multi-controller slot
					this.addSlotToContainer(new SlotRestrictive(cardInv, i, 9 + x * 18, y * 18 + 3) {
						@Override
						public ItemStack getStack() {
							return this.inventory.getStackInSlot(getSlotIndex());
						}

						@Override
						public ItemStack decrStackSize(int amount) {
							return this.inventory.decrStackSize(getSlotIndex(), amount);
						}

						@Override
						public boolean isHere(IInventory inv, int slotIn) {
							return inv == this.inventory && slotIn == getSlotIndex();
						}

						@Override
						public void putStack(@Nonnull ItemStack stack) {
							this.inventory.setInventorySlotContents(getSlotIndex(), stack);
 						}

						@Override
						public int getSlotIndex() {
							return super.getSlotIndex() + slotDifference;
						}
					});

					// Add to counter
					i++;
				}
			}
		}
	}

	public void setSlotDiff(int scroll) {
		this.slotDifference = scroll;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void setAll(List<ItemStack> list) {
		// Don't directly put stacks into slots of the card inventory
		for (int i = 0; i < list.size(); ++i) {
			if ( i < CARD_SLOT_ROWS * CARD_SLOT_COLUMNS ) {
				this.master.cardInv.setInventorySlotContents(i, list.get(i));
			} else {
				this.getSlot(i).putStack(list.get(i));
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

	@Override
	public ISyncHost getSyncHost() {
		return this.master;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		// Check if host match our host class
		if (host instanceof TileMultiControllerCore) {
			// Update current host
			this.master = (TileMultiControllerCore) host;
		}
	}
}

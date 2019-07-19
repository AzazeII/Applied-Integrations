package AppliedIntegrations.Container.tile.MultiController;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.MultiController.PacketScrollServerToClient;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class ContainerMultiControllerCore extends ContainerWithPlayerInventory {
	private static final int CARD_SLOT_ROWS = 5;
	private static final int CARD_SLOT_COLUMNS = 9;
	private TileMultiControllerCore master;

	public ContainerMultiControllerCore(EntityPlayer player, TileMultiControllerCore master) {
		super(player);

		// Bind card slots
		this.addCardSlots(master.cardInv.getViewInventory());

		this.master = master;

		// Add listener
		this.master.listeners.add(this);

		// Bind player slots
		super.bindPlayerInventory(player.inventory, 107, 165);
	}

	public void scrollTo(int slotScroll) {
		if (slotScroll == 0) {
			return;
		}

		// Increase/decrease scroll difference by row size
		master.setSlotDiff(master.getSlotDiff() + (slotScroll < 0 ? -CARD_SLOT_COLUMNS : CARD_SLOT_COLUMNS));
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
					this.addSlotToContainer(new SlotRestrictive(cardInv, i, 9 + x * 18, y * 18 + 3));

					// Add to counter
					i++;
				}
			}
		}
	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		super.onContainerClosed(player);

		// Nullify slot difference to make items not teleport between slots after reopening GUI
		this.master.setSlotDiff(0);
	}

	@Override
	protected void syncHostWithGUI() {
		super.syncHostWithGUI();

		NetworkHandler.sendTo(new PacketScrollServerToClient(master.getSlotDiff(),
				(TileMultiControllerCore) getSyncHost()), (EntityPlayerMP) player);
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

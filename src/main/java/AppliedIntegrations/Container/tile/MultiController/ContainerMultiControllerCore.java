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
	public static final int CARD_SLOT_VIEW_ROWS = 5;
	public static final int CARD_SLOT_ROWS = 17;
	public static final int CARD_SLOT_COLUMNS = 9;
	private TileMultiControllerCore master;

	public ContainerMultiControllerCore(EntityPlayer player, TileMultiControllerCore master) {
		super(player);

		this.addCardSlots(master.cardInv.getViewInventory());
		this.master = master;
		this.master.listeners.add(this);
		super.bindPlayerInventory(player.inventory, 107, 165);
	}

	public void scrollTo(double slotScroll) {
		if (slotScroll == 0) {
			return;
		}

		master.setSlotDiff(master.getSlotDiff() + (slotScroll < 0 ? -CARD_SLOT_COLUMNS : CARD_SLOT_COLUMNS));
	}

	private void addCardSlots(AIGridNodeInventory cardInv) {
		int i = 0;

		for (int y = 0; y < CARD_SLOT_VIEW_ROWS; y++) {
			for (int x = 0; x < CARD_SLOT_COLUMNS; x++) {
				if (cardInv != null) {
					this.addSlotToContainer(new SlotRestrictive(cardInv, i, 9 + x * 18, y * 18 + 3));
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
		if (host instanceof TileMultiControllerCore) {
			this.master = (TileMultiControllerCore) host;
		}
	}
}

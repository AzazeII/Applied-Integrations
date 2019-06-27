package AppliedIntegrations.Container.part;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketTerminalUpdate;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import AppliedIntegrations.api.IEnergySelectorContainer;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.storage.IMEMonitor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 */
public class ContainerEnergyTerminal extends ContainerWithPlayerInventory implements IEnergySelectorContainer {
	// X of output
	private static final int OUTPUT_POSITION_X = 26;
	// Y of output
	private static final int OUTPUT_POSITION_Y = 92;
	// X of input
	private static final int INPUT_POSITION_X = 8;
	// Y of input
	private static final int INPUT_POSITION_Y = OUTPUT_POSITION_Y;
	private static int OUTPUT_INV_INDEX = 38, INPUT_INV_INDEX = 37;
	public EntityPlayer player;
	private PartEnergyTerminal terminal;
	private AIGridNodeInventory privateInventory = new AIGridNodeInventory(AppliedIntegrations.modid + ".item.energy.cell.inventory",
			2,
			64) {
		@Override
		public boolean isItemValidForSlot(final int slotID, final ItemStack itemStack) {

			return Utils.getEnergyFromItemStack(itemStack) != null;
		}
	};

	public ContainerEnergyTerminal(PartEnergyTerminal terminal, EntityPlayer player) {

		super(player);
		this.bindPlayerInventory(player.inventory, 122, 180);
		this.terminal = terminal;
		this.player = player;

		// Do all AE2 mechanics only on server
		if (!terminal.getHostWorld().isRemote) {

			// Get energy inventory
			IMEMonitor<IAEEnergyStack> inv = terminal.getEnergyInventory();

			// Check not null
			if (inv != null) {
				// Add listener for ME monitor
				inv.addListener(terminal, null);

				// Notify GUI first time about list, to make it show current list of all energies
				for (ContainerEnergyTerminal listener : terminal.listeners) {
					// Send packet over network
					NetworkHandler.sendTo(new PacketTerminalUpdate(inv.getStorageList(),
							terminal.getSortOrder(),
							terminal), (EntityPlayerMP) listener.player);
				}
			}

			// Add listener
			terminal.listeners.add(this);
		}

		this.addSlotToContainer(new SlotRestrictive(privateInventory, 0, INPUT_POSITION_X, INPUT_POSITION_Y));

		this.addSlotToContainer(new SlotFurnaceOutput(this.player,
				privateInventory,
				1,
				OUTPUT_POSITION_X,
				OUTPUT_POSITION_Y));

		ContainerChest chest;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		// Create empty stack
		ItemStack itemstack = ItemStack.EMPTY;

		// Get slot at index
		Slot slot = this.inventorySlots.get(index);

		// Check not null and has stack
		if (slot != null && slot.getHasStack()) {
			// Get stack in slot
			ItemStack itemstack1 = slot.getStack();

			// Copy stack
			itemstack = itemstack1.copy();

			// Check if index
			if (index < INPUT_INV_INDEX - 1) {
				if (!this.mergeItemStack(itemstack1, INPUT_INV_INDEX - 1, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, INPUT_INV_INDEX - 1, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	@Override
	public void setSelectedEnergy(LiquidAIEnergy _energy) {

	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		// Call super
		super.onContainerClosed(player);

		// Get inventory
		IMEMonitor<IAEEnergyStack> inv = terminal.getEnergyInventory();

		// Check not null
		if (inv == null) {
			return;
		}

		// Remove terminal from listeners list from ME monitor of energy terminal
		inv.removeListener(terminal);

		// Remove listener from terminal
		terminal.listeners.remove(this);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ISyncHost getSyncHost() {
		return this.terminal;
	}

	@Override
	public void setSyncHost(ISyncHost host) {
		// Check if host match our host class
		if (host instanceof PartEnergyTerminal) {
			// Update current host
			this.terminal = (PartEnergyTerminal) host;
		}
	}
}
package AppliedIntegrations.Container.part;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Gui.Widgets.WidgetEnergySelector;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketTerminalUpdate;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import AppliedIntegrations.api.IEnergySelectorContainer;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.EnergyList;
import appeng.api.config.SortOrder;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.widgets.GuiImgButton;
import com.google.common.collect.Ordering;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
	private static int INPUT_INV_INDEX = 37;

	public EntityPlayer player;
	private PartEnergyTerminal terminal;

	public final List<WidgetEnergySelector> widgetEnergySelectors = new ArrayList<>();

	public EnergyStack selectedStack = new EnergyStack(null, 0);

	public GuiImgButton sortButton;

	public SortOrder sortMode = SortOrder.NAME;
	public IItemList<IAEEnergyStack> list = new EnergyList();

	// Create comparator for list
	public Ordering<IAEEnergyStack> sorter = new Ordering<IAEEnergyStack>() {
		@Override
		public int compare(@Nullable IAEEnergyStack left, @Nullable IAEEnergyStack right) {
			// Check both energies not null
			if (left == null || right == null)
			// Same place in slots
			{
				return 0;
			}

			//------------ Alphabet Sorting ------------//
			if (sortMode == SortOrder.NAME) {
				// Get left energy name or "null"
				String leftEnergyName = left.getEnergy() == null ? "null" : left.getEnergy().getEnergyName();

				// Get right energy name or "null"
				String rightEnergyName = right.getEnergy() == null ? "null" : right.getEnergy().getEnergyName();

				// Compare first energy to second by default method of class String
				return leftEnergyName.compareTo(rightEnergyName);

				//------------ Amount Sorting ------------//
			} else if (sortMode == SortOrder.AMOUNT) {
				// Get left energy amount
				Long leftAmount = left.getStackSize();

				// Get right energy amount
				Long rightAmount = right.getStackSize();

				// Compare first energy to second by default method of class Long
				return leftAmount.compareTo(rightAmount);

				//------------ Mod Sorting ------------//
			} else if (sortMode == SortOrder.MOD) {
				// Get mod id of left energy
				String leftModid = left.getEnergy() == null ? "null" : left.getEnergy().getModid();

				// Get mod id of right energy
				String rightModid = right.getEnergy() == null ? "null" : right.getEnergy().getModid();


				return leftModid.compareTo(rightModid);
			}

			// Random sorting
			return 0;
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

		this.addSlotToContainer(new SlotRestrictive(terminal.energyIOInventory, 0, INPUT_POSITION_X, INPUT_POSITION_Y));

		this.addSlotToContainer(new SlotFurnaceOutput(this.player,
				terminal.energyIOInventory, 1, OUTPUT_POSITION_X, OUTPUT_POSITION_Y));
	}

	public void updateList(IItemList<IAEEnergyStack> list) {
		// Create sorted list
		List<IAEEnergyStack> sorted = sorter.sortedCopy(list);

		// Replace current list with server-sided one
		this.list = new EnergyList();
		sorted.forEach(this.list::add);

		// Call update function
		updateStacksPrecise(sorted);
	}


	public void updateStacksPrecise(List<IAEEnergyStack> sorted) {
		// Update energies in selectors. Also if size is zero, we need to reset selectors
		if (sorted.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				widgetEnergySelectors.get(i).setCurrentStack(new EnergyStack(sorted.get(i).getEnergy(), sorted.get(i).getStackSize()));
			}
		} else {
			for (int i = 0; i < widgetEnergySelectors.size(); i++) {
				widgetEnergySelectors.get(i).setCurrentStack(new EnergyStack(null, 0));
			}
		}

		// Now, if stack is selected it should be updated, when monitor changes
		// Check if both stack size and energy are greater than zero(or not equal null)
		if (this.selectedStack.getEnergy() != null && this.selectedStack.amount > 0) {
			// Call list to give as precisely equal stack, to stack we have, then convert it to normal Energy stack and set our selected stack to it.
			// It will update size of monitored stack
			final IAEEnergyStack precise = list.findPrecise(AEEnergyStack.fromStack(selectedStack));
			if (precise != null) {
				selectedStack = precise.getStack();
			} else {
				selectedStack = new EnergyStack(null, 0);
			}
		}
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
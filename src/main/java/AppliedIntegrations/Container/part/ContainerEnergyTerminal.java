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
	private static final int OUTPUT_POSITION_X = 26;
	private static final int OUTPUT_POSITION_Y = 92;
	private static final int INPUT_POSITION_X = 8;
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
			if (left == null || right == null) {
				return 0;
			}

			if (sortMode == SortOrder.NAME) {
				//------------ Alphabet Sorting ------------//
				String leftEnergyName = left.getEnergy() == null ? "null" : left.getEnergy().getEnergyName();
				String rightEnergyName = right.getEnergy() == null ? "null" : right.getEnergy().getEnergyName();
				return leftEnergyName.compareTo(rightEnergyName);

			} else if (sortMode == SortOrder.AMOUNT) {
				//------------ Amount Sorting ------------//
				// Get left energy amount
				Long leftAmount = left.getStackSize();
				Long rightAmount = right.getStackSize();
				return leftAmount.compareTo(rightAmount);

			} else if (sortMode == SortOrder.MOD) {
				//------------ Mod Sorting ------------//
				String leftModid = left.getEnergy() == null ? "null" : left.getEnergy().getModid();
				String rightModid = right.getEnergy() == null ? "null" : right.getEnergy().getModid();
				return leftModid.compareTo(rightModid);
			}


			return 0;
		}
	};


	public ContainerEnergyTerminal(PartEnergyTerminal terminal, EntityPlayer player) {
		super(player);
		this.bindPlayerInventory(player.inventory, 122, 180);
		this.terminal = terminal;
		this.player = player;

		if (!terminal.getHostWorld().isRemote) {
			IMEMonitor<IAEEnergyStack> inv = terminal.getEnergyInventory();
			if (inv != null) {
				inv.addListener(terminal, null);

				for (ContainerEnergyTerminal listener : terminal.listeners) {
					NetworkHandler.sendTo(new PacketTerminalUpdate(inv.getStorageList(),
							terminal.getSortOrder(),
							terminal), (EntityPlayerMP) listener.player);
				}
			}

			terminal.listeners.add(this);
		}

		this.addSlotToContainer(new SlotRestrictive(terminal.energyIOInventory, 0, INPUT_POSITION_X, INPUT_POSITION_Y));
		this.addSlotToContainer(new SlotFurnaceOutput(this.player,
				terminal.energyIOInventory, 1, OUTPUT_POSITION_X, OUTPUT_POSITION_Y));
	}

	public void updateList(IItemList<IAEEnergyStack> list) {
		List<IAEEnergyStack> sorted = sorter.sortedCopy(list);

		this.list = new EnergyList();
		sorted.forEach(this.list::add);
		updateStacksPrecise(sorted);
	}


	public void updateStacksPrecise(List<IAEEnergyStack> sorted) {
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
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
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
	public void setSelectedEnergy(LiquidAIEnergy energy) {

	}

	@Override
	public void onContainerClosed(@Nonnull final EntityPlayer player) {
		super.onContainerClosed(player);

		IMEMonitor<IAEEnergyStack> inv = terminal.getEnergyInventory();
		if (inv == null) {
			return;
		}

		inv.removeListener(terminal);
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
		if (host instanceof PartEnergyTerminal) {
			this.terminal = (PartEnergyTerminal) host;
		}
	}
}
package AppliedIntegrations.Inventory.Manager;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.api.IInventoryHost;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.AEApi;
import appeng.api.config.RedstoneMode;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

import static net.minecraft.init.Items.AIR;

/**
 * @Author Azazell
 */
public class UpgradeInventoryManager implements IInventoryHost {
	public interface IUpgradeInventoryManagerHost extends ISyncHost {
		// Send data to client
		void syncClient(int filterSize, boolean redstoneControlled, boolean autoCrafting, boolean inverted,
		                boolean fuzzyCompare, int upgradeSpeedCount);
	}

	private final IUpgradeInventoryManagerHost host;
	public AIGridNodeInventory upgradeInventory;
	public int filterSize;
	public boolean redstoneControlled;
	public boolean inverted;
	public int upgradeSpeedCount;
	public boolean fuzzyCompare;
	public boolean autoCrafting;
	public RedstoneMode redstoneMode = RedstoneMode.IGNORE;

	public UpgradeInventoryManager(IUpgradeInventoryManagerHost host, String name, int size) {
		this(host, name, size, (stack) -> true);
	}

	public UpgradeInventoryManager(IUpgradeInventoryManagerHost host, String name, int size, Predicate<ItemStack> isItemValid) {
		this.host = host;
		this.upgradeInventory = new AIGridNodeInventory(name, size, 1, this) {
			@Override
			public boolean isItemValidForSlot(int i, ItemStack itemStack) {
				return isItemValid.test(itemStack);
			}
		};
	}

	@Override
	public void onInventoryChanged() {
		//=========+Reset+=========//
		this.filterSize = 0;
		this.redstoneControlled = false;
		this.inverted = false;
		this.fuzzyCompare = false;
		this.autoCrafting = false;
		this.upgradeSpeedCount = 0;
		//=========+Reset+=========//

		// Iterate over all items and detect upgrade cards
		for (int i = 0; i < this.upgradeInventory.getSizeInventory(); i++) {
			ItemStack currentStack = this.upgradeInventory.getStackInSlot(i);
			if (currentStack.getItem() != AIR) {
				if (AEApi.instance().definitions().materials().cardCapacity().isSameAs(currentStack)) {
					this.filterSize++;
				}

				if (AEApi.instance().definitions().materials().cardSpeed().isSameAs(currentStack)) {
					this.upgradeSpeedCount++;
				}

				if (AEApi.instance().definitions().materials().cardRedstone().isSameAs(currentStack)) {
					this.redstoneControlled = true;
				}

				if (AEApi.instance().definitions().materials().cardInverter().isSameAs(currentStack)) {
					this.inverted = true;
				}

				if (AEApi.instance().definitions().materials().cardFuzzy().isSameAs(currentStack)) {
					this.fuzzyCompare = true;
				}

				if (AEApi.instance().definitions().materials().cardCrafting().isSameAs(currentStack)) {
					this.autoCrafting = true;
				}
			}
		}

		// Sync data
		host.syncClient(filterSize, redstoneControlled, autoCrafting, inverted, fuzzyCompare, upgradeSpeedCount);
	}

	public void acceptVal(Enum val) {
		if (val instanceof RedstoneMode) {
			redstoneMode = (RedstoneMode) val;
		}
	}
}

package AppliedIntegrations.Inventory.Manager;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.api.IInventoryHost;
import appeng.api.AEApi;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

import static net.minecraft.init.Items.AIR;

/**
 * @Author Azazell
 */
public class UpgradeInventoryManager implements IInventoryHost {
	public interface IUpgradeInventoryManagerHost {
		void doSync(int filterSize, boolean redstoneControlled, int upgradeSpeedCount);
	}

	private final IUpgradeInventoryManagerHost host;
	public AIGridNodeInventory upgradeInventory;
	public int filterSize;
	public boolean redstoneControlled;
	public int upgradeSpeedCount;

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
		// Set current filter size to zero
		this.filterSize = 0;

		// Trigger redstone control
		this.redstoneControlled = false;

		// Set speed to 0
		this.upgradeSpeedCount = 0;
		//=========+Reset+=========//

		// Iterate until i equal to stack size
		for (int i = 0; i < this.upgradeInventory.getSizeInventory(); i++) {

			// Get current stack from slot
			ItemStack currentStack = this.upgradeInventory.getStackInSlot(i);

			// Check not air
			if (currentStack.getItem() != AIR) {
				// Check if current stack is capacity card stack
				if (AEApi.instance().definitions().materials().cardCapacity().isSameAs(currentStack)) {
					// Increase filter size
					this.filterSize++;
				}

				// Check if current stack is redstone card stack
				if (AEApi.instance().definitions().materials().cardRedstone().isSameAs(currentStack)) {
					// Trigger restone control
					this.redstoneControlled = true;
				}

				// Check if current stack is speed card stack
				if (AEApi.instance().definitions().materials().cardSpeed().isSameAs(currentStack)) {
					// Increase speed
					this.upgradeSpeedCount++;
				}
			}
		}

		// Sync data
		host.doSync(filterSize, redstoneControlled, upgradeSpeedCount);
	}
}

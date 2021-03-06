package AppliedIntegrations.Inventory.Manager;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.api.IInventoryHost;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.YesNo;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.function.Predicate;

import static net.minecraft.init.Items.AIR;

/**
 * @Author Azazell
 */
public class UpgradeInventoryManager implements IInventoryHost {
	private static final String KEY_FILTER_SIZE = "#FILTER_SIZE";
	private static final String KEY_RED_CONTROLLED = "#RED_CONTROLLER";
	private static final String KEY_INVERTED = "#INVERTED";
	private static final String KEY_AUTO_CRAFTING = "#AUTO_CRAFTING";
	private static final String KEY_FUZZY_COMPARE = "#FUZZY_COMPARE";
	private static final String KEY_REDSTONE_MODE = "#REDSTONE_MODE";
	private static final String KEY_FUZZY_MODE = "#FUZZY_MODE";
	private static final String KEY_SPEED_COUNT = "#UPGRADE_SPEED_COUNT";
	private static final String KEY_CRAFT_ONLY = "#CRAFT_ONLY";

	private final ISyncHost host;
	public AIGridNodeInventory upgradeInventory;
	public byte filterSize;
	public boolean redstoneControlled;
	public boolean inverted;
	public int upgradeSpeedCount;
	public boolean fuzzyCompare;
	public boolean autoCrafting;
	public RedstoneMode redstoneMode = RedstoneMode.IGNORE;
	public FuzzyMode fuzzyMode = FuzzyMode.IGNORE_ALL;
	public YesNo craftOnly = YesNo.NO;

	public UpgradeInventoryManager(ISyncHost host, String name, int size) {
		this(host, name, size, (stack) -> true);
	}

	public UpgradeInventoryManager(ISyncHost host, String name, int size, Predicate<ItemStack> isItemValid) {
		this.host = host;
		this.upgradeInventory = new AIGridNodeInventory(name, size, 1, this) {
			@Override
			public boolean isItemValidForSlot(int i, ItemStack itemStack) {
				return isItemValid.test(itemStack);
			}
		};
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setByte(KEY_FILTER_SIZE, filterSize);
		nbt.setInteger(KEY_SPEED_COUNT, upgradeSpeedCount);
		nbt.setBoolean(KEY_RED_CONTROLLED, redstoneControlled);
		nbt.setBoolean(KEY_INVERTED, inverted);
		nbt.setBoolean(KEY_AUTO_CRAFTING, autoCrafting);
		nbt.setBoolean(KEY_FUZZY_COMPARE, fuzzyCompare);
		nbt.setByte(KEY_REDSTONE_MODE, (byte) redstoneMode.ordinal());
		nbt.setByte(KEY_FUZZY_MODE, (byte) fuzzyMode.ordinal());
		nbt.setByte(KEY_CRAFT_ONLY, (byte) craftOnly.ordinal());
	}

	public void readFromNBT(NBTTagCompound nbt) {
		filterSize = nbt.getByte(KEY_FILTER_SIZE);
		upgradeSpeedCount = nbt.getInteger(KEY_SPEED_COUNT);
		redstoneControlled = nbt.getBoolean(KEY_RED_CONTROLLED);
		inverted = nbt.getBoolean(KEY_INVERTED);
		autoCrafting = nbt.getBoolean(KEY_AUTO_CRAFTING);
		fuzzyCompare = nbt.getBoolean(KEY_FUZZY_COMPARE);
		redstoneMode = RedstoneMode.values()[nbt.getByte(KEY_REDSTONE_MODE)];
		fuzzyMode = FuzzyMode.values()[nbt.getByte(KEY_FUZZY_MODE)];
		craftOnly = YesNo.values()[nbt.getByte(KEY_CRAFT_ONLY)];
	}

	@Override
	public void onInventoryChanged() {
		this.filterSize = 0;
		this.redstoneControlled = false;
		this.inverted = false;
		this.fuzzyCompare = false;
		this.autoCrafting = false;
		this.upgradeSpeedCount = 0;

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
	}

	public void acceptVal(Enum val) {
		if (val instanceof RedstoneMode) {
			redstoneMode = (RedstoneMode) val;
		} else if (val instanceof FuzzyMode) {
			fuzzyMode = (FuzzyMode) val;
		} else if (val instanceof YesNo) {
			craftOnly = (YesNo) val;
		}
	}
}

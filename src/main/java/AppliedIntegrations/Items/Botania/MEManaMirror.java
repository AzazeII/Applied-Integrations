package AppliedIntegrations.Items.Botania;
import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.ItemEnergyWirelessTerminal;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.api.Botania.IAEManaStack;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.features.INetworkEncodable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;
import appeng.me.helpers.BaseActionSource;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Optional;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "vazkii.botania.api.mana.IManaItem", modid = "botania", striprefs = true),
		@Optional.Interface(iface = "vazkii.botania.api.mana.IManaTooltipDisplay", modid = "botania", striprefs = true)})
/**
 * @Author Azazell
 */
public class MEManaMirror extends ItemEnergyWirelessTerminal implements IAEItemPowerStorage, INetworkEncodable, IBotaniaIntegrated, IManaItem, IManaTooltipDisplay {
	private static final double ENERGY_CONSUMPTION_PER_MANA = 0.005;

	public MEManaMirror(String registry) {
		super(registry);
		AEApi.instance().registries().wireless().registerWirelessHandler(this);
	}

	private IStorageChannel<IAEManaStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
	}

	private IMEMonitor<IAEManaStack> getManaInventory(IGrid targetGrid) {
		IStorageGrid storage = targetGrid.getCache(IStorageGrid.class);

		return storage.getInventory(getChannel());
	}

	@Override
	public int getMana(ItemStack stack) {
		// Tunnel network mana to this method
		IGrid grid = getGrid(stack);

		if (grid == null) {
			return 0;
		}

		// Read mana quantity from inventory into new mana list
		IMEMonitor<IAEManaStack> inventory = getManaInventory(grid);
		IItemList<IAEManaStack> list = inventory.getStorageList();

		// Get first and only(since we have only one type of mana) stack
		IAEManaStack manaStack = list.getFirstItem();

		if (manaStack != null) {
			// Calculate potential extractable mana count and actual mana count
			int extractableManaCount = (int) (getAECurrentPower(stack) / ENERGY_CONSUMPTION_PER_MANA);
			int stackSize = (int) manaStack.getStackSize();

			// We need to return only that amount of mana which we can extract in future with our energy
			return Math.min(stackSize, extractableManaCount);
		}

		return 0;
	}

	@Override
	public void addMana(ItemStack stack, int mana) {
		IGrid grid = getGrid(stack);

		if (grid == null) {
			return;
		}

		// Extract energy for operation
		double extracted = extractAEPower(stack, Math.abs(mana) * ENERGY_CONSUMPTION_PER_MANA, Actionable.SIMULATE);

		// Check if we have enough energy
		if (extracted > 0) {
			// Consume energy for operation
			extractAEPower(stack, Math.abs(mana) * ENERGY_CONSUMPTION_PER_MANA, Actionable.MODULATE);

			// Extract only that amount of mana, which mirror can power
			mana = (mana < 0 ? -1 : 1) * (int) (extracted / ENERGY_CONSUMPTION_PER_MANA);

			// Tunnel network mana to this method
			// Read mana quantity from inventory into new mana list
			IMEInventory<IAEManaStack> inventory = getManaInventory(grid);

			// Inject (or extract if it's value is negative) mana
			if (mana < 0) {
				inventory.extractItems(getChannel().createStack(Math.abs(mana)), Actionable.MODULATE, new BaseActionSource());
			} else if (mana > 0) {
				inventory.injectItems(getChannel().createStack(Math.abs(mana)), Actionable.MODULATE, new BaseActionSource());
			}
		}
	}

	@Override
	public int getMaxMana(ItemStack stack) {
		return getMana(stack);
	}

	@Override
	public boolean canReceiveManaFromPool(ItemStack itemStack, TileEntity tileEntity) {
		return false;
	}

	@Override
	public boolean canReceiveManaFromItem(ItemStack itemStack, ItemStack itemStack1) {
		return false;
	}

	@Override
	public boolean canExportManaToPool(ItemStack itemStack, TileEntity tileEntity) {
		return true;
	}

	@Override
	public boolean canExportManaToItem(ItemStack itemStack, ItemStack itemStack1) {
		return true;
	}

	@Override
	public boolean isNoExport(ItemStack itemStack) {
		return false;
	}

	@Override
	public float getManaFractionForDisplay(ItemStack itemStack) {
		return (float) getMana(itemStack) / (float) getMaxMana(itemStack);
	}

	@Override
	public boolean canHandle(ItemStack itemStack) {
		return itemStack.getItem() == ItemEnum.ITEMMANAWIRELESSMIRROR.getItem();
	}
}

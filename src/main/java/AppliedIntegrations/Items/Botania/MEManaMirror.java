package AppliedIntegrations.Items.Botania;
import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.api.Botania.IAEManaStack;
import AppliedIntegrations.api.Botania.IManaStorageChannel;
import AppliedIntegrations.grid.Mana.AEManaStack;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.features.ILocatable;
import appeng.api.features.INetworkEncodable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;
import appeng.api.util.IConfigManager;
import appeng.core.localization.GuiText;
import appeng.me.helpers.BaseActionSource;
import appeng.util.Platform;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.api.mana.IManaTooltipDisplay;

import java.util.List;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "vazkii.botania.api.mana.IManaItem", modid = "botania", striprefs = true), @Optional.Interface(iface = "vazkii.botania.api.mana.IManaTooltipDisplay", modid = "botania", striprefs = true),})
/**
 * @Author Azazell
 */
public class MEManaMirror extends AIItemRegistrable implements IWirelessTermHandler, IAEItemPowerStorage, INetworkEncodable, IBotaniaIntegrated, IManaItem, IManaTooltipDisplay {
	private static final AEManaStack BASE_MANA_REQUEST = new AEManaStack(1000);
	private final double capacity = 16000;

	private double storage;
	private static final String TAG_KEY = "encryptionKey";

	private final int manaCapacity = 500000;

	public MEManaMirror(String registry) {
		super(registry);
		this.setMaxStackSize(1);
		AEApi.instance().registries().wireless().registerWirelessHandler(this);
	}

	private IStorageChannel<IAEManaStack> getChannel() {
		return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
	}

	private IMEMonitor<IAEManaStack> getManaInventory(IGrid targetGrid) {
		IStorageGrid storage = targetGrid.getCache(IStorageGrid.class);

		return storage.getInventory(getChannel());
	}

	private IGrid getGrid(ItemStack stack) {
		ILocatable obj = null;

		try {
			// Get object from parsed long from nbt tag
			obj = AEApi.instance().registries().locatable().getLocatableBy( Long.parseLong( getEncryptionKey(stack) ));
		} catch( final NumberFormatException err ) {
			// :P
		}

		if( obj instanceof IActionHost) {
			// Now use object as medium for accessing to grid
			final IGridNode n = ( (IActionHost) obj ).getActionableNode();
			final IGrid targetGrid = n.getGrid();

			// Extract mana from network and inject into stack
			return targetGrid;
		}

		return null;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(final World w, final EntityPlayer player, final EnumHand hand) {
		AEApi.instance().registries().wireless().openWirelessTerminalGui(player.getHeldItem(hand), w, player);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isFull3D() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
		// Add stored energy quantity
		lines.add(I18n.translateToLocal("Energy Stored") + ": " + this.getAECurrentPower(stack) + " - " + (this.getAECurrentPower(stack) / this.getAEMaxPower(stack)) * 100 + "%");

		// Add GUI tip of linked or unlinked terminal
		if (stack.hasTagCompound()) {
			// Read key from tag
			final NBTTagCompound tag = Platform.openNbtData(stack);
			if (tag != null) {
				final String encKey = tag.getString(TAG_KEY);

				if (encKey.isEmpty()) {
					lines.add(GuiText.Unlinked.getLocal());
				} else {
					lines.add(GuiText.Linked.getLocal());
				}
			}
		} else {
			lines.add(I18n.translateToLocal(GuiText.Unlinked.getLocal()));
		}
	}

	@Override
	public double injectAEPower(ItemStack itemStack, double v, Actionable actionable) {
		double injected = Math.min(storage + v, capacity);
		if (actionable == Actionable.MODULATE) {
			storage = injected;
		}
		return injected;
	}

	@Override
	public double extractAEPower(ItemStack itemStack, double v, Actionable actionable) {
		double extracted = Math.min(storage - v, capacity);
		if (actionable == Actionable.MODULATE) {
			storage = extracted;
		}
		return extracted;
	}

	@Override
	public double getAEMaxPower(ItemStack itemStack) {
		return storage;
	}

	@Override
	public double getAECurrentPower(ItemStack itemStack) {
		return capacity;
	}

	@Override
	public AccessRestriction getPowerFlow(ItemStack itemStack) {
		return AccessRestriction.READ;
	}

	@Override
	public int getMana(ItemStack stack) {
		// Tunnel network mana to this method
		IGrid grid = getGrid(stack);

		if (grid == null) {
			return 0;
		}

		// Read mana quantity from inventory into new mana list
		IMEInventory<IAEManaStack> inventory = getManaInventory(grid);
		IItemList<IAEManaStack> list = getChannel().createList();

		inventory.getAvailableItems(list);

		// Get first and only(since we have only one type of mana) stack
		IAEManaStack manaStack = list.getFirstItem();

		if (manaStack != null) {
			return (int) manaStack.getStackSize();
		}

		return 0;
	}

	@Override
	public int getMaxMana(ItemStack itemStack) {
		return manaCapacity;
	}

	@Override
	public void addMana(ItemStack stack, int mana) {
		// Tunnel network mana to this method
		IGrid grid = getGrid(stack);

		if (grid == null) {
			return;
		}

		// Read mana quantity from inventory into new mana list
		IMEInventory<IAEManaStack> inventory = getManaInventory(grid);

		// Inject or extract mana if it's value is negative
		if (mana < 0) {
			inventory.extractItems(getChannel().createStack(Math.abs(mana)), Actionable.MODULATE, new BaseActionSource());
		} else if (mana > 0) {
			inventory.injectItems(getChannel().createStack(Math.abs(mana)), Actionable.MODULATE, new BaseActionSource());
		}
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
	public String getEncryptionKey(ItemStack stack) {
		NBTTagCompound tag = Platform.openNbtData(stack);

		return tag.hasKey(TAG_KEY) ? tag.getString(TAG_KEY) : "";
	}

	@Override
	public void setEncryptionKey(ItemStack stack, String s, String s1) {
		NBTTagCompound tag = Platform.openNbtData(stack);

		tag.setString(TAG_KEY, s);
	}

	@Override
	public boolean canHandle(ItemStack itemStack) {
		return itemStack.getItem() == ItemEnum.ITEMMANAWIRELESSMIRROR.getItem();
	}

	@Override
	public boolean usePower(EntityPlayer entityPlayer, double v, ItemStack itemStack) {
		return this.extractAEPower(itemStack, v, Actionable.MODULATE) >= v - 0.5;
	}

	@Override
	public boolean hasPower(EntityPlayer entityPlayer, double v, ItemStack itemStack) {
		return getAECurrentPower(itemStack) >= v;
	}

	@Override
	public IConfigManager getConfigManager(ItemStack itemStack) {
		return null;
	}
}

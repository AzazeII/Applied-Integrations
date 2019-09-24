package AppliedIntegrations.Items;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.features.INetworkEncodable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.util.IConfigManager;
import appeng.core.localization.GuiText;
import appeng.util.Platform;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.MessageFormat;
import java.util.List;

/**
 * @Author Azazell
 */
public class ItemEnergyWirelessTerminal extends AIItemRegistrable implements IWirelessTermHandler, INetworkEncodable, IAEItemPowerStorage {
	private static final String TAG_KEY = "#encryption_key";
	private static final String TAG_ENERGY = "#energy_stored";
	protected final double capacity = 1600000;

	public ItemEnergyWirelessTerminal(String name) {
		super(name);
		this.setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isFull3D() {
		return false;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}
	@Override
	public boolean isRepairable() {
		return false;
	}
	@Override
	public boolean isDamaged( final ItemStack stack ) {
		return true;
	}
	@Override
	public void setDamage( final ItemStack stack, final int damage ) {

	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(final ItemStack stack, final World world, final List<String> lines, final ITooltipFlag advancedTooltips) {
		// Calculate energy percentage
		double percent = getAECurrentPower(stack) / getAEMaxPower(stack) * 100;

		// Add stored energy quantity
		lines.add(GuiText.StoredEnergy.getLocal() + ": " + MessageFormat.format( " {0,number,#} ", getAECurrentPower(stack) )
				+ " - " + MessageFormat.format( " {0,number,#.##%} ", percent ) + "%");

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
	public ActionResult<ItemStack> onItemRightClick(final World w, final EntityPlayer player, final EnumHand hand) {
		AEApi.instance().registries().wireless().openWirelessTerminalGui(player.getHeldItem(hand), w, player);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}

	@Override
	public double getDurabilityForDisplay( final ItemStack is ) {
		return 1 - getAECurrentPower( is ) / getAEMaxPower( is );
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
	public double injectAEPower( final ItemStack stack, final double v, Actionable actionable ) {
		// Calculate constants
		final double maxStorage = this.getAEMaxPower( stack );
		final double currentStorage = this.getAECurrentPower( stack );
		final double required = maxStorage - currentStorage;
		final double overflow = v - required;

		if( actionable == Actionable.MODULATE ) {
			// Inject energy, calculate energy amount to add
			final NBTTagCompound data = Platform.openNbtData( stack );
			final double toAdd = Math.min( v, required );

			data.setDouble( TAG_ENERGY, currentStorage + toAdd );
		}

		return Math.max( 0, overflow );
	}

	@Override
	public double extractAEPower(ItemStack stack, double v, Actionable actionable) {
		// Calculate amount we can extract
		final double currentStorage = this.getAECurrentPower( stack );
		final double extractable = Math.min( v, currentStorage );

		if( actionable == Actionable.MODULATE ) {
			// Extract energy
			final NBTTagCompound data = Platform.openNbtData( stack );

			data.setDouble(TAG_ENERGY, currentStorage - extractable);
		}

		return extractable;
	}

	@Override
	public double getAEMaxPower(ItemStack itemStack) {
		return capacity;
	}

	@Override
	public double getAECurrentPower(ItemStack stack) {
		final NBTTagCompound data = Platform.openNbtData( stack );

		return data.getDouble(TAG_ENERGY);
	}

	@Override
	public AccessRestriction getPowerFlow(ItemStack itemStack) {
		return AccessRestriction.WRITE;
	}

	@Override
	public boolean canHandle(ItemStack itemStack) {
		return itemStack.getItem() == ItemEnum.ITEMENERGYWIRELESSTERMINAL.getItem();
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

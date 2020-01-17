package AppliedIntegrations.Items;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.features.ILocatable;
import appeng.api.features.INetworkEncodable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.capabilities.Capabilities;
import appeng.core.localization.GuiText;
import appeng.items.tools.powered.ToolWirelessTerminal;
import appeng.tile.networking.TileWireless;
import appeng.util.Platform;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.List;

/**
 * @Author Azazell
 */
public class ItemEnergyWirelessTerminal extends AIItemRegistrable implements IWirelessTermHandler, INetworkEncodable, IAEItemPowerStorage {
	// Make other mods be able to interact with our AE storage
	private static class EnergyTerminalCapabilityDelegate implements ICapabilityProvider, IEnergyStorage {
		private final ItemStack is;

		private final IAEItemPowerStorage item;

		private final Object teslaAdapter;

		EnergyTerminalCapabilityDelegate(ItemStack is, IAEItemPowerStorage item ) {
			this.is = is;
			this.item = item;

			if( Capabilities.TESLA_CONSUMER != null || Capabilities.TESLA_HOLDER != null ) {
				this.teslaAdapter = new TeslaPowerDelegate();
			} else {
				this.teslaAdapter = null;
			}
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing ) {
			return capability == Capabilities.FORGE_ENERGY || capability == Capabilities.TESLA_CONSUMER || capability == Capabilities.TESLA_HOLDER;
		}

		@SuppressWarnings( "unchecked" )
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing ) {
			if( capability == Capabilities.FORGE_ENERGY ) {
				return (T) this;
			} else if( capability == Capabilities.TESLA_CONSUMER || capability == Capabilities.TESLA_HOLDER ) {
				return (T) this.teslaAdapter;
			}
			return null;
		}

		@Override
		public int receiveEnergy( int maxReceive, boolean simulate ) {
			final double convertedOffer = PowerUnits.RF.convertTo( PowerUnits.AE, maxReceive );
			final double overflow = this.item.injectAEPower( this.is, convertedOffer, simulate ? Actionable.SIMULATE : Actionable.MODULATE );

			return maxReceive - (int) PowerUnits.AE.convertTo( PowerUnits.RF, overflow );
		}

		@Override
		public int extractEnergy( int maxExtract, boolean simulate ) {
			return 0; // Ignored for terminal
		}

		@Override
		public int getEnergyStored() {
			return (int) PowerUnits.AE.convertTo( PowerUnits.RF, this.item.getAECurrentPower( this.is ) );
		}

		@Override
		public int getMaxEnergyStored() {
			return (int) PowerUnits.AE.convertTo( PowerUnits.RF, this.item.getAEMaxPower( this.is ) );
		}

		@Override
		public boolean canExtract()
		{
			return false;
		}

		@Override
		public boolean canReceive()
		{
			return true;
		}

		private class TeslaPowerDelegate implements ITeslaConsumer, ITeslaHolder {
			@Override
			public long givePower( long power, boolean simulated ) {
				return EnergyTerminalCapabilityDelegate.this.receiveEnergy( (int) power, simulated );
			}

			@Override
			public long getStoredPower() {
				return EnergyTerminalCapabilityDelegate.this.getEnergyStored();
			}

			@Override
			public long getCapacity() {
				return EnergyTerminalCapabilityDelegate.this.getMaxEnergyStored();
			}
		}
	}

	private static final String TAG_KEY = "#encryption_key";
	private static final String TAG_ENERGY = "#energy_stored";

	private static final String TAG_SQUARED_RANGE = "#squared_range";
	private static final String TAG_RANGE = "#range";

	protected final double capacity = 1600000;

	public ItemEnergyWirelessTerminal(String name) {
		super(name);
		this.setMaxStackSize(1);

		AEApi.instance().registries().wireless().registerWirelessHandler(this);
	}

	protected IGrid getGrid(ItemStack stack) {
		ILocatable obj = null;

		try {
			obj = AEApi.instance().registries().locatable().getLocatableBy( Long.parseLong( getEncryptionKey(stack) ));
		} catch( final NumberFormatException err ) {}

		if( obj instanceof IActionHost) {
			// Now use object as medium for accessing to grid
			final IGridNode n = ( (IActionHost) obj ).getActionableNode();
			return n.getGrid();
		}

		return null;
	}

	protected boolean isNotInRange(ItemStack stack, IGrid grid, BlockPos pos, int worldID) {
		NBTTagCompound tag = Platform.openNbtData(stack);ToolWirelessTerminal t;

		// Maximize both ranges
		tag.setDouble(TAG_RANGE, Double.MAX_VALUE);
		tag.setDouble(TAG_SQUARED_RANGE, Double.MAX_VALUE);

		if( grid != null ) {
			// Try to find nearest wireless access point
			final IMachineSet tw = grid.getMachines( TileWireless.class );

			IWirelessAccessPoint wap = null;
			for( final IGridNode n : tw ) {
				final IWirelessAccessPoint point = (IWirelessAccessPoint) n.getMachine();
				if( this.isWapInRange( point, tag, pos, worldID ) ) {
					wap = point;
				}
			}

			return wap == null;
		}

		return true;
	}

	private boolean isWapInRange(final IWirelessAccessPoint wap, NBTTagCompound tag, BlockPos pos, int worldID) {
		// Calculate squared range limit and get location of WAP
		double rangeLimit = wap.getRange();
		rangeLimit *= rangeLimit;
		DimensionalCoord dc = wap.getLocation();

		if( dc.getWorld().provider.getDimension() == worldID ) {
			// Calculate vector difference between WAP and player
			final double offX = dc.x - pos.getX();
			final double offY = dc.y - pos.getY();
			final double offZ = dc.z - pos.getZ();

			final double r = offX * offX + offY * offY + offZ * offZ;
			if( r < rangeLimit && tag.getDouble(TAG_SQUARED_RANGE) > r ) {
				if( wap.isActive() ) {
					tag.setDouble(TAG_SQUARED_RANGE, r);
					tag.setDouble(TAG_RANGE, Math.sqrt(r));

					return true;
				}
			}
		}

		return false;
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
		double percent = getAECurrentPower(stack) / getAEMaxPower(stack);
		lines.add(GuiText.StoredEnergy.getLocal() + ": " + MessageFormat.format( " {0,number,#} ", getAECurrentPower(stack) )
				+ " - " + MessageFormat.format( " {0,number,#.##%} ", percent ) + "%");

		if (stack.hasTagCompound()) {
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
		final double maxStorage = this.getAEMaxPower( stack );
		final double currentStorage = this.getAECurrentPower( stack );
		final double required = maxStorage - currentStorage;
		final double overflow = v - required;

		if( actionable == Actionable.MODULATE ) {
			final NBTTagCompound data = Platform.openNbtData( stack );
			final double toAdd = Math.min( v, required );

			data.setDouble( TAG_ENERGY, currentStorage + toAdd );
		}

		return Math.max( 0, overflow );
	}

	@Override
	public double extractAEPower(ItemStack stack, double v, Actionable actionable) {
		final double currentStorage = this.getAECurrentPower( stack );
		final double extractable = Math.min( v, currentStorage );

		if( actionable == Actionable.MODULATE ) {
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

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt ) {
		return new EnergyTerminalCapabilityDelegate(stack, this);
	}
}

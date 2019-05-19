package AppliedIntegrations.Parts.P2P;


import AppliedIntegrations.api.AIApi;
import appeng.api.implementations.items.IMemoryCard;
import appeng.api.implementations.items.MemoryCardMessages;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartItemStack;
import appeng.api.util.AEColor;
import appeng.api.util.AEPartLocation;
import appeng.me.GridAccessException;
import appeng.me.cache.P2PCache;
import appeng.parts.p2p.PartP2PTunnel;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;

/**
 * @Author Azazell
 */
public abstract class AIPartP2PTunnel<T extends AIPartP2PTunnel<T>> extends PartP2PTunnel<T> {
	// Overridden field, now tunnels can access it's output value
	private boolean isOutput;

	public AIPartP2PTunnel(ItemStack is) {
		super(is);
	}

	@Override
	public boolean isOutput() {
		return isOutput;
	}

	protected void setOutput(boolean newValue) {
		isOutput = newValue;
	}

	private boolean loadPart(ItemStack newType, EntityPlayer player, EnumHand hand, short freq){
		// Remove part
		this.getHost().removePart( this.getSide(), true );

		// Create new part
		final AEPartLocation dir = this.getHost().addPart( newType, this.getSide(), player, hand );

		// Get new part
		final IPart newBus = this.getHost().getPart( dir );

		// Check if new part is AI p2p tunnel
		if( newBus instanceof AIPartP2PTunnel ) {
			// Cast to tunnel
			final AIPartP2PTunnel newTunnel = (AIPartP2PTunnel) newBus;

			// Set tunnel mode to output
			newTunnel.setOutput(true);

			// Notify part
			newTunnel.onTunnelNetworkChange();

			try {
				// Get p2p cache
				final P2PCache p2p = newTunnel.getProxy().getP2P();

				// Update frequency in cache
				p2p.updateFreq( newTunnel, freq );
			} catch( final GridAccessException ignored ){}

			return true;
		}

		return false;
	}

	@Override
	public ItemStack getItemStack( final PartItemStack type ) {
		// Pass call to super, to make p2p tunnels drop them self instead of dropping p2p tunnel - ME
		return super.getItemStack(type);
	}

	@Override
	public boolean onPartActivate(final EntityPlayer player, final EnumHand hand, final Vec3d pos ) {
		// Call only on client
		if( getLogicalSide() == CLIENT ) {
			return true;
		}

		// Call only for main hand
		if( hand == EnumHand.OFF_HAND ) {
			return false;
		}

		// Get item stack in player hand
		ItemStack is = player.getHeldItem(hand);

		// Get new tunnel stack from API
		ItemStack newPart = Objects.requireNonNull(AIApi.instance()).getTunnelFromStack(is.getItem());

		// Check if stack isn't empty and item in stack is memory card
		if( !is.isEmpty() && is.getItem() instanceof IMemoryCard) {
			// Cast item to memory card
			final IMemoryCard mc = (IMemoryCard) is.getItem();

			// Get card NBT tag
			final NBTTagCompound data = mc.getData( is );

			// Get new type from nbt tag
			final ItemStack newType = new ItemStack( data );

			// Get frequency
			final short freq = data.getShort( "freq" );

			// Check if new type not empty
			if( !newType.isEmpty() ) {
				// Check if new type instance of part item
				if( newType.getItem() instanceof IPartItem) {
					// Trace test part
					final IPart testPart = ( (IPartItem) newType.getItem() ).createPartFromItemStack( newType );

					// Check if test part is p2p tunnel
					if( testPart instanceof PartP2PTunnel ) {
						// Load part; Check if part has loaded successfully
						if(loadPart(newType, player, hand, freq)){
							// Notify player in chat about successful loading settings
							mc.notifyUser( player, MemoryCardMessages.SETTINGS_LOADED );

							return true;
						}
					}
				}
			}

			// Notify player in chat about invalid machine
			mc.notifyUser( player, MemoryCardMessages.INVALID_MACHINE );
		}else if (newPart != null && !newPart.isEmpty()) {
			// Save output and frequency
			// boolean oldOutput = this.isOutput(); // (1)
			short myFreq = this.getFrequency(); // (2)

			// Load part from data
			loadPart(newPart, player, hand, myFreq);

			// Notify neighbor blocks
			Platform.notifyBlocksOfNeighbors( this.getTile().getWorld(), this.getTile().getPos() );

			return true;
		}

		return false;
	}

	// Copied from super-class, only line changed(except for comments):
	// setOutput(false) -> isOutput = false
	@Override
	public boolean onPartShiftActivate( final EntityPlayer player, final EnumHand hand, final Vec3d pos ) {
		// Get current item in inventory of player
		final ItemStack is = player.inventory.getCurrentItem();

		// Check if stack isn't empty and item of stack is memory card
		if( !is.isEmpty() && is.getItem() instanceof IMemoryCard ) {
			// Call only on server
			if( Platform.isClient() ) {
				return true;
			}

			// Cast stack to card
			final IMemoryCard mc = (IMemoryCard) is.getItem();

			// Get data from card
			final NBTTagCompound data = mc.getData( is );

			// Get frequency from tag
			final short storedFrequency = data.getShort( "freq" );

			// Get current frequency
			short newFreq = this.getFrequency();

			// Store current output value
			final boolean wasOutput = this.isOutput();

			// make tunnel not output
			setOutput(false);

			// True if tunnel was output or, frequency equal to zero, or frequency equal to new frequency
			final boolean needsNewFrequency = wasOutput || this.getFrequency() == 0 || storedFrequency == newFreq;

			try {
				// Check if tunnel needs new frequency
				if( needsNewFrequency ) {
					// generate new frequency from p2p cache
					newFreq = this.getProxy().getP2P().newFrequency();
				}

				// Update tunnel frequency
				this.getProxy().getP2P().updateFreq( this, newFreq );
			} catch( final GridAccessException ignored ) { }

			// Notify tunnel
			this.onTunnelConfigChange();

			// Get p2p from stack
			final ItemStack p2pItem = this.getItemStack( PartItemStack.WRENCH );

			// Get type
			final String type = p2pItem.getUnlocalizedName();

			// Write data to tag
			p2pItem.writeToNBT( data );

			// Write frequency
			data.setShort( "freq", this.getFrequency() );

			// Get colors from freq
			final AEColor[] colors = Platform.p2p().toColors( this.getFrequency() );

			// Create color code array
			final int[] colorCode = new int[] {
					colors[0].ordinal(), colors[0].ordinal(), colors[1].ordinal(), colors[1].ordinal(),
					colors[2].ordinal(), colors[2].ordinal(), colors[3].ordinal(), colors[3].ordinal(),
			};

			// Write array to data
			data.setIntArray( "colorCode", colorCode );

			// Set memory contend
			mc.setMemoryCardContents( is, type + ".name", data );

			// If tunnel need new freq
			if( needsNewFrequency ) {
				// Notify player in chat
				mc.notifyUser( player, MemoryCardMessages.SETTINGS_RESET );
			} else {
				// Notify player in chat
				mc.notifyUser( player, MemoryCardMessages.SETTINGS_SAVED );
			}
			return true;
		}
		return false;
	}

	@Override
	public void readFromNBT( final NBTTagCompound data ) {
		super.readFromNBT( data );

		// Load isOutput tag
		isOutput = data.getBoolean("output");
	}
}

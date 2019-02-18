package AppliedIntegrations.Container;

import AppliedIntegrations.API.*;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class ContainerEnergyStorage
		extends ContainerWithNetworkTool
{
	/**
	 * X position offset for upgrade slots
	 */
	private static final int UPGRADE_SLOT_X = 187;

	/**
	 * Y position offset for upgrade slots
	 */
	private static final int UPGRADE_SLOT_Y = 8;

	/**
	 * Y position for the player inventory
	 */
	private static int PLAYER_INV_POSITION_Y = 102;

	/**
	 * Y position for the hotbar inventory
	 */
	private static int HOTBAR_INV_POSITION_Y = 160;

	/**
	 * The Energy storage bus.
	 */
	private final PartEnergyStorage storageBus;

	/**
	 * Cache of filteredEnergies.
	 */
	private final ArrayList<LiquidAIEnergy> filteredEnergies = new ArrayList<LiquidAIEnergy>( PartEnergyStorage.FILTER_SIZE );

	/**
	 * Cache of isVoidAllowed.
	 */
	private boolean isVoidAllowed = false;

	public ContainerEnergyStorage(final PartEnergyStorage part, final EntityPlayer player )
	{
		super(part,player);
		// Call super


		// Set the part
		this.storageBus = part;
		part.listeners.add(this);

		for( int i = 0; i < PartEnergyStorage.FILTER_SIZE; ++i )
		{
			this.filteredEnergies.add( null );
		}

		// Add the upgrade slot
		this.addUpgradeSlots( part.getUpgradeInventory(), 1, this.UPGRADE_SLOT_X,
				this.UPGRADE_SLOT_Y );

		// Bind to the player's inventory
		this.bindPlayerInventory( player.inventory, this.PLAYER_INV_POSITION_Y+67,
				this.HOTBAR_INV_POSITION_Y+67 );


	}

	@Override
	protected boolean detectAndSendChangesMP( final EntityPlayerMP playerMP )
	{
		// Has the filtered list changed?
		boolean updateFilters = false;
		for( int filterIndex = 0; filterIndex < PartEnergyStorage.FILTER_SIZE; ++filterIndex )
		{
			if( this.filteredEnergies.get( filterIndex ) != this.storageBus.getFilteredEnergy( filterIndex ) )
			{
				// Found mismatch
				this.filteredEnergies.set( filterIndex, this.storageBus.getFilteredEnergy( filterIndex ) );
				updateFilters = true;
			}
		}
		if( updateFilters )
		{
			// Update the client

		}


		return false;
	}

	@Override
	public void onContainerClosed(EntityPlayer p)
	{
		super.onContainerClosed(p);
		this.storageBus.listeners.remove(this);
	}

	@Override
	public boolean onFilterReceive(AIPart part) {
		return part == this.storageBus;
	}

	@Override
	public boolean canInteractWith( final EntityPlayer player )
	{
		return true;
	}

	public void setFilteredEnergies(final List<LiquidAIEnergy> filteredEnergies)
	{

	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber )
	{
		// Get the slot
		Slot slot = this.getSlotOrNull( slotNumber );

		// Do we have a valid slot with an item?
		if( ( slot != null ) && ( slot.getHasStack() ) )
		{
			if( ( this.storageBus != null ) && ( this.storageBus.addFilteredEnergyFromItemstack( player, slot.getStack() ) ) )
			{
				return null;
			}

			// Pass to super
			return super.transferStackInSlot( player, slotNumber );
		}

		return null;
	}
}

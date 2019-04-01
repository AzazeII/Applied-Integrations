package AppliedIntegrations.Container.part;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Container.ContainerWithNetworkTool;
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

	public ContainerEnergyStorage(final PartEnergyStorage part, final EntityPlayer player )
	{
		super(part,player);
		// Call super

		// Add listener
		part.linkedListeners.add(this);

		// Set the part
		this.storageBus = part;

		for( int i = 0; i < PartEnergyStorage.FILTER_SIZE; ++i )
		{
			this.filteredEnergies.add( null );
		}

		// Add the upgrade slot
		/*this.addUpgradeSlots( part.getUpgradeInventory(), 1, this.UPGRADE_SLOT_X,
				this.UPGRADE_SLOT_Y );*/

		// Bind to the player's inventory
		this.bindPlayerInventory( player.inventory, this.PLAYER_INV_POSITION_Y+67,
				this.HOTBAR_INV_POSITION_Y+67 );


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

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber )
	{
		// Get the slot
		Slot slot = this.getSlotOrNull( slotNumber );

		// Do we have a valid slot with an item?
		if( ( slot != null ) && ( slot.getHasStack() ) )
		{
			/*if( ( this.storageBus != null ) && ( this.storageBus.addFilteredEnergyFromItemstack( player, slot.getStack() ) ) )
			{
				return null;
			}*/

			// Pass to super
			return super.transferStackInSlot( player, slotNumber );
		}

		return null;
	}
}

package AppliedIntegrations.Container;

import AppliedIntegrations.API.IEnergyInterface;
import AppliedIntegrations.Container.slot.SlotNetworkTool;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.AEApi;
import appeng.api.implementations.guiobjects.IGuiItem;
import appeng.api.implementations.guiobjects.INetworkTool;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.api.util.DimensionalCoord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.init.Items.AIR;

/**
 * @Author Azazell
 */
public abstract class ContainerWithNetworkTool
        extends ContainerWithPlayerInventory
{
    /**
     * Number of rows in the network tool
     */
    private static final int TOOL_ROWS = 3;

    /**
     * Number of columns in the network tool
     */
    private static final int TOOL_COLUMNS = 3;

    /**
     * X position to start drawing slots
     */
    private static final int TOOL_SLOT_X_OFFSET = 187;

    /**
     * Y position to start drawing slots
     */
    private static final int TOOL_SLOT_Y_OFFSET = 102;


    /**
     * Index of the first network tool slot
     */
    private int firstToolSlotNumber = -1;

    /**
     * Index of the last network tool slot
     */
    private int lastToolSlotNumber = -1;

    /**
     * Index of the first upgrade slot
     */
    private int firstUpgradeSlot = -1;

    /**
     * Index of the first upgrade slot
     */
    private int lastUpgradeSlot = -1;

    /**
     * How far apart each slot should be drawn
     */
    private static int UPGRADE_Y_POSITION_MULTIPLIER = 18;
    /**
     * The part associated with this container
     */
    private AIOPart bus;

    public ContainerWithNetworkTool(IEnergyInterface Einterface, EntityPlayer player) {
super(player);
    }

    public ContainerWithNetworkTool(PartEnergyStorage EStorage, EntityPlayer player) {
        super(player);
    }

    protected void addUpgradeSlots(final AIGridNodeInventory upgradeInventory, final int count, final int xPosition, final int yPosition )
    {
        Slot upgradeSlot = null;

        // Add the upgrade slots
        for( int slotIndex = 0; slotIndex < count; slotIndex++ )
        {
            // Create the slot
            upgradeSlot = new SlotRestrictive( upgradeInventory, slotIndex, xPosition, yPosition +
                    ( slotIndex * this.UPGRADE_Y_POSITION_MULTIPLIER ) );

            // Add the slot
            this.addSlotToContainer( upgradeSlot );

            // Check first
            if( slotIndex == 0 )
            {
                this.firstUpgradeSlot = upgradeSlot.slotNumber;
            }
        }

        // Set last
        if( upgradeSlot != null )
        {
            this.lastUpgradeSlot = upgradeSlot.slotNumber;
        }
    }

    protected boolean hasNetworkTool = false;
    public ContainerWithNetworkTool(AIOPart part, final EntityPlayer player)
    {
        super( player );
    }


    /**
     * Attempt to merge the specified slot stack with the tool inventory
     *
     * @param slotStack
     * @return
     */
    protected boolean mergeSlotWithNetworkTool( final ItemStack slotStack )
    {
        if( this.hasNetworkTool )
        {
            // Is the item an upgrade card?
            if( !( slotStack.getItem() instanceof IUpgradeModule) )
            {
                // Not an upgrade module
                return false;
            }

            return this.mergeItemStack( slotStack, this.firstToolSlotNumber, this.lastToolSlotNumber + 1, false );
        }

        return false;
    }

    /**
     * Attempt to merge the specified slot stack with the upgrade inventory
     *
     * @param slotStack
     * @return
     */
    protected boolean mergeSlotWithUpgrades( final ItemStack slotStack )
    {
        // Is the item an upgrade card?
        if( !( slotStack.getItem() instanceof IUpgradeModule ) )
        {
            // Not an upgrade module
            return false;
        }

        boolean didMerge = false;

        // Are there any open slots in the upgrade inventory?
        for( int index = this.firstUpgradeSlot; index <= this.lastUpgradeSlot; index++ )
        {
            // Get the slot
            Slot upgradeSlot = (Slot)this.inventorySlots.get( index );

            // Is the slot empty?
            if( ( upgradeSlot != null ) && ( !upgradeSlot.getHasStack() ) )
            {
                // Can the slot accept this item?
                if( upgradeSlot.isItemValid( slotStack ) )
                {
                    // Create an itemstack of size 1
                    ItemStack upgradeStack = slotStack.copy();
                    upgradeStack.setCount(1);

                    // Place the stack in the upgrade slot
                    upgradeSlot.putStack( upgradeStack );

                    // Decrement the slot stack
                    slotStack.setCount(slotStack.getCount()-1);

                    // Mark that we merged
                    didMerge = true;

                    // Is the slot stack at zero?
                    if( slotStack.getCount() == 0 )
                    {
                        // Break the loop
                        break;
                    }
                }
            }
        }

        return didMerge;
    }

    /**
     * Checks if the slot clicked was in the network tools inventory
     *
     * @param slotNumber
     * @return True if it was in the tools inventory, false otherwise.
     */
    protected boolean slotClickedWasInNetworkTool( final int slotNumber )
    {
        return this.hasNetworkTool && ( slotNumber >= this.firstToolSlotNumber ) && ( slotNumber <= this.lastToolSlotNumber );
    }

    protected boolean slotClickedWasInUpgrades( final int slotNumber )
    {
        return ( slotNumber >= this.firstUpgradeSlot ) && ( slotNumber <= this.lastUpgradeSlot );
    }

    public boolean hasNetworkTool()
    {
        return this.hasNetworkTool;
    }

    /**
     * Checks if the shift+click happend in the network tool.
     */
    @Override
    public ItemStack transferStackInSlot( final EntityPlayer player, final int slotNumber )
    {
        // Note: mergeItemStack args: ItemStack stack, int slotStart, int slotEnd(exclusive), boolean reverse

        // Get the slot that was clicked on
        Slot slot = this.getSlotOrNull( slotNumber );

        boolean didMerge = false;

        // Did we get a slot, and does it have a valid item?
        if( ( slot != null ) && ( slot.getHasStack() ) )
        {
            // Get the slots item stack
            ItemStack slotStack = slot.getStack();

            // Was the slot clicked in the player or hotbar inventory?
            if( this.slotClickedWasInPlayerInventory( slotNumber ) || this.slotClickedWasInHotbarInventory( slotNumber ) )
            {
                // Attempt to merge with the upgrade inventory
                didMerge = this.mergeSlotWithUpgrades( slotStack );

                // Did we merge?
                if( !didMerge )
                {
                    // Attempt to merge with the network tool
                    didMerge = this.mergeSlotWithNetworkTool( slotStack );
                }

                // Did we merge?
                if( !didMerge )
                {
                    // Attempt to swap
                    didMerge = this.swapSlotInventoryHotbar( slotNumber, slotStack );
                }
            }
            // Was the slot clicked in the upgrades?
            else if( this.slotClickedWasInUpgrades( slotNumber ) )
            {
                // Attempt to merge with the network tool
                didMerge = this.mergeSlotWithNetworkTool( slotStack );

                // Did we merge with the network tool?
                if( !didMerge )
                {
                    // Attempt to merge with the player inventory
                    didMerge = this.mergeSlotWithPlayerInventory( slotStack );
                }
            }
            // Was the slot clicked in the network tool?
            else if( this.hasNetworkTool && this.slotClickedWasInNetworkTool( slotNumber ) )
            {
                // Attempt to merge with the upgrade inventory
                didMerge = this.mergeSlotWithUpgrades( slotStack );

                // Did we merge with the upgrades?
                if( !didMerge )
                {
                    // Attempt to merge with the player inventory
                    didMerge = this.mergeSlotWithPlayerInventory( slotStack );
                }
            }

            // Were we able to merge?
            if( !didMerge )
            {
                return null;
            }

            // Did the merger drain the stack?
            if( slotStack.getCount() == 0 )
            {
                // Set the slot to have no item
                slot.putStack( new ItemStack(AIR) );
            }

            // Inform the slot its stack changed;
            slot.onSlotChanged();

            // Sync
            this.detectAndSendChanges();
        }

        // Done ( returning null prevents retrySlotClick from being called )
        return null;
    }
}

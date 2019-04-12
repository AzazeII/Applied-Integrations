package AppliedIntegrations.Container;

import AppliedIntegrations.API.IEnergyInterface;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.implementations.items.IUpgradeModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static net.minecraft.init.Items.AIR;

/**
 * @Author Azazell
 */
public abstract class ContainerWithUpgradeSlots
        extends ContainerWithPlayerInventory
{
    // First tool slot
    private int firstToolSlotNumber = -1;

    // Last tool slot
    private int lastToolSlotNumber = -1;

    // First upgrade slot
    private int firstUpgradeSlot = -1;

    // Last upgrade slot
    private int lastUpgradeSlot = -1;

    // Distance between Y point, of each upgrade slot
    private static int UPGRADE_Y_POSITION_MULTIPLIER = 18;

    public ContainerWithUpgradeSlots(IEnergyInterface Einterface, EntityPlayer player) {
        super(player);
    }

    public ContainerWithUpgradeSlots(PartEnergyStorage EStorage, EntityPlayer player) {
        super(player);
    }

    protected void addUpgradeSlots(final AIGridNodeInventory upgradeInventory, final int count, final int xPosition, final int yPosition )
    {
        Slot upgradeSlot = null;

        // Add the upgrade slots
        for( int slotIndex = 0; slotIndex < count; slotIndex++ ) {
            // Create the slot
            upgradeSlot = new SlotRestrictive( upgradeInventory, slotIndex, xPosition, yPosition +
                    ( slotIndex * UPGRADE_Y_POSITION_MULTIPLIER ) ) {

                // Override icon getter for this slot
                @SideOnly(Side.CLIENT)
                public String getSlotTexture() {
                    return AppliedIntegrations.modid + ":gui/slots/upgradesloticon";
                }
            };

            // Add the slot
            this.addSlotToContainer( upgradeSlot );

            // Check first
            if( slotIndex == 0 ) {
                this.firstUpgradeSlot = upgradeSlot.slotNumber;
            }
        }

        // Set last
        if( upgradeSlot != null ) {
            this.lastUpgradeSlot = upgradeSlot.slotNumber;
        }
    }

    protected boolean hasNetworkTool = false;
    public ContainerWithUpgradeSlots(AIOPart part, final EntityPlayer player)
    {
        super( player );
    }


    // @Return true, if slots was merged successfully
    protected boolean mergeSlotWithNetworkTool( final ItemStack slotStack ) {
        if( this.hasNetworkTool ) {
            // Is the item an upgrade card?
            if( !( slotStack.getItem() instanceof IUpgradeModule) ) {
                // Not an upgrade module
                return false;
            }

            return this.mergeItemStack( slotStack, this.firstToolSlotNumber, this.lastToolSlotNumber + 1, false );
        }

        return false;
    }

    // @Return true, if slots was merged successfully
    protected boolean mergeSlotWithUpgrades( final ItemStack slotStack ) {
        // Is the item an upgrade card?
        if( !( slotStack.getItem() instanceof IUpgradeModule ) ) {
            // Not an upgrade module
            return false;
        }

        boolean didMerge = false;

        // Are there any open slots in the upgrade inventory?
        for( int index = this.firstUpgradeSlot; index <= this.lastUpgradeSlot; index++ ) {
            // Get the slot
            Slot upgradeSlot = (Slot)this.inventorySlots.get( index );

            // Is the slot empty?
            if( ( upgradeSlot != null ) && ( !upgradeSlot.getHasStack() ) ) {
                // Can the slot accept this item?
                if( upgradeSlot.isItemValid( slotStack ) ) {
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

    // Check if slot clicked from network tool inventory
    protected boolean slotClickedWasInNetworkTool( final int slotNumber ) {
        return this.hasNetworkTool && ( slotNumber >= this.firstToolSlotNumber ) && ( slotNumber <= this.lastToolSlotNumber );
    }

    protected boolean slotClickedWasInUpgrades( final int slotNumber ) {
        return ( slotNumber >= this.firstUpgradeSlot ) && ( slotNumber <= this.lastUpgradeSlot );
    }

    public boolean hasNetworkTool()
    {
        return this.hasNetworkTool;
    }

    // Shift+click on slot event
    @Nonnull
    @Override
    public ItemStack transferStackInSlot( final EntityPlayer player, final int slotNumber ) {
        // Get the slot that was clicked on
        Slot slot = this.getSlotOrNull( slotNumber );

        // Did player inventory merged with slot inventory?
        boolean merged = false;

        // Did we get a slot, and does it have a valid item?
        if( ( slot != null ) && ( slot.getHasStack() ) ) {
            // Get the slots item stack
            ItemStack slotStack = slot.getStack();

            // Was the slot clicked in the player or hotbar inventory?
            if( this.slotClickedWasInPlayerInventory( slotNumber ) || this.slotClickedWasInHotbarInventory( slotNumber ) ) {
                // Attempt to merge with the upgrade inventory
                merged = this.mergeSlotWithUpgrades( slotStack );

                // Did we merge?
                // Attempt to merge with the network tool
                if( !merged )
                    merged = this.mergeSlotWithNetworkTool(slotStack);

                // Did we merge?
                // Attempt to swap
                if( !merged )
                    merged = this.swapSlotInventoryHotbar(slotNumber, slotStack);
            }
            // Was the slot clicked in the upgrades?
            else if( this.slotClickedWasInUpgrades( slotNumber ) ) {
                // Attempt to merge with the network tool
                merged = this.mergeSlotWithNetworkTool( slotStack );

                // Did we merge with the network tool?
                if( !merged )
                {
                    // Attempt to merge with the player inventory
                    merged = this.mergeSlotWithPlayerInventory( slotStack );
                }
            }
            // Was the slot clicked in the network tool?
            else if( this.hasNetworkTool && this.slotClickedWasInNetworkTool( slotNumber ) ) {
                // Attempt to merge with the upgrade inventory
                merged = this.mergeSlotWithUpgrades( slotStack );

                // Did we merge with the upgrades?
                if( !merged )
                {
                    // Attempt to merge with the player inventory
                    merged = this.mergeSlotWithPlayerInventory( slotStack );
                }
            }

            // Were we able to merge?
            if( !merged ) {
                return new ItemStack(AIR);
            }

            // Did the merger drain the stack?
            if( slotStack.getCount() == 0 ) {
                // Set the slot to have no item
                slot.putStack( new ItemStack(AIR) );
            }

            // Inform the slot its stack changed;
            slot.onSlotChanged();

            // Sync
            this.detectAndSendChanges();
        }

        // Done ( returning null prevents retrySlotClick from being called )
        return new ItemStack(AIR);
    }
}

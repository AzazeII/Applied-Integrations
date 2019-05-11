package AppliedIntegrations.Container;

import AppliedIntegrations.Utils.AIUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
/**
 * @Author Azazell
 */
public abstract class ContainerWithPlayerInventory extends AIContainer {
    // Number of slot rows
    private static int ROWS = 3;

    // Number of slot columns
    private static int COLUMNS = 9;

    // Size(height and width) of each slot
    protected static final int SLOT_SIZE = 18;

    // X offset of inventory
    private static final int INVENTORY_X_OFFSET = 8;

    // Player's hotbar slots
    private final Slot[] hotbarSlots = new Slot[ContainerWithPlayerInventory.COLUMNS];

    // Player's main slots
    private final Slot[] playerSlots = new Slot[ContainerWithPlayerInventory.COLUMNS * ContainerWithPlayerInventory.ROWS];

    public ContainerWithPlayerInventory( final EntityPlayer player ) {
        super( player );
    }

    // Try to merge stack with player slots
    protected final boolean mergeSlotWithHotbarInventory( final ItemStack slotStack ) {
        return this.mergeItemStack( slotStack, this.hotbarSlots[0].slotNumber,
                this.hotbarSlots[ContainerWithPlayerInventory.COLUMNS - 1].slotNumber + 1, false );
    }

    // Try to merge stack with player slots
    protected final boolean mergeSlotWithPlayerInventory( final ItemStack slotStack ) {
        return this.mergeItemStack( slotStack, this.playerSlots[0].slotNumber,
                this.playerSlots[( ContainerWithPlayerInventory.COLUMNS * ContainerWithPlayerInventory.ROWS ) - 1].slotNumber + 1, false );
    }

    // @return True, if slot is from hotbar inv
    protected final boolean slotClickedWasInHotbarInventory( final int slotNumber ) {
        return ( slotNumber >= this.hotbarSlots[0].slotNumber ) &&
                ( slotNumber <= this.hotbarSlots[ContainerWithPlayerInventory.COLUMNS - 1].slotNumber );
    }

    // Check if slot clicked is slot from player's inventory
    protected final boolean slotClickedWasInPlayerInventory( final int slotNumber ) {
        return ( slotNumber >= this.playerSlots[0].slotNumber ) &&
                ( slotNumber <= this.playerSlots[( ContainerWithPlayerInventory.COLUMNS * ContainerWithPlayerInventory.ROWS ) -
                        1].slotNumber );
    }

    // Attempt to move items from hotbar to main inventory. Returns: true, if operation was successful
    protected final boolean swapSlotInventoryHotbar( final int slotNumber, final ItemStack slotStack ) {
        if( this.slotClickedWasInHotbarInventory( slotNumber ) ) {
            return this.mergeSlotWithPlayerInventory( slotStack );
        } else if( this.slotClickedWasInPlayerInventory( slotNumber ) ) {
            return this.mergeSlotWithHotbarInventory( slotStack );
        }

        return false;
    }

    /**
     * Binds the player inventory to this container.
     *
     * @param playerInventory
     * Inventory to bind
     * The Y position offset for the slots
     * @param hotbarPositionY
     * The Y position offset for hotbar slots
     */
    public final void bindPlayerInventory(final IInventory playerInventory, final int inventoryOffsetY, final int hotbarPositionY ) {
        // Hot bar
        // Iterate until column = count
        for( int column = 0; column < ContainerWithPlayerInventory.COLUMNS; column++ ) {
            // Create the slot
            this.hotbarSlots[column] = new Slot( playerInventory, column,
                    ContainerWithPlayerInventory.INVENTORY_X_OFFSET + ( column * ContainerWithPlayerInventory.SLOT_SIZE ), hotbarPositionY );

            // Add the slot
            this.addSlotToContainer( this.hotbarSlots[column] );
        }

        // Main inventory
        // Iterate until row = count
        for( int row = 0; row < ContainerWithPlayerInventory.ROWS; row++ ) {
            // Iterate until column = count
            for( int column = 0; column < ContainerWithPlayerInventory.COLUMNS; column++ ) {
                // Calculate index
                int index = column + ( row * ContainerWithPlayerInventory.COLUMNS );

                // Create the slot
                this.playerSlots[index] = new Slot( playerInventory, ContainerWithPlayerInventory.COLUMNS + index,
                        ContainerWithPlayerInventory.INVENTORY_X_OFFSET + ( column * ContainerWithPlayerInventory.SLOT_SIZE ),
                        ( row * ContainerWithPlayerInventory.SLOT_SIZE ) + inventoryOffsetY );

                // Add the slot
                this.addSlotToContainer( this.playerSlots[index] );
            }
        }
    }
}

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
public abstract class ContainerWithPlayerInventory
        extends AIContainer
{
    /**
     * The number of rows in the player inventory
     */
    private static int ROWS = 3;

    /**
     * The number of columns in the player inventory
     */
    private static int COLUMNS = 9;

    /**
     * The width and height of the slots
     */
    protected static final int SLOT_SIZE = 18;

    /**
     * X position offset for inventory slots
     */
    private static final int INVENTORY_X_OFFSET = 8;

    /**
     * Array of player hotbar slots
     */
    private final Slot[] hotbarSlots = new Slot[ContainerWithPlayerInventory.COLUMNS];

    /**
     * Array of player inventory slots.
     */
    private final Slot[] playerSlots = new Slot[ContainerWithPlayerInventory.COLUMNS * ContainerWithPlayerInventory.ROWS];

    public ContainerWithPlayerInventory( final EntityPlayer player )
    {
        super( player );
    }

    /**
     * Attempt to merge the specified slot stack with the hotbar inventory
     *
     * @param slotStack
     * @return
     */
    protected final boolean mergeSlotWithHotbarInventory( final ItemStack slotStack )
    {
        return this.mergeItemStack( slotStack, this.hotbarSlots[0].slotNumber,
                this.hotbarSlots[ContainerWithPlayerInventory.COLUMNS - 1].slotNumber + 1, false );
    }

    /**
     * Attempt to merge the specified slot stack with the player inventory
     *
     * @param slotStack
     * @return
     */
    protected final boolean mergeSlotWithPlayerInventory( final ItemStack slotStack )
    {
        return this.mergeItemStack( slotStack, this.playerSlots[0].slotNumber,
                this.playerSlots[( ContainerWithPlayerInventory.COLUMNS * ContainerWithPlayerInventory.ROWS ) - 1].slotNumber + 1, false );
    }

    /**
     * Checks if the slot clicked was in the hotbar inventory
     *
     * @param slotNumber
     * @return True if it was in the hotbar inventory, false otherwise.
     */
    protected final boolean slotClickedWasInHotbarInventory( final int slotNumber )
    {
        return ( slotNumber >= this.hotbarSlots[0].slotNumber ) &&
                ( slotNumber <= this.hotbarSlots[ContainerWithPlayerInventory.COLUMNS - 1].slotNumber );
    }

    /**
     * Checks if the slot clicked was in the player inventory
     *
     * @param slotNumber
     * @return True if it was in the player inventory, false otherwise.
     */
    protected final boolean slotClickedWasInPlayerInventory( final int slotNumber )
    {
        return ( slotNumber >= this.playerSlots[0].slotNumber ) &&
                ( slotNumber <= this.playerSlots[( ContainerWithPlayerInventory.COLUMNS * ContainerWithPlayerInventory.ROWS ) -
                        1].slotNumber );
    }

    /**
     * Attempt to move the item from hotbar <-> player inventory
     *
     * @param slotNumber
     * @return
     */
    protected final boolean swapSlotInventoryHotbar( final int slotNumber, final ItemStack slotStack )
    {
        if( this.slotClickedWasInHotbarInventory( slotNumber ) )
        {
            return this.mergeSlotWithPlayerInventory( slotStack );
        }
        else if( this.slotClickedWasInPlayerInventory( slotNumber ) )
        {
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
    public final void bindPlayerInventory(final IInventory playerInventory, final int inventoryOffsetY, final int hotbarPositionY )
    {
        // Hot-bar ID's 0-8
        for( int column = 0; column < ContainerWithPlayerInventory.COLUMNS; column++ )
        {
            // Create the slot
            this.hotbarSlots[column] = new Slot( playerInventory, column,
                    ContainerWithPlayerInventory.INVENTORY_X_OFFSET + ( column * ContainerWithPlayerInventory.SLOT_SIZE ), hotbarPositionY );

            // Add the slot
            this.addSlotToContainer( this.hotbarSlots[column] );
        }

        // Main inventory ID's 9-36
        for( int row = 0; row < ContainerWithPlayerInventory.ROWS; row++ )
        {
            for( int column = 0; column < ContainerWithPlayerInventory.COLUMNS; column++ )
            {
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

package AppliedIntegrations.Container;


import AppliedIntegrations.Parts.AIPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * @Author Azazell
 */
public abstract class AIContainer extends Container
{
    /**
     * Map slotNumber -> Slot
     */
    private final HashMap<Integer, Slot> slotMap = new HashMap<Integer, Slot>();
    /**
     * Set if the player is an MP player.
     */
    private final EntityPlayerMP playerMP;

    /**
     * The player interacting with this container.
     */
    public final EntityPlayer player;

    public AIContainer(final EntityPlayer player )
    {
        // Set the player
        this.player = player;
        if( player instanceof EntityPlayerMP )
        {
            this.playerMP = (EntityPlayerMP)player;
        }
        else
        {
            this.playerMP = null;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
    /**
     * Adds a slot to the container and the slot map.
     */
    @Override
    protected Slot addSlotToContainer( @Nonnull final Slot slot )
    {
        // Call super
        super.addSlotToContainer( slot );

        // Map the slot
        if( this.slotMap.put( slot.slotNumber, slot ) != null )
        {
            //ACHTUNG
        }

        return slot;
    }

    /**
     * Detects server side changes to send to the player.<br/>
     * When modifying slots, return true to set {@code playerMP.isChangingQuantityOnly} to {@code false},
     * or set it directly with the player argument.
     *
     * @param playerMP
     *
     * @return
     */
    protected boolean detectAndSendChangesMP( @Nonnull final EntityPlayerMP playerMP )
    {
        return false;
    }

    /**
     * Use getSlotOrNull.
     */
    @Override
    @Deprecated
    public Slot getSlot( final int slotNumber )
    {
        return super.getSlot( slotNumber );
    }

    /**
     * Returns the slot with the specified slot number or null.
     *
     * @param slotNumber
     * @return
     */
    @Nullable
    public Slot getSlotOrNull( final int slotNumber )
    {
        return this.slotMap.get( slotNumber );
    }

    /**
     * Clears the slot map.
     */
    @Override
    public void onContainerClosed( @Nonnull final EntityPlayer player )
    {
        // Call super
        super.onContainerClosed( player );

        // Clear the map
        this.slotMap.clear();
    }
    public abstract boolean onFilterReceive(AIPart part);
}

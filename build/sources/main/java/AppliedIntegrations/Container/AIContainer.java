package AppliedIntegrations.Container;


import AppliedIntegrations.Parts.AIPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author Azazell
 */
public abstract class AIContainer extends Container
{
    private final List<Slot> slotMap = new ArrayList<>();

    /**
     * The player interacting with this container.
     */
    public final EntityPlayer player;

    public AIContainer(final EntityPlayer player )
    {
        // Set the player
        this.player = player;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    protected Slot addSlotToContainer( @Nonnull final Slot slot ) {
        // Call super
        super.addSlotToContainer(slot);

        // Map the slot
        this.slotMap.add(slot.slotNumber, slot);

        return slot;
    }

    @Nullable
    public Slot getSlotOrNull( final int slotNumber )
    {
        return this.slotMap.get( slotNumber );
    }

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

package AppliedIntegrations.Container;

import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Entities.AITile;

import AppliedIntegrations.Utils.AIUtils;
import appeng.helpers.IPriorityHost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

public class ContainerPriority
        extends Container
{

    /**
     * The host we are setting the priority for
     */
    public final IPriorityHost host;

    /**
     * Player associated with this container
     */
    private final EntityPlayer player;

    public ContainerPriority( final IPriorityHost host, final EntityPlayer player )
    {
        // Set the host
        this.host = host;

        // Set the player
        this.player = player;
    }

    /**
     * Sends the priority to the client
     */
    private void sendPriorityToClient()
    {

    }

    @Override
    public boolean canInteractWith( final EntityPlayer player )
    {
        if( this.host instanceof IInventory)
        {
            return ( (IInventory)this.host ).isUsableByPlayer( player );
        }
        else if( this.host instanceof AIPart)
        {
            return ( (AIPart)this.host ).isPartUseableByPlayer( player );
        }
        else if( this.host instanceof AITile)
        {
            return AIUtils.canPlayerInteractWith( player, (TileEntity)this.host );
        }
        return false;
    }

    public void onClientRequestAdjustPriority( final int adjustment )
    {
        // Adjust
        int newPriority = this.host.getPriority() + adjustment;

        // Set
        this.onClientRequestSetPriority( newPriority );
    }

    /**
     * Called when a client requests the priority of the part.
     */
    public void onClientRequestPriority()
    {
        // Send the priority
        this.sendPriorityToClient();
    }

    /**
     * Called when a client requests to set the priority of the part.
     *
     * @param newPriority
     */
    public void onClientRequestSetPriority( final int newPriority )
    {
        // Set the priority
        this.host.setPriority( newPriority );

        // Send the reply
        this.sendPriorityToClient();
    }

}

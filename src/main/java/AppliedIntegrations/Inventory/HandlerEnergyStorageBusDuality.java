package AppliedIntegrations.Inventory;

import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.tile.TileEnergyInterface;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.storage.data.IItemList;
import cofh.redstoneflux.api.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
/**
 * @Author Azazell
 */

public class HandlerEnergyStorageBusDuality
        extends HandlerEnergyStorageBusBase
{
    /**
     * The handler that is actually used, can be null.
     */
    private HandlerEnergyStorageBusBase internalHandler;

    /**
     * Handler used when facing an Energy container.
     */
    private HandlerEnergyStorageBusContainer containerHandler;

    /**
     * Handler used when facing an interface.
     */
    private HandlerEnergyStorageBusInterface interfaceHandler;

    /**
     * Creates the handler.
     *
     * @param part
     */
    public HandlerEnergyStorageBusDuality( final PartEnergyStorage part )
    {
        super( part );
    }

    /**
     * Updates the internal handler to match the duality handler.
     */
    private void updateInternalHandler()
    {
        // Ensure there is an internal handler?
        if( this.internalHandler != null )
        {
            // Set the filtered Energies
            this.internalHandler.filteredEnergies = this.filteredEnergies;

            // Set inverted
            this.internalHandler.inverted = this.inverted;

            // Set void
            this.internalHandler.setVoidAllowed( this.isVoidAllowed() );
        }
    }

    @Override
    public boolean canAccept( final IAEEnergyStack fluidStack )
    {
        // Ensure we have an internal handler
        if( this.internalHandler != null )
        {
            // Pass to handler
            return this.internalHandler.canAccept( fluidStack );
        }

        // No handler
        return false;
    }

    @Override
    public IAEEnergyStack extractItems( final IAEEnergyStack request, final Actionable mode, final IActionSource source )
    {
        // Ensure we have an internal handler
        if( this.internalHandler != null )
        {
            // Pass to handler
            return this.internalHandler.extractItems( request, mode, source );
        }

        // No handler
        return null;
    }

    @Override
    public IItemList<IAEEnergyStack> getAvailableItems( final IItemList<IAEEnergyStack> out )
    {
        // Ensure we have an internal handler
        if( this.internalHandler != null )
        {
            // Pass to handler
            return this.internalHandler.getAvailableItems( out );
        }

        // No handler
        return out;
    }

    @Override
    public IAEEnergyStack injectItems( final IAEEnergyStack input, final Actionable mode, final IActionSource source )
    {
        // Ensure we have an internal handler
        if( this.internalHandler != null )
        {
            // Pass to handler
            return this.internalHandler.injectItems( input, mode, source );
        }

        // No handler
        return input;
    }

    @Override
    public boolean onNeighborChange()
    {
        boolean doUpdate = false;

        // What is the storage bus facing?
        TileEntity tileEntity = this.getFacingTile();

        HandlerEnergyStorageBusBase newHandler = null;

        if( tileEntity instanceof IPartHost )
        {
            // Get the part
            IPart facingPart = this.getFacingPartFromPartHost( (IPartHost)tileEntity );

            // Is the part a ME interface?
            if( facingPart instanceof PartEnergyInterface )
            {
                // Create the interface handler if needed
                if( this.interfaceHandler == null )
                {
                    // Create the handler
                    this.interfaceHandler = new HandlerEnergyStorageBusInterface( this.partStorageBus );
                }

                // Set the internal handler to the interface handler
                newHandler = this.interfaceHandler;
            }
        }else if( tileEntity instanceof TileEnergyInterface ) {
            // Create the interface handler if needed
            if( this.interfaceHandler == null )
            {
                // Create the handler
                this.interfaceHandler = new HandlerEnergyStorageBusInterface( this.partStorageBus );
            }

            // Set the internal handler to the interface handler
            newHandler = this.interfaceHandler;

        }else if( tileEntity instanceof IEnergyReceiver) {
            // Create the container handler if needed
            if( this.containerHandler == null )
            {
                // Create the handler
                this.containerHandler = new HandlerEnergyStorageBusContainer( this.partStorageBus );
            }

            // Set internal handler to the container handler
            newHandler = this.containerHandler;
        }

        // Has the handler changed?
        if( this.internalHandler != newHandler )
        {
            // Was there a previous handler?
            if( this.internalHandler != null )
            {
                // Let the old handler know the neighbor changed
                this.internalHandler.onNeighborChange();
            }

            // Set the new handler
            this.internalHandler = newHandler;

            // Mark for update
            doUpdate = true;
        }

        // Pass to internal handler
        if( this.internalHandler != null )
        {
            // Update it
            this.updateInternalHandler();

            // Pass to the handler
            doUpdate |= this.internalHandler.onNeighborChange();
        }

        return doUpdate;

    }

    @Override
    public void setInverted( final boolean isInverted )
    {
        this.inverted = isInverted;

        if( this.internalHandler != null )
        {
            this.internalHandler.setInverted( isInverted );
        }
    }

    @Override
    public void setVoidAllowed( final boolean isVoidAllowed )
    {
        // Call super
        super.setVoidAllowed( isVoidAllowed );

        // Call handler
        if( this.internalHandler != null )
        {
            this.internalHandler.setVoidAllowed( isVoidAllowed );
        }
    }

    @Override
    public void tickingRequest( final IGridNode node, final int TicksSinceLastCall )
    {
        // Ensure we have an internal handler
        if( this.internalHandler != null )
        {
            // Pass to handler
            this.internalHandler.tickingRequest( node, TicksSinceLastCall );
        }
    }

    @Override
    public boolean validForPass( final int pass )
    {
        // Ensure we have an internal handler
        if( this.internalHandler != null )
        {
            // Pass to handler
            return this.internalHandler.validForPass( pass );
        }

        // No handler
        return false;
    }
}
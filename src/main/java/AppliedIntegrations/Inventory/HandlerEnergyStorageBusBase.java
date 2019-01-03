package AppliedIntegrations.Inventory;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.AIApi;
import AppliedIntegrations.Parts.EnergyStorageBus.PartEnergyStorage;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
/**
 * @Author Azazell
 */
public abstract class HandlerEnergyStorageBusBase
        implements IMEInventoryHandler<IAEFluidStack>
{
    protected static final AIApi aiApi = AIApi.instance();

    /**
     * Storage bus associated with this handler.
     */
    protected PartEnergyStorage partStorageBus;

    /**
     * Controls what operations this bus is allowed to perform.
     */
    protected AccessRestriction access;

    /**
     * Filtered Energies
     */
    protected List<LiquidAIEnergy> filteredEnergies = new ArrayList<LiquidAIEnergy>();

    /**
     * When true the filter becomes a blacklist.
     */
    protected boolean inverted;

    /**
     * Source of all actions the handler performs.
     */
    protected MachineSource machineSource;

    /**
     * When true excess Energy will be destroyed IF the bus is attached to a
     * void
     * jar.
     */
    private boolean isVoidAllowed = false;

    /**
     * Creates the handler.
     *
     * @param part
     */
    public HandlerEnergyStorageBusBase( final PartEnergyStorage part )
    {
        // Set the part
        this.partStorageBus = part;

        // Set to full read/write access.
        this.access = AccessRestriction.READ_WRITE;

        if( part != null )
        {
            // Set the source
            this.machineSource = new MachineSource( part );
        }
    }

    /**
     * Returns true if there are no filters.
     *
     * @return
     */
    protected boolean allowAny()
    {
        // Are all filters null?
        for( LiquidAIEnergy filteredEnergy : this.filteredEnergies)
        {
            if( filteredEnergy != null )
            {
                // There is a filter
                return false;
            }
        }

        // No filters
        return true;
    }

    protected boolean canTransferEnergy(final LiquidAIEnergy Energy )
    {
        // Can any Energy be transfered?
        if( this.allowAny() )
        {
            // Allow the transfer
            return true;
        }

        /*
         * Validate based on if the Energy is filtered and the storage bus is
         * inverted. See explanation below.
         */
        return( this.filteredEnergies.contains( Energy.getEnergy() ) == !this.inverted );

        /*
         * Truth 'table' ---- Conditions: * isFiltered = true * inverted = false
         * Expected outcome: * valid = true Sequence * valid = ( isFiltered ==
         * !inverted ); * valid = ( true == !false ) * valid = ( true == true )
         * * valid = true
         *
         * ---- Conditions: * isFiltered = false * inverted = false Expected
         * outcome: * valid = false Sequence * valid = ( isFiltered == !inverted
         * ); * valid = ( false == !false ) * valid = ( false == true ) * valid
         * = false
         *
         * ---- Conditions: * isFiltered = true * inverted = true Expected
         * outcome: * valid = false Sequence * valid = ( isFiltered == !inverted
         * ); * valid = ( true == !true ) * valid = ( true == false ) * valid =
         * false ----
         *
         * Conditions: * isFiltered = false * inverted = true Expected outcome:
         * * valid = true Sequence * valid = ( isFiltered == !inverted ); *
         * valid = ( false == !true ) * valid = ( false == false ) * valid =
         * true ----
         */
    }

    /**
     * Gets the tile entity the storage bus is facing.
     *
     * @return
     */
    protected TileEntity getFacingTile()
    {
        // Get the host
        TileEntity hostTile = this.partStorageBus.getHostTile();

        // Is there a host?
        if( hostTile == null )
        {
            // No host.
            return null;
        }

        // Is the host in a loaded world?
        if( hostTile.getWorldObj() == null )
        {
            // No world.
            return null;
        }

        // Get what direction of the storage bus.
        ForgeDirection orientation = this.partStorageBus.getSide();

        // Return the tile entity the storage bus is facing.
        return hostTile.getWorldObj().getTileEntity( hostTile.xCoord + orientation.offsetX, hostTile.yCoord + orientation.offsetY,
                hostTile.zCoord + orientation.offsetZ );
    }

    /**
     * Gets the part that is facing the Energy storage bus.
     *
     * @param partHost
     * @return
     */
    protected IPart getFacingPartFromPartHost( final IPartHost partHost )
    {
        return partHost.getPart( this.partStorageBus.getSide().getOpposite() );
    }

    protected boolean hasSecurityPermission()
    {
        // Is our access restricted to read-only or no access?
        if( ( this.access == AccessRestriction.READ ) || ( this.access == AccessRestriction.NO_ACCESS ) )
        {
            // Not allowed to insert
            return false;
        }
        return true;
    }

    /**
     * Checks if the fluid is an Energy gas.
     *
     * @param fluid
     * @return
     */
    protected boolean isFluidEnergy(final FluidStack fluid )
    {
        // Ensure the request is not null
        if( fluid == null )
        {
            return false;
        }

        // Ensure the fluid an Energy
        return( fluid.getFluid() instanceof LiquidAIEnergy);
    }

    /**
     * Checks if the AE fluidstack is an Energy gas.
     *
     * @param fluidStack
     * @return
     */
    protected boolean isFluidEnergy(final IAEFluidStack fluidStack )
    {
        // Ensure the request is not null
        if( fluidStack == null )
        {
            return false;
        }

        return this.isFluidEnergy( fluidStack.getFluidStack() );
    }

    /**
     * Lets the host grid know that the storage amount has changed.
     *
     * @param change
     */
    protected void postAlterationToHostGrid( final Iterable<IAEFluidStack> change )
    {
        try
        {
            if( this.partStorageBus.getActionableNode().isActive() )
            {
                this.partStorageBus.getGridBlock().getStorageGrid().postAlterationOfStoredItems( StorageChannel.FLUIDS, change, this.machineSource );
            }
        }
        catch( Exception e )
        {

        }
    }

    /**
     * Is the specified fluid allowed to be placed in the container? This does
     * not take into consideration the amount currently in the container.
     */
    @Override
    public abstract boolean canAccept( final IAEFluidStack fluidStack );

    /**
     * Extracts Energy gas from the attached source.
     */
    @Override
    public abstract IAEFluidStack extractItems( final IAEFluidStack request, final Actionable mode, final BaseActionSource source );

    /**
     * Returns the access restrictions, if any, imposed on the storage bus.
     */
    @Override
    public AccessRestriction getAccess()
    {
        return this.access;
    }

    /**
     * Gets the list of 'fluids' that can be extracted from this storage bus.
     */
    @Override
    public abstract IItemList<IAEFluidStack> getAvailableItems( final IItemList<IAEFluidStack> out );

    /**
     * Gets the storage channel for the storage bus.
     */
    @Override
    public StorageChannel getChannel()
    {
        return StorageChannel.FLUIDS;
    }

    /**
     * Gets the priority of the storage bus.
     */
    @Override
    public int getPriority()
    {
        return this.partStorageBus.getPriority();
    }

    /**
     * Dunno
     */
    @Override
    public int getSlot()
    {
        return 0;
    }

    /**
     * Inserts Energy into the source.
     * Returns the number of items not added.
     */
    @Override
    public abstract IAEFluidStack injectItems( final IAEFluidStack input, final Actionable mode, final BaseActionSource source );

    /**
     * Checks if the specified is prioritized.
     */
    @Override
    public final boolean isPrioritized( final IAEFluidStack fluidStack )
    {
        // Ensure the fluid stack is an Energy gas
        if( !this.isFluidEnergy( fluidStack ) )
        {
            // Not an Energy gas.
            return false;
        }

        // Is the Energy prioritized?
        try
        {
            return this.filteredEnergies.contains( ( (LiquidAIEnergy)fluidStack.getFluidStack().getFluid() ).getEnergy() );
        }
        catch( Exception e )
        {
            return false;
        }
    }

    /**
     * Gets if voiding is allowed.
     *
     * @return
     */
    public boolean isVoidAllowed()
    {
        return this.isVoidAllowed;
    }

    /**
     * Called when a neighboring block changes.
     *
     * @return True if a cell update is needed.
     */
    public abstract boolean onNeighborChange();

    /**
     * Sets if the storage bus filter mode is inverted or not.
     *
     * @param isInverted
     * True = Blacklist, False = Whitelist.
     */
    public void setInverted( final boolean isInverted )
    {
        this.inverted = isInverted;
    }

    /**
     * Set's the list of filtered Energies.
     *
     * @param EnergyList
     */
    public void setPrioritizedEnergies( final List<LiquidAIEnergy> EnergyList )
    {
        this.filteredEnergies = EnergyList;
    }


    /**
     * Sets if the handler can void excess Energy if connected to void jar.
     *
     * @param
     */
    public void setVoidAllowed( final boolean isVoidAllowed )
    {
        this.isVoidAllowed = isVoidAllowed;
    }

    /**
     * Called periodically by the Energy storage bus.
     */
    public abstract void tickingRequest( final IGridNode node, final int TicksSinceLastCall );

    /**
     * Is the handler valid for this pass?
     */
    @Override
    public abstract boolean validForPass( final int pass );

}
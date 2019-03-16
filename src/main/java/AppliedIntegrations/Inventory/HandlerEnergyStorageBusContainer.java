package AppliedIntegrations.Inventory;

import AppliedIntegrations.API.*;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import appeng.api.config.Actionable;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;

import ic2.api.energy.tile.IEnergySink;
import ic2.core.block.wiring.TileEntityElectricBlock;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import static AppliedIntegrations.API.LiquidAIEnergy.EU;
import static AppliedIntegrations.API.LiquidAIEnergy.J;
import static AppliedIntegrations.API.LiquidAIEnergy.RF;

/**
 * @Author Azazell
 */
class HandlerEnergyStorageBusContainer
        extends HandlerEnergyStorageBusBase
{
    private TileEntity EnergyContainer;

    Hashtable<LiquidAIEnergy, Long> cachedContainerEnergies = new Hashtable<LiquidAIEnergy, Long>();

    public HandlerEnergyStorageBusContainer( final PartEnergyStorage part )
    {
        super( part );
        this.EnergyContainer = part.getFacingContainer();
    }


    private void addListToDictionary( final List<IEnergyStack> EnergyList, final Hashtable<LiquidAIEnergy, Long> dictionary )
    {
        // Add each Energy
        if( EnergyList != null )
        {
            for( IEnergyStack stack : EnergyList )
            {
                dictionary.put( stack.getEnergy(), stack.getStackSize() );
            }
        }
    }

    private void adjustCache(final LiquidAIEnergy Energy, final long diff )
    {
        long cachedAmount = 0;

        // Does the cache have this Energy?
        if( this.cachedContainerEnergies.containsKey( Energy ) )
        {
            cachedAmount = this.cachedContainerEnergies.get( Energy );
        }

        // Change the amount
        long newAmount = cachedAmount + diff;

        // Is there any amount left?
        if( newAmount > 0 )
        {
            // Update the cache
            this.cachedContainerEnergies.put( Energy, newAmount );
        }
        // None left, was there some?
        else if( cachedAmount > 0 )
        {
            // Remove from the cache
            this.cachedContainerEnergies.remove( Energy );
        }
    }

    /**
     * Gets Energy stored in the container.
     */
    private List<IEnergyStack> getContainerEnergy()
    {
        // Ensure there is a container
        if( this.EnergyContainer == null )
        {
            // No container
            return null;
        }

        // Get the Energy and amounts in the container
        List<IEnergyStack> containerStacks = new ArrayList<IEnergyStack>();
        // Add energy to stack
        if(Utils.getEnergyFromContainer(EnergyContainer) instanceof IEnergyReceiver)
        containerStacks.add(new EnergyStack(RF,((IEnergyReceiver)EnergyContainer).getEnergyStored(this.partStorageBus.getSide())));
        if(Utils.getEnergyFromContainer(EnergyContainer) instanceof IEnergySink)
            containerStacks.add(new EnergyStack(EU,(int)((IEnergySink)EnergyContainer).getDemandedEnergy()));
        if(Utils.getEnergyFromContainer(EnergyContainer) instanceof IStrictEnergyAcceptor)
            containerStacks.add(new EnergyStack(J,(int)((IStrictEnergyAcceptor)EnergyContainer).getEnergy()));


        // Ensure there is Energy in the container
        if( ( containerStacks == null ) || containerStacks.isEmpty() )
        {
            // Empty
            return null;
        }

        List<IEnergyStack> EnergyList = new ArrayList<IEnergyStack>();

        // Skipping the filter check?
        boolean skipFilterCheck = this.allowAny();

        // Add the Energy
        for( IEnergyStack EnergyStack : containerStacks )
        {
            // Is the Energy in the filter?
            if( skipFilterCheck || ( this.filteredEnergies.contains( EnergyStack.getEnergy() ) ) )
            {
                // Convert to fluid
                LiquidAIEnergy energy = EnergyStack.getEnergy();

                // Is there a fluid form of the Energy?
                if( energy != null )
                {
                    // Add to the list
                    EnergyList.add( EnergyStack );
                }
            }
        }

        return EnergyList;
    }

    @Override
    public boolean canAccept( final IAEFluidStack fluidStack )
    {
        // Ensure we have an Energy container
        if( this.EnergyContainer == null )
        {
            // No container
            return false;
        }

        // Ensure the bus has security access
        if( !this.hasSecurityPermission() )
        {
            // The bus does not have security access.
            return false;
        }

        // Ensure the fluid is an Energy gas
        if( !this.isFluidEnergy( fluidStack ) )
        {
            // Not Energy gas.
            return false;
        }

        // Ensure we are allowed to transfer this fluid
        if( !this.canTransferEnergy( (LiquidAIEnergy)fluidStack.getFluid() ) )
        {
            /*
             * Either: Not on whitelist or is on blacklist
             */
            return false;
        }

        // Get the Energy, if any, in the container
        IEnergyStack containerStack = new EnergyStack(Utils.getEnergyFromContainer(getFacingTile()),1);

        // Is the container empty?
        if( containerStack == null )
        {
            // Container is empty, can accept any Energy.
            return true;
        }

        // Get the gas Energy
        LiquidAIEnergy energy = ( (LiquidAIEnergy)fluidStack.getFluid() ).getEnergy();

        // Does the Energy in the container match the gas Energy?
        return energy == containerStack.getEnergy();
    }

    /**
     * Extracts Energy from the container.
     * returns the number of items extracted, null
     */
    @Override
    public IAEFluidStack extractItems( final IAEFluidStack request, final Actionable mode, final BaseActionSource source )
    {
        if(EnergyContainer instanceof IEnergyHandler) {
            IEnergyHandler handler = (IEnergyHandler)EnergyContainer;
            if ((this.EnergyContainer == null) || (request == null)) {
                // Nothing to drain from, or empty request
                return null;
            }
            // Ensure the fluid is an Energy
            if (!this.isFluidEnergy(request)) {
                // Not Energy gas fluid.
                return null;
            }

            // Get the fluid stack from the request
            FluidStack toDrain = request.getFluidStack();


            // Simulate draining the container
            int drained = handler.extractEnergy(this.partStorageBus.getSide(),toDrain.amount,true);

            // Do we have the power to drain this?
            if (!this.partStorageBus.extractPowerForEnergyTransfer(drained, Actionable.SIMULATE)) {
                // Not enough power
                return null;
            }

            // Are we modulating?
            if (mode == Actionable.MODULATE) {

                // Extract
                handler.extractEnergy(this.partStorageBus.getSide(),toDrain.amount,false);

                // Take power
                this.partStorageBus.extractPowerForEnergyTransfer(toDrain.amount, Actionable.MODULATE);

                // Update cache
                this.adjustCache(((LiquidAIEnergy) toDrain.getFluid()).getEnergy(), -toDrain.amount);
            }

            // Copy the request
            IAEFluidStack extractedFluid = request.copy();

            // probe
            extractedFluid.setStackSize(1-handler.extractEnergy(this.partStorageBus.getSide(),1,true));

            return extractedFluid;
        }
        return null;
    }

    /**
     * Gets the list of fluids from the container.
     */
    @Override
    public IItemList<IAEFluidStack> getAvailableItems( final IItemList<IAEFluidStack> out )
    {
        if( this.EnergyContainer != null )
        {
            // Get the contents of the container
            List<IEnergyStack> EnergyList = this.getContainerEnergy();

            // Update the cache
            this.cachedContainerEnergies.clear();
            this.addListToDictionary( EnergyList, this.cachedContainerEnergies);

            if( EnergyList != null )
            {
                for( IEnergyStack Energy : EnergyList )
                {
                    // Convert to fluid
                    LiquidAIEnergy energy = Energy.getEnergy();

                    // Add to the item list
                    out.add( Utils.ConvertToAEFluidStack(energy,Energy.getStackSize()));
                }
            }
        }

        return out;
    }

    /**
     * Inserts Energy into the container.
     * returns the number of energy not added.
     */
    @Override
    public IAEFluidStack injectItems( final IAEFluidStack input, final Actionable mode, final BaseActionSource source )
    {
        if(this.EnergyContainer instanceof IStrictEnergyAcceptor) {
            IStrictEnergyAcceptor JoulesEnergyStorage = (IStrictEnergyAcceptor)this.EnergyContainer;
            if ((this.EnergyContainer == null) || (input == null) || (!this.canAccept(input))) {
                return input;
            }
            // Get the fluid stack from the input
            FluidStack toFill = input.getFluidStack();
            int AmountToFill = toFill.amount;
            // Do we have the power to complete this operation?
            if (!this.partStorageBus.extractPowerForEnergyTransfer(AmountToFill, Actionable.SIMULATE)) {
                // Not enough power
                return input;
            }
            int EnergyLeft = (int)(AmountToFill-Math.min(JoulesEnergyStorage.getEnergy(),AmountToFill));
            // Are we modulating?
            if (mode == Actionable.MODULATE) {
                // Modulate filling // Amount of energy that was (or would have been, if simulated) received.
                int Return = (int)JoulesEnergyStorage.transferEnergyToAcceptor(this.partStorageBus.getSide(),AmountToFill);
                EnergyLeft = AmountToFill-Return;

                // Take power for as much as we claimed we could take.
                this.partStorageBus.extractPowerForEnergyTransfer(AmountToFill, Actionable.MODULATE);

                // Update the cache
                this.adjustCache(((LiquidAIEnergy) toFill.getFluid()).getEnergy(), AmountToFill);
            }
            // Did we completely drain the input stack?
            if (EnergyLeft == 0) {
                // Nothing left over
                return null;
            }
            // Return what was left over
            IAEFluidStack remainingFluid = input.copy();
            remainingFluid.setStackSize(EnergyLeft);

            return remainingFluid;
        }else if (this.EnergyContainer instanceof IEnergyReceiver) {
            IEnergyReceiver RFEnergyContainer = (IEnergyReceiver)this.EnergyContainer;
            if ((this.EnergyContainer == null) || (input == null) || (!this.canAccept(input))) {
                return input;
            }
            // Get the fluid stack from the input
            FluidStack toFill = input.getFluidStack();
            int AmountToFill = toFill.amount;
            // Do we have the power to complete this operation?
            if (!this.partStorageBus.extractPowerForEnergyTransfer(AmountToFill, Actionable.SIMULATE)) {
                // Not enough power
                return input;
            }
            int EnergyLeft = 1 - RFEnergyContainer.receiveEnergy(this.partStorageBus.getSide(),1,true);
            // Are we modulating?
            if (mode == Actionable.MODULATE) {
                // Modulate filling // Amount of energy that was (or would have been, if simulated) received.
                int Return = RFEnergyContainer.receiveEnergy(this.partStorageBus.getSide(),AmountToFill,false);
                EnergyLeft = AmountToFill-Return;

                // Take power for as much as we claimed we could take.
                this.partStorageBus.extractPowerForEnergyTransfer(AmountToFill, Actionable.MODULATE);

                // Update the cache
                this.adjustCache(((LiquidAIEnergy) toFill.getFluid()).getEnergy(), AmountToFill);
            }
            // Did we completely drain the input stack?
            if (EnergyLeft == 0) {
                // Nothing left over
                return null;
            }
            // Return what was left over
            IAEFluidStack remainingFluid = input.copy();
            remainingFluid.setStackSize(EnergyLeft);

            return remainingFluid;
        }else if(this.EnergyContainer instanceof TileEntityElectricBlock) {
            TileEntityElectricBlock EUEnergyContainer = (TileEntityElectricBlock)this.EnergyContainer;

            if ((this.EnergyContainer == null) || (input == null) || (!this.canAccept(input))) {
                return input;
            }
            // Get the fluid stack from the input
            FluidStack toFill = input.getFluidStack();
            int AmountToFill = toFill.amount;
            // Do we have the power to complete this operation?
            if (!this.partStorageBus.extractPowerForEnergyTransfer(AmountToFill, Actionable.SIMULATE)) {
                // Not enough power
                return input;
            }
            int EnergyLeft = AmountToFill - Math.min((int)EUEnergyContainer.getCapacity(),AmountToFill);
            // Are we modulating?
            if (mode == Actionable.MODULATE) {
                // Modulate filling // Amount of energy that was (or would have been, if simulated) received.
                int Return = (int)EUEnergyContainer.injectEnergy(this.partStorageBus.getSide(),AmountToFill,4.0D);
                EnergyLeft = Return;

                // Take power for as much as we claimed we could take.
                this.partStorageBus.extractPowerForEnergyTransfer(AmountToFill, Actionable.MODULATE);

                // Update the cache
                this.adjustCache(((LiquidAIEnergy) toFill.getFluid()).getEnergy(), AmountToFill);
            }
            // Did we completely drain the input stack?
            if (EnergyLeft == 0) {
                // Nothing left over
                return null;
            }
            // Return what was left over
            IAEFluidStack remainingFluid = input.copy();
            remainingFluid.setStackSize(EnergyLeft);

            return remainingFluid;
        }
        return null;
    }

    /**
     * Checks if we are still facing a valid container.
     *
     * @return
     */
    @Override
    public boolean onNeighborChange()
    {
        // Get the tile we are facing
        TileEntity tileEntity = this.getFacingTile();

        // Are we facing an Energy container?
        if( tileEntity instanceof IEnergyReceiver || tileEntity instanceof IEnergySink || tileEntity instanceof IStrictEnergyAcceptor)
        {
            // Has the container changed?
            if( this.EnergyContainer != tileEntity )
            {
                // Set the container
                this.EnergyContainer = tileEntity;
                this.partStorageBus.saveContainer(tileEntity);
                // Clear the cache
                this.cachedContainerEnergies.clear();

                // Container changed
                return true;
            }

            return false;
        }

        // Was the bus facing a container?
        if( this.EnergyContainer != null )
        {
            // Clear the reference
            this.EnergyContainer = null;

            // Send one last tick
            this.tickingRequest( null, 0 );

            return true;
        }

        return false;
    }

    /**
     * Checks the Energy container.
     */
    @Override
    public void tickingRequest( final IGridNode node, final int TicksSinceLastCall )
    {
        // Create the checklist
        HashSet<LiquidAIEnergy> Energies = new HashSet<LiquidAIEnergy>();

        // Get the current contents of the container
        List<IEnergyStack> currentContainerContents = this.getContainerEnergy();

        // Convert to dictionary
        Hashtable<LiquidAIEnergy, Long> currentContainerEnergies = new Hashtable<LiquidAIEnergy, Long>();
        if( currentContainerContents != null )
        {
            this.addListToDictionary( currentContainerContents, currentContainerEnergies );

            // Add the current Energies to check list
            Energies.addAll( currentContainerEnergies.keySet() );
        }

        // Add the cached Energies to check list
        Energies.addAll( this.cachedContainerEnergies.keySet() );

        // Is there anything to check?
        if( Energies.size() == 0 )
        {
            // Nothing to check.
            return;
        }

        // Alteration list
        List<IAEFluidStack> alterations = null;

        // Compare all amounts
        for( LiquidAIEnergy Energy : Energies )
        {
            // Value cached
            long cachedAmount = 0;
            if( this.cachedContainerEnergies.containsKey( Energy ) )
            {
                cachedAmount = this.cachedContainerEnergies.get( Energy );
            }

            // Current value
            long currentAmount = 0;
            if( currentContainerEnergies.containsKey( Energy ) )
            {
                currentAmount = currentContainerEnergies.get( Energy );
            }

            // Calculate the difference
            long diff = currentAmount - cachedAmount;

            // Do they differ?
            if( diff != 0 )
            {
                // First alteration?
                if( alterations == null )
                {
                    // Create the list
                    alterations = new ArrayList<IAEFluidStack>();
                }

                // Create the alteration
                alterations.add( Utils.ConvertToAEFluidStack(Energy,diff) );

                // Update the cache
                this.adjustCache( Energy, diff );
            }
        }

        // Any alterations?
        if( alterations != null )
        {
            // Post the changes
            this.postAlterationToHostGrid( alterations );
        }

    }

    /**
     * Valid for pass 1 if there are filters or the container has stored
     * Energy.
     * Valid for pass 2 if no filters or stored Energy.
     *
     * @return
     */
    @Override
    public boolean validForPass( final int pass )
    {
        if( this.EnergyContainer != null && EnergyContainer instanceof IStrictEnergyAcceptor)
        {
            IStrictEnergyAcceptor receiver =(IStrictEnergyAcceptor) this.EnergyContainer;
            boolean hasFilters = !this.allowAny();
            boolean hasStored = !new EnergyStack(J,(int)receiver.getEnergy()).isEmpty();

            // Is this the priority pass?
            if( pass == 1 )
            {
                // Valid if has filters or container has something in it
                return( hasFilters || hasStored );
            }

            // Valid if has no filters.
            return( !hasFilters );

        }
        if( this.EnergyContainer != null && EnergyContainer instanceof IEnergyReceiver) {
        IEnergyReceiver receiver = (IEnergyReceiver) this.EnergyContainer;
        boolean hasFilters = !this.allowAny();
        boolean hasStored = !new EnergyStack(RF, receiver.getEnergyStored(this.partStorageBus.getSide())).isEmpty();

        // Is this the priority pass?
        if (pass == 1) {
            // Valid if has filters or container has something in it
            return (hasFilters || hasStored);
        }

        // Valid if has no filters.
        return (!hasFilters);
        }
        if( this.EnergyContainer != null && EnergyContainer instanceof IEnergySink)
        {
            IEnergySink receiver =(IEnergySink) this.EnergyContainer;
            boolean hasFilters = !this.allowAny();
            boolean hasStored = !new EnergyStack(EU,(int)receiver.getDemandedEnergy()).isEmpty();

            // Is this the priority pass?
            if( pass == 1 )
            {
                // Valid if has filters or container has something in it
                return( hasFilters || hasStored );
            }

            // Valid if has no filters.
            return( !hasFilters );

        }

        return false;
    }
}

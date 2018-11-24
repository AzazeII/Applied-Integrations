package AppliedIntegrations.Inventory;

import AppliedIntegrations.API.EnergyStack;
import AppliedIntegrations.API.IEnergyStack;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HandlerItemEnergyCellCreative
        extends HandlerItemEnergyCell
{

    public HandlerItemEnergyCellCreative(final ItemStack storageStack, final ISaveProvider saveProvider )
    {
        super( storageStack, saveProvider );

        this.totalBytes = 0;
        this.totalEnergyStorage = 0;
    }

    /**
     * The creative cell can not store new Energy.
     */
    @Override
    public boolean canAccept( final IAEFluidStack input )
    {
        return false;
    }

    /**
     * The creative cell can only provide Energy based on its parition table.
     */
    @Override
    public IAEFluidStack extractItems(final IAEFluidStack request, final Actionable mode, final BaseActionSource src )
    {
        // Ensure there is a request, and that it is an Energy gas
        if( ( request != null ) && ( request.getFluid() != null ) && ( request.getFluid() instanceof LiquidAIEnergy) )
        {
            // Get the Energy of the Energy
            LiquidAIEnergy requestEnergy = ( (LiquidAIEnergy)request.getFluid() ).getEnergy();

            // Is the cell partitioned for this Energy?
            if( ( requestEnergy != null ) && ( this.partitionEnergies.contains( requestEnergy ) ) )
            {
                return request.copy();
            }
        }

        return null;
    }

    /**
     * Available items based on partition table.
     */
    @Override
    public IItemList<IAEFluidStack> getAvailableItems(final IItemList<IAEFluidStack> availableList )
    {
        for( LiquidAIEnergy Energy : this.partitionEnergies )
        {
            // Create the AE fluid stack
            availableList.add(Utils.ConvertToAEFluidStack(Energy,2000000000));

        }

        return availableList;
    }

    /**
     * No storage
     */
    @Override
    public long getFreeBytes()
    {
        return 0;
    }

    /**
     * 'Stored' Energy based on partition table.
     */
    @Override
    public List<IEnergyStack> getStoredEnergy()
    {
        // Make the list
        List<IEnergyStack> storedList = new ArrayList<IEnergyStack>( this.partitionEnergies.size() );

        for( LiquidAIEnergy Energy : this.partitionEnergies )
        {
            storedList.add(new EnergyStack(Energy,1));
        }

        return storedList;
    }

    /**
     * No storage
     */
    @Override
    public long getUsedBytes()
    {
        return 0;
    }

    /**
     * Used types based on partition table.
     */
    @Override
    public int getUsedTypes()
    {
        return this.partitionEnergies.size();
    }

    /**
     * Creative cell can not inject.
     */
    @Override
    public IAEFluidStack injectItems( final IAEFluidStack input, final Actionable mode, final BaseActionSource src )
    {
        // Ensure there is an input.
        if( ( input == null ) )
        {
            // No input
            return null;
        }

        // Can not inject items.
        return input.copy();
    }

    /**
     * This is a creative cell.
     */
    @Override
    public boolean isCreative()
    {
        return true;
    }

    /**
     * Creative cell is always partitioned.
     */
    @Override
    public boolean isPartitioned()
    {
        return true;
    }

    /**
     * Creative cell can not inject.
     */
    @Override
    public boolean isPrioritized( final IAEFluidStack input )
    {
        return false;
    }

    /**
     * Meaniningless on creative cell.
     */
    @Override
    public void partitionToCellContents()
    {
        // Ignored
    }

    /**
     * Creative cell can not inject.
     */
    @Override
    public boolean validForPass( final int pass )
    {
        return false;
    }

}

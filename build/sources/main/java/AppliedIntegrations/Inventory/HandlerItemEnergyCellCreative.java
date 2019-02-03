package AppliedIntegrations.Inventory;

import AppliedIntegrations.API.EnergyStack;
import AppliedIntegrations.API.IEnergyStack;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Utils;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.ISaveProvider;
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
    public boolean canAccept( final IAEEnergyStack input )
    {
        return false;
    }

    /**
     * The creative cell can only provide Energy based on its parition table.
     */
    @Override
    public IAEEnergyStack extractItems(final IAEEnergyStack request, final Actionable mode, final IActionSource src )
    {
        // Ensure there is a request, and that it is an Energy gas
        if( ( request != null ) && ( request.getEnergy() != null ) && ( request.getEnergy() instanceof LiquidAIEnergy) )
        {
            // Get the Energy of the Energy
            LiquidAIEnergy requestEnergy = ( (LiquidAIEnergy)request.getEnergy() ).getEnergy();

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
    public IItemList<IAEEnergyStack> getAvailableItems(final IItemList<IAEEnergyStack> availableList )
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
    public IAEEnergyStack injectItems( final IAEEnergyStack input, final Actionable mode, final IActionSource src )
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
    public boolean isPrioritized( final IAEEnergyStack input )
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

package AppliedIntegrations.Inventory;

import AppliedIntegrations.API.EnergyStack;
import AppliedIntegrations.API.IEnergyStack;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Gui.SortMode;
import AppliedIntegrations.Items.ItemEnum;
import AppliedIntegrations.Items.StorageCells.EnergyStorageCell;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.ViewItems;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.ISaveProvider;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IItemList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Azazell
 */
public class HandlerItemEnergyCell
        implements IMEInventoryHandler<IAEEnergyStack>
{

    /**
     * NBT Keys
     */
    private static final String NBT_ENERGY_NUMBER_KEY = "Energy#",
            NBT_SORT_KEY = "SortMode",
            NBT_PARTITION_COUNT_KEY = "PartitionCount",
            NBT_PARTITION_NUMBER_KEY = "Partition#";

    /**
     * Controls how many Energy can fit in a single byte.
     */
    private static final long ENERGY_PER_BYTE = 1000;
    private static String NBT_KEY_VIEW_MODE = "ViewMode";
    /**
     * Default sort mode.
     */
    private static SortMode mode = SortMode.NUMERIC;

    /**
     * Default view mode.
     */
    private static final ViewItems DEFAULT_VIEW_MODE = ViewItems.ALL;
    private static String NBT_PARTITION_KEY = "KEYPARTITION#";

    /**
     * Stores cell data
     */
    private NBTTagCompound cellData;

    /**
     * Energy stored on the cell.
     */
    private IEnergyStack[] storedEnergy;

    /**
     * Total number of Energy types the cell can store.
     */
    private int totalTypes;

    /**
     * Total number of bytes the cell can store.
     */
    protected long totalBytes;

    /**
     * Total number of Energy the cell can hold.
     */
    protected long totalEnergyStorage;

    /**
     * Number of Energy stored on the cell.
     */
    private long usedEnergyStorage = 0;

    /**
     * View mode.
     */
    private ViewItems viewMode = DEFAULT_VIEW_MODE;

    /**
     * Who do we tell we have changed?
     * Can be null.
     */
    private final ISaveProvider saveProvider;

    /**
     * List of Energies this cell can only accept.
     */
    protected final ArrayList<LiquidAIEnergy> partitionEnergies = new ArrayList<LiquidAIEnergy>();

    public HandlerItemEnergyCell(final ItemStack storageStack, final ISaveProvider saveProvider, ItemEnum iNum )
    {
        // Ensure we have a NBT tag
        if( !storageStack.hasTagCompound() )
        {
            storageStack.setTagCompound( new NBTTagCompound() );
        }

        // Get the NBT tag
        this.cellData = storageStack.getTagCompound();

        // Get the total types we can store
        this.totalTypes = ( (EnergyStorageCell)iNum.getItem() ).getTotalTypes( storageStack );

        // Get the total bytes we can hold
        this.totalBytes = ( (EnergyStorageCell)iNum.getItem() ).getBytes( storageStack );

        // Calculate how many Energy we can hold
        this.totalEnergyStorage = this.totalBytes * HandlerItemEnergyCell.ENERGY_PER_BYTE;

        // Setup the storage
        this.storedEnergy = new IEnergyStack[this.totalTypes];

        // Set the save provider
        this.saveProvider = saveProvider;

        // Read the cell data
        this.readCellData();
    }

    /**
     * Attempts to add the Energy to the cell
     *
     * @param Energy
     * @return Amount not stored
     */
    private long addEnergyToCell(final LiquidAIEnergy Energy, final long amount, final Actionable mode )
    {
        // Calculate amount to store
        long amountToStore = Math.min( amount, this.totalEnergyStorage - this.usedEnergyStorage );

        // Ensure we can store any
        if( amountToStore == 0 )
        {
            // Cell is full
            return amount;
        }

        // Get the slot for this Energy
        int slotIndex = this.getSlotFor( Energy );

        // Ensure there is somewhere to put the Energy
        if( slotIndex == -1 )
        {
            return amount;
        }

        // Are we modulating?
        if( mode == Actionable.MODULATE )
        {
            // Get the slot
            IEnergyStack stackToAddTo = this.storedEnergy[slotIndex];

            // Is the slot null?
            if( stackToAddTo == null )
            {
                // Create the stack
                stackToAddTo = new EnergyStack( Energy, 0 );

                // Store it
                this.storedEnergy[slotIndex] = stackToAddTo;
            }

            // Add to the stack
            stackToAddTo.adjustStackSize( amountToStore );

            // Adjust the used amount
            this.usedEnergyStorage += amountToStore;

            // Write the changes to the data tag
            this.writeStorageChanges( slotIndex, stackToAddTo );
        }

        // Return the amount we could not store
        return( amount - amountToStore );

    }

    /**
     * Attempts to extract Energy from the cell.
     *
     * @param Energy
     * @param amount
     * @param mode
     * @return Amount extracted.
     */
    private long extractEnergyFromCell(final LiquidAIEnergy Energy, final long amount, final Actionable mode )
    {
        // Do we have this Energy stored?
        int slotIndex = this.getSlotFor( Energy );
        if( ( slotIndex == -1 ) || ( this.storedEnergy[slotIndex] == null ) )
        {
            // Not stored.
            return 0;
        }

        // Get the slot
        IEnergyStack slotToExtractFrom = this.storedEnergy[slotIndex];

        // Calculate the amount to extract
        long amountToExtract = Math.min( slotToExtractFrom.getStackSize(), amount );

        // Are we modulating?
        if( mode == Actionable.MODULATE )
        {
            // Extract from the slot
            slotToExtractFrom.adjustStackSize( -amountToExtract );

            // Is the slot now empty?
            if( slotToExtractFrom.isEmpty() )
            {
                // Null it
                slotToExtractFrom = null;

                // Update the storage
                this.storedEnergy[slotIndex] = null;
            }

            // Adjust the used amount
            this.usedEnergyStorage -= amountToExtract;

            // Sync the data tag
            this.writeStorageChanges( slotIndex, slotToExtractFrom );

        }

        return amountToExtract;
    }

    /**
     * Finds the first matching, or empty slot and return its index.
     *
     * @param Energy
     * @return -1 if no match or empty slot found
     */
    private int getSlotFor( final LiquidAIEnergy Energy )
    {
        int slot = -1;

        // Look for a match
        for( int index = 0; index < this.totalTypes; index++ )
        {
            // Get the stack
            IEnergyStack internalStack = this.storedEnergy[index];

            // Is the slot empty?
            if( internalStack == null )
            {
                // Is this the first empty slot we have encountered?
                if( slot == -1 )
                {
                    // Set this as the empty slot to add to if no match is found.
                    slot = index;
                }
                continue;
            }

            // Do the Energies match?
            if( internalStack.getEnergy() == Energy )
            {
                // Found a match
                slot = index;

                // Stop searching
                break;
            }
        }

        return slot;
    }

    /**
     * Reads the data from the cell item.
     */
    private void readCellData()
    {
        // Load stored Energy from data
        for( int index = 0; index < this.totalTypes; index++ )
        {
            // Is there a Energy tag?
            if( this.cellData.hasKey( HandlerItemEnergyCell.NBT_ENERGY_NUMBER_KEY + index ) )
            {
                // Set the storage
                this.storedEnergy[index] = EnergyStack.loadEnergyStackFromNBT( this.cellData
                        .getCompoundTag( HandlerItemEnergyCell.NBT_ENERGY_NUMBER_KEY + index ) );

                if( this.storedEnergy[index] != null )
                {
                    // Update the stored amount
                    this.usedEnergyStorage += this.storedEnergy[index].getStackSize();
                }
            }
        }

        // Load the sort mode
        if( this.cellData.hasKey( HandlerItemEnergyCell.NBT_SORT_KEY ) )
        {
            this.mode = SortMode.getMode(this.cellData.getInteger("mode"));
        }

        // Load view mode
        if( this.cellData.hasKey( NBT_KEY_VIEW_MODE ) )
        {

        }

        // Load partition list
        if( this.cellData.hasKey( HandlerItemEnergyCell.NBT_PARTITION_KEY ) )
        {
            // Get the partition tag
            NBTTagCompound partitionData = this.cellData.getCompoundTag( HandlerItemEnergyCell.NBT_PARTITION_KEY );

            // Get the partition count
            int partitionCount = partitionData.getInteger( HandlerItemEnergyCell.NBT_PARTITION_NUMBER_KEY );

            // Read the partition list
            String tag;
            LiquidAIEnergy partitionEnergy;
            for( int i = 0; i < partitionCount; i++ )
            {
                // Read the Energy tag
                tag = partitionData.getString( HandlerItemEnergyCell.NBT_PARTITION_NUMBER_KEY + i );

                // Skip if empty tag
                if( tag.equals( "" ) )
                {
                    continue;
                }

                // Get the Energy
                partitionEnergy = LiquidAIEnergy.getEnergy(tag);

                if( partitionEnergy != null )
                {
                    // Add the Energy
                    this.partitionEnergies.add( partitionEnergy );
                }
            }
        }
    }

    /**
     * Synchronizes the data tag to the partition list.
     */
    private void writePartitionList()
    {
        // Is the cell partitioned?
        if( !this.isPartitioned() )
        {
            // Remove the partition tag
            this.cellData.removeTag( HandlerItemEnergyCell.NBT_PARTITION_KEY );
        }
        else
        {
            // Create the partition data
            NBTTagCompound partitionData = new NBTTagCompound();

            // Write the partition list
            int count = 0;
            for( LiquidAIEnergy pEnergy : this.partitionEnergies )
            {
                // Write the Energy tag
                partitionData.setString( HandlerItemEnergyCell.NBT_PARTITION_NUMBER_KEY + count, pEnergy.getTag() );

                // Increment the count
                count++ ;
            }

            // Write the count
            partitionData.setInteger( HandlerItemEnergyCell.NBT_PARTITION_COUNT_KEY, count );

            // Write the partition data
            this.cellData.setTag( HandlerItemEnergyCell.NBT_PARTITION_KEY, partitionData );
        }

        // Inform the save provider
        if( this.saveProvider != null )
        {
            this.saveProvider.saveChanges( null );
        }
    }

    /**
     * Synchronizes the data tag to the changed slot.
     *
     * @param slotIndex

     */
    private void writeStorageChanges( final int slotIndex, final IEnergyStack EnergyStack )
    {
        // Create a new NBT
        NBTTagCompound EnergyTag = new NBTTagCompound();

        // Is there data to write?
        if( ( EnergyStack != null ) && EnergyStack.hasEnergy() && !EnergyStack.isEmpty() )
        {
            // Write the Energy to the tag
            EnergyStack.writeToNBT( EnergyTag );

            // Update the data tag
            this.cellData.setTag( HandlerItemEnergyCell.NBT_ENERGY_NUMBER_KEY + slotIndex, EnergyTag );
        }
        else
        {
            // Remove the tag, as it is now empty
            this.cellData.removeTag( HandlerItemEnergyCell.NBT_ENERGY_NUMBER_KEY + slotIndex );
        }

        // Inform the save provider
        if( this.saveProvider != null )
        {
            this.saveProvider.saveChanges( null );
        }
    }

    /**
     * Adds an Energy to the cells partitioning.
     *
     * @param Energy
     */
    public boolean addEnergyToPartitionList( final LiquidAIEnergy Energy )
    {
        // Ensure the list does not already contain the Energy
        if( !this.partitionEnergies.contains( Energy ) )
        {
            // Add to the list
            this.partitionEnergies.add( Energy );

            // Update the cell data
            this.writePartitionList();

            return true;
        }

        return false;
    }

    /**
     * Checks if the cell can accept/store the fluid.
     */
    @Override
    public boolean canAccept( final IAEEnergyStack input )
    {

        // Ensure there is an input
        if( input == null)
        {
            // Null input
            return false;
        }
        if (!(input.getEnergy() instanceof LiquidAIEnergy))
            return false;

        // Get the fluid
        Fluid inputFluid = input.getEnergy();


        // Is the cell partitioned?
        if( this.isPartitioned() )
        {
            // Get the input Energy
            LiquidAIEnergy inputEnergy = ( (LiquidAIEnergy)inputFluid ).getEnergy();

            // Is the cell partitioned for this Energy?
            if( !this.partitionEnergies.contains( inputEnergy ) )
            {
                // Cell partition will not allow this Energy.
                return false;
            }
        }

        // Return if there is a match or empty slot for the Energy
        return( -1 != this.getSlotFor( ( (LiquidAIEnergy)inputFluid ).getEnergy() ) );
    }

    /**
     * Removes all partitioning from the cell.
     */
    public void clearPartitioning()
    {
        // Clear the list
        this.partitionEnergies.clear();

        // Update the cell data
        this.writePartitionList();
    }

    /**
     * Attempts to extract Energy from the cell.
     * returns the number of items extracted, null
     */
    @Override
    public IAEEnergyStack extractItems( final IAEEnergyStack request, final Actionable mode, final IActionSource src )
    {
        // Ensure there is a request, and that it is an Energy gas
        if( ( request == null ) || ( request.getEnergy() == null ) || ( !( request.getEnergy() instanceof LiquidAIEnergy) ) )
        {
            // Invalid request.
            return null;
        }

        // Get the Energy of the Energy
        LiquidAIEnergy requestEnergy = ((LiquidAIEnergy) request.getEnergy()).getEnergy();

        // Calculate the amount of Energy to extract
        long EnergyAmountRequested = request.getStackSize();

        // Is the requested amount a whole Energy?
        if( EnergyAmountRequested == 0 )
        {
            // Can not extract partial amounts
            return null;
        }

        // Extract
        long extractedEnergyAmount = this.extractEnergyFromCell( requestEnergy, EnergyAmountRequested, mode );

        // Did we extract any?
        if( extractedEnergyAmount == 0 )
        {
            // Nothing was extracted
            return null;
        }

        // Copy the request
        IAEEnergyStack extractedFluid = request.copy();

        // Set the amount extracted
        extractedFluid.setStackSize( extractedEnergyAmount );
        return extractedFluid;

    }

    /**
     * Mode required to access the cell.
     */
    @Override
    public AccessRestriction getAccess()
    {
        return AccessRestriction.READ_WRITE;
    }

    /**
     * Gets the list of Energy energies stored on the cell.
     */
    @Override
    public IItemList<IAEEnergyStack> getAvailableItems( final IItemList<IAEEnergyStack> availableList )
    {
        for( IEnergyStack EnergyStack : this.storedEnergy )
        {
            // Skip if null
            if( EnergyStack == null )
            {
                continue;
            }


            LiquidAIEnergy Energy = EnergyStack.getEnergy();

            // Create the AE fluid stack
            availableList.add(getChannel().createStack(new FluidStack(Energy,(int)EnergyStack.getStackSize())));

        }

        return availableList;
    }

    /**
     * Which storage channel this cell is on.
     */
    @Override
    public IStorageChannel<IAEEnergyStack> getChannel()
    {
        return AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class);
    }


    public long getFreeBytes()
    {
        return this.totalBytes - this.getUsedBytes();
    }


    public ArrayList<LiquidAIEnergy> getPartitionEnergies()
    {
        return this.partitionEnergies;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }


    @Override
    public int getSlot()
    {
        return 0;
    }


    public SortMode getSortingMode()
    {
        return this.mode;
    }

    /**
     * Gets a list of the stored Energy on this cell.
     *
     * @return
     */
    public List<IEnergyStack> getStoredEnergy()
    {
        // Make the list
        List<IEnergyStack> storedList = new ArrayList<IEnergyStack>( this.totalTypes );

        // Add each non-null stack
        for( IEnergyStack stack : this.storedEnergy )
        {
            if( stack != null )
            {
                storedList.add( stack );
            }
        }

        return storedList;
    }

    /**
     * Total number of bytes the cell can hold.
     *
     * @return
     */
    public long getTotalBytes()
    {
        return this.totalBytes;
    }

    /**
     * Total number of types the cell can hold.
     *
     * @return
     */
    public int getTotalTypes()
    {
        return this.totalTypes;
    }

    /**
     * Returns how many bytes are used.
     *
     * @return
     */
    public long getUsedBytes()
    {
        return (long)Math.ceil( this.usedEnergyStorage / (double)HandlerItemEnergyCell.ENERGY_PER_BYTE );
    }

    /**
     * Returns how many types are used.
     *
     * @return
     */
    public int getUsedTypes()
    {
        // Assume we are empty
        int typeCount = 0;

        // Count the number of valid types
        for( IEnergyStack stack : this.storedEnergy )
        {
            if( stack != null )
            {
                typeCount++ ;
            }
        }

        // Return the count
        return typeCount;
    }

    /**
     * Gets the view mode.
     *
     * @return
     */
    public ViewItems getViewMode()
    {
        return this.viewMode;
    }

    /**
     * Attempts to add Energy to the cell.
     * returns the number of items not added.
     */
    @Override
    public IAEEnergyStack injectItems( final IAEEnergyStack input, final Actionable mode, final IActionSource src )
    {
        // Ensure we have an input.
        if( ( input == null ) )
        {
            // No input
            return null;
        }

        // Ensure the input is a energy
        if( ( input.getEnergy() == null ) || !( input.getEnergy() instanceof LiquidAIEnergy) )
        {
            // Invalid fluid
            return input.copy();
        }

        // Ensure the Energy can be accepted
        if( !this.canAccept( input ) )
        {
            // Can not accept this Energy
            return input.copy();
        }

        // Get the Energy
        LiquidAIEnergy EnergyEnergy = (LiquidAIEnergy)input.getEnergy();

        // Calculate the amount to store
        long amountToStore = input.getStackSize();

        // Is the amount a whole Energy?
        if( amountToStore == 0 )
        {
            // Can not store partial amounts.
            return input.copy();
        }

        // Get the amount not stored
        long amountNotStored = this.addEnergyToCell( EnergyEnergy, amountToStore, mode );

        // Was all stored?
        if( amountNotStored == 0 )
        {
            // All was stored
            return null;
        }

        // Copy the input
        IAEEnergyStack result = input.copy();

        // Set the size to how much was left over
        result.setStackSize( amountNotStored );

        return result;
    }

    /**
     * Is the cell being handled a creative cell?
     *
     * @return
     */
    public boolean isCreative()
    {
        return false;
    }

    /**
     * Returns true if the cell is partitioned.
     *
     * @return
     */
    public boolean isPartitioned()
    {
        return( this.partitionEnergies.size() != 0 );
    }

    /**
     * Is the cell partitioned to accept the fluid?
     */
    @Override
    public boolean isPrioritized( final IAEEnergyStack input )
    {
        // Is the cell partitioned?
        if( this.isPartitioned() )
        {
            // Ensure there is an input
            if( input == null )
            {
                // Null input
                return false;
            }

            // Get the fluid
            Fluid inputFluid = input.getEnergy();

            // Is the fluid an Energy gas?
            if( !( inputFluid instanceof LiquidAIEnergy) )
            {
                // Not Energy gas
                return false;
            }

            // Get the Energy
            LiquidAIEnergy inputEnergy = ( (LiquidAIEnergy)inputFluid ).getEnergy();

            // Is the cell partitioned for this Energy?
            return this.partitionEnergies.contains( inputEnergy );
        }

        return false;
    }

    /**
     * Sets the partition list to the contents of the cell.
     */
    public void partitionToCellContents()
    {
        // Clear any existing partition data
        this.partitionEnergies.clear();

        // Loop over the cell contents
        for( int slotIndex = 0; slotIndex < this.totalTypes; slotIndex++ )
        {
            // Is there anything stored in this slot?
            if( this.storedEnergy[slotIndex] != null )
            {
                // Add to the partition list
                this.partitionEnergies.add( this.storedEnergy[slotIndex].getEnergy() );
            }
        }

        // Write changes
        this.writePartitionList();
    }

    /**
     * Removes an Energy from the cells partitioning.
     *
     * @param Energy
     */
    public boolean removeEnergyFromPartitionList( final LiquidAIEnergy Energy )
    {
        // Was the Energy removed?
        if( this.partitionEnergies.remove( Energy ) )
        {
            // Update the cell data
            this.writePartitionList();

            return true;
        }

        return false;
    }

    /**
     * Replaces one Energy with another in the partition list.
     *
     * @param originalEnergy
     * @param newEnergy
     */
    public boolean replaceEnergyInPartitionList(final LiquidAIEnergy originalEnergy, final LiquidAIEnergy newEnergy )
    {
        // Get the index of the original Energy.
        int index = this.partitionEnergies.indexOf( originalEnergy );

        // Is the original Energy in the list?
        if( index >= 0 )
        {
            // Replace the Energy.
            this.partitionEnergies.set( index, newEnergy );

            // Update the cell data
            this.writePartitionList();

            return true;
        }

        return false;
    }

    public void setSortingMode( final SortMode mode )
    {
        // Set the mode
        this.mode = mode;

        // Store the mode
        if( this.mode != SortMode.NUMERIC )
        {
            this.cellData.setInteger( NBT_SORT_KEY, mode.ordinal() );
        }
    }

    public void setViewMode( final ViewItems viewMode )
    {
        // Set the mode
        this.viewMode = viewMode;

        // Store the mode
        if( this.viewMode != DEFAULT_VIEW_MODE )
        {
            this.cellData.setInteger( NBT_KEY_VIEW_MODE, this.viewMode.ordinal() );
        }
    }

    /**
     * Valid for pass 1 if partitioned or has stored Energy.
     * Energy.
     * Valid for pass 2 if not partitioned and empty.
     *
     * @return
     */
    @Override
    public boolean validForPass( final int pass )
    {
        boolean isPartitioned = this.isPartitioned();
        boolean hasStoredEnergy = ( this.usedEnergyStorage > 0 );

        // Is this the priority pass?
        if( pass == 1 )
        {
            // Valid if is partitioned or cell has something in it.
            return( isPartitioned || hasStoredEnergy );
        }

        // Valid if not partitioned and cell is empty.
        return( ( !isPartitioned ) && ( !hasStoredEnergy ) );
    }
}
package AppliedIntegrations.Container;

import AppliedIntegrations.API.*;
import AppliedIntegrations.API.Grid.ICraftingIssuerHost;
import AppliedIntegrations.API.Gui.ICraftingIssuerContainer;
import AppliedIntegrations.API.Storage.EnergyRepo;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyRepo;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Container.slot.SlotRestrictive;

import AppliedIntegrations.Utils.EffectiveSide;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEFluidStack;
import appeng.me.helpers.BaseActionSource;
import appeng.me.helpers.MachineSource;
import appeng.me.helpers.PlayerSource;
import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.IElectricItem;
import mekanism.api.energy.IEnergizedItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nullable;
import java.util.Collection;
/**
 * @Author Azazell
 */
public abstract class ContainerEnergyTerminalDuality extends ContainerWithPlayerInventory implements IMEMonitorHandlerReceiver<IAEEnergyStack>, IEnergySelectorContainer, ICraftingIssuerContainer {
    /**
     * X position for the output slot
     */
    private static final int OUTPUT_POSITION_X = 26;

    /**
     * Y position for the output slot
     */
    private static final int OUTPUT_POSITION_Y = 92;

    /**
     * X position for the input slot
     */
    private static final int INPUT_POSITION_X = 8;

    /**
     * Y position for the input slot
     */
    private static final int INPUT_POSITION_Y = ContainerEnergyTerminalDuality.OUTPUT_POSITION_Y;

    /**
     * Y position for the player inventory
     */
    protected static final int PLAYER_INV_POSITION_Y = 122;

    /**
     * Y position for the hotbar inventory
     */
    protected static final int HOTBAR_INV_POSITION_Y = 180;

    /**
     * The minimum amount of time to wait before playing
     * sounds again. In ms.
     */
    private static final int MINIMUM_SOUND_WAIT = 900;

    /**
     * The number of ticks required to pass before doWork is called.
     */
    private static final int WORK_TICK_RATE = 3;

    /**
     * The maximum amount of energy to try and transfer each time
     * the transfer method is called.
     * This is a soft-cap.
     */
    private static final int ENEGY_TRANSFER_PER_WORK_CYCLE = 64;

    /**
     * Inventory indices for the input and output
     */
    private static int OUTPUT_INV_INDEX = 1, INPUT_INV_INDEX = 0;

    /**
     * Location of the splash sound
     */
    private final String soundLocation_Splash = "game.neutral.swim";

    /**
     * Location of the paper sound
     */
    private final String soundLocation_Paper = "thaumcraft:page";

    /**
     * The last known stack size stored in the export slot
     */
    private int audioStackSizeTracker = 0;

    /**
     * Work slots
     */
    private Slot inputSlot;

    /**
     * Import and export inventory
     */
    private IInventory inventory;

    /**
     * The last time, in ms, the transfer sound played
     */
    private long lastSoundPlaytime = 0;

    /**
     * Work slots
     */
    private Slot outputSlot;

    /**
     * The Energy the user has selected.
     */
    private LiquidAIEnergy selectedEnergy;

    /**
     * Tracks the number of ticks
     */
    private int tickCounter = 0;

    /**
     * Tracks the amount of work performed.
     */
    private int workCounter = 0;

    /**
     * List of energies on the network
     */
    protected final IEnergyRepo repo;

    /**
     * Energy network monitor
     */
    private IMEMonitor<IAEEnergyStack> energyMonitor;

    /**
     * Create the container and register the owner
     *
     * @param player
     */
    public ContainerEnergyTerminalDuality(final EntityPlayer player )
    {
        // Call super
        super( player );

        if( EffectiveSide.isClientSide() )
        {
            // Set the sound time
            this.lastSoundPlaytime = System.currentTimeMillis();
        }

        // Create the reop
        this.repo = new EnergyRepo();
    }

    /**
     * Attach this container to the Energy monitor
     *
     */
    private boolean attachToMonitor( final IMEMonitor<IAEEnergyStack> eMonitor )
    {
        if( ( EffectiveSide.isServerSide() ) && ( eMonitor != null ) )
        {
            // Get the grid
            IGrid grid = this.getHostGrid();
            if( grid == null )
            {
                return false;
            }

            // Detach from any current monitor
            if( this.energyMonitor != null )
            {
                this.detachFromMonitor();
            }

            // Set the monitor
            this.energyMonitor = eMonitor;

            // Listen
            this.energyMonitor.addListener( this, grid.hashCode() );

            // Update our cached list of energies
           // this.repo.copyFrom( this.energyMonitor.getStorageList() );
            return true;
        }

        return false;
    }

    /**
     * Returns true if the specified stack can be merged into the output slot.
     *
     * @param stackToMerge
     * @return True if the slot is empty,
     * or if can be merged by increasing the slots stacksize by the specified stacks stacksize.
     */
    private boolean canMergeWithOutputSlot( final ItemStack stackToMerge )
    {
        // Ensure the stack is not null.
        if( stackToMerge == null )
        {
            // Invalid itemstack
            return false;
        }

        // Is the slot empty?
        if( !this.outputSlot.getHasStack() )
        {
            return true;
        }

        // Get the slot stack
        ItemStack slotStack = this.outputSlot.getStack();

        // Get the stack size
        int slotStackSize = slotStack.getCount();

        // Is the slot full?
        if( slotStack.getMaxStackSize() == slotStackSize )
        {
            return false;
        }

        // Will adding the stack cause the slot to be over full?
        if( ( slotStackSize + stackToMerge.getCount() ) > slotStack.getMaxStackSize() )
        {
            return false;
        }

        // Do the stacks match?
        // Compare ignoring stack size
        ItemStack o = slotStack.copy();
        ItemStack n = stackToMerge.copy();
        o.setCount(1);
        n.setCount(1);
        return ItemStack.areItemStacksEqual( o, n );
    }

    /**
     * Returns if the player has the requested permission or not.
     *
     * @param perm
     * @param actionSource
     * @return
     */
    private boolean checkSecurityPermission(final SecurityPermissions perm, final IActionSource actionSource )
    {

        // Ensure there is an action source.
        if( actionSource == null )
        {
            return false;
        }

        // Get the source node
        IGridNode sourceNode = null;
        if( actionSource instanceof MachineSource)
        {
            sourceNode = ( (MachineSource)actionSource ).machine().get().getActionableNode();
        }
        else if( actionSource instanceof PlayerSource)
        {
            sourceNode = ( (PlayerSource)actionSource ).machine().get().getActionableNode();
        }

        // Ensure there is a node
        if( sourceNode == null )
        {
            return false;
        }

        // Get the security grid for the node.
        ISecurityGrid sGrid = sourceNode.getGrid().getCache( ISecurityGrid.class );

        // Return the permission.
        return sGrid.hasPermission( this.player, perm );
    }

  
    @Nullable
    private ImmutablePair<Integer, ItemStack> drainContainer(final ItemStack container, final BaseActionSource actionSource,
                                                             final Actionable mode )
    {
        // Ensure there is a container
        if( container == null )
        {
            return null;
        }

        // Get the fluid stack from the item
        IEnergyStack containerEnergy = new EnergyStack(Utils.getEnergyFromItemStack(container),Utils.getEnergyInContainer(container));

        // Ensure there is something to drain
        if( ( containerEnergy == null ) || containerEnergy.isEmpty() )
        {
            // Nothing to drain
            return null;
        }

        // Get the proposed drain amount.
        int amount = (int)containerEnergy.getStackSize();

        // Do a network injection
        long rejectedAmount = this.energyMonitor.injectItems(AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class).createStack(new FluidStack(containerEnergy.getEnergy(),amount)), mode, actionSource).getStackSize();

        // Was any rejected?
        if( rejectedAmount > 0 )
        {
            amount -= (int)rejectedAmount;
            
            if( amount <= 0 )
            {
                // Network is full
                return null;
            }
        }

        // Adjust work counter
        if( mode == Actionable.MODULATE )
        {
            this.workCounter += amount;
        }

        // Attempt to drain the container
        return Utils.extractFromContainer( container, amount );
    }

    /**
     * Fills an energy container item.
     *
     * @param withEnergy
     * @param container
     * @param actionSource
     * @param mode
     * @return The result of the fill. <AmountFilled, NewContainer>
     */
    @Nullable
    private ImmutablePair<Integer, ItemStack> fillContainer(final LiquidAIEnergy withEnergy, final ItemStack container,
                                                            final BaseActionSource actionSource, final Actionable mode )
    {
        // Ensure there is an Energy
        if( withEnergy == null )
        {
            return null;
        }

        // Get the capacity of the container
        int containerCapacity = Utils.getContainerCapacity( container );

        // Can the container hold energy?
        if( containerCapacity == 0 )
        {
            // Invalid container
            return null;
        }

        // Do an extraction from the network
        long extractedAmount = this.energyMonitor.extractItems(AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class).createStack(new FluidStack(withEnergy,containerCapacity)), mode, actionSource ).getStackSize();

        // Was anything extracted?
        if( extractedAmount <= 0 )
        {
            // Gas is not present on network.
            return null;
        }

        // Calculate the proposed amount, based on how much we need and how much
        // is available
        int proposedFillAmount = (int)Math.min( containerCapacity, extractedAmount );

        // Adjust work counter
        if( mode == Actionable.MODULATE )
        {
            this.workCounter += proposedFillAmount;
        }

        // Create a new container filled to the proposed amount
        return Utils.injectInContainer(container,proposedFillAmount);
    }

    /**
     * Binds the container to the specified inventory and the players inventory.
     *
     * @param inventory
     */
    protected void bindToInventory( final IInventory inventory )
    {
        // Set the inventory
        this.inventory = inventory;

        // Create the input slot
        this.inputSlot = new SlotRestrictive( inventory, ContainerEnergyTerminalDuality.INPUT_INV_INDEX,
                ContainerEnergyTerminalDuality.INPUT_POSITION_X, ContainerEnergyTerminalDuality.INPUT_POSITION_Y );
        this.addSlotToContainer( this.inputSlot );

        // Create the output slot
        this.outputSlot = new SlotFurnaceOutput( this.player, inventory, ContainerEnergyTerminalDuality.OUTPUT_INV_INDEX,
                ContainerEnergyTerminalDuality.OUTPUT_POSITION_X, ContainerEnergyTerminalDuality.OUTPUT_POSITION_Y );
        this.addSlotToContainer( this.outputSlot );

        // Bind to the player's inventory
        this.bindPlayerInventory( this.player.inventory, ContainerEnergyTerminalDuality.PLAYER_INV_POSITION_Y,
                ContainerEnergyTerminalDuality.HOTBAR_INV_POSITION_Y );

    }

    /**
     * Detaches from the monitor if attached.
     */
    protected void detachFromMonitor()
    {
        if( EffectiveSide.isServerSide() )
        {
            if( this.energyMonitor != null )
            {
                // Stop listening
                this.energyMonitor.removeListener( this );

                // Null the monitor
                this.energyMonitor = null;

                // Clear the repo
                this.repo.clear();
            }
        }
    }

    /**
     * Checks if there is any work to perform.
     * If there is it does so.
     */
    @Override
    protected boolean detectAndSendChangesMP( final EntityPlayerMP playerMP )
    {
        // Compare selected energies
        if( this.getHostSelectedEnergy() != this.selectedEnergy )
        {
            // Update the selected Energy
            this.selectedEnergy = this.getHostSelectedEnergy();


        }

        // Is there a monitor?
        if( this.energyMonitor != null )
        {
            // Inc tick tracker
            this.tickCounter += 1;

            if( this.tickCounter > ContainerEnergyTerminalDuality.WORK_TICK_RATE )
            {
                // Do work
                this.doWork( this.tickCounter );

                // Reset the tick counter
                this.tickCounter = 0;

                return true;
            }
        }
        else
        {
            // Attempt to attach to a monitor
            if( this.attachToMonitor( this.getNewMonitor() ) )
            {
                // Send update
                this.onClientRequestFullUpdate();
            }

        }

        return false;
    }

    /**
     * Called periodically so that the container can perform work.
     */
    protected abstract void doWork( int elapsedTicks );

    /**
     * Gets the action source.
     *
     * @return
     */
    protected abstract BaseActionSource getActionSource();

    /**
     * Gets the grid for the host.
     *
     * @return
     */
    @Nullable
    protected abstract IGrid getHostGrid();

    /**
     * Return the selected Energy stored in the host.
     *
     * @return
     */
    @Nullable
    protected abstract LiquidAIEnergy getHostSelectedEnergy();

    /**
     * Attempts to get a new energy monitor.
     *
     * @return
     */
    @Nullable
    protected abstract IMEMonitor<IAEEnergyStack> getNewMonitor();

    /**
     * Sets the hosts selected energy.
     *
     * @param Energy
     */
    protected abstract void setHostSelectedEnergy( @Nullable LiquidAIEnergy Energy );

    /**
     * Fills, drains, or sets label energy.
     *
     * @param stack
     * This is not modified during the course of this function.
     * @param energy
     * Ignored when draining
     * @param actionSource
     * @param mode
     * @return The new stack if changes made, the original stack otherwise.
     */
    protected ItemStack transferEnergy(final ItemStack stack, final LiquidAIEnergy energy, final BaseActionSource actionSource, final Actionable mode )
    {
        // Ensure the stack & monitor are not null
        if( ( stack == null ) || ( this.energyMonitor == null ) )
        {
            return stack;
        }


        // Valid container?
        if( stack.getItem() instanceof IEnergyContainerItem || stack.getItem() instanceof IElectricItem || stack.getItem() instanceof IEnergizedItem)
        {
            // Invalid container
            return stack;
        }

        // Result of the operation
        ImmutablePair<Integer, ItemStack> result = null;

        // Filling?
        if( Utils.getContainerCapacity(stack) < Utils.getContainerMaxCapacity(stack) )
        {
            // Check extract permission
            if( this.checkSecurityPermission( SecurityPermissions.EXTRACT, actionSource ) )
            {
                // Attempt to fill the container
                result = this.fillContainer( energy, stack, actionSource, mode );
            }
        }
        // Draining
        else
        {
            // Check inject permission
            if( this.checkSecurityPermission( SecurityPermissions.INJECT, actionSource ) )
            {
                // Attempt to drain the container
                result = this.drainContainer( stack, actionSource, mode );
            }
        }

        // Is there any result?
        if( result != null )
        {
            // Return the new stack.
            return result.right;
        }

        // No result
        return stack;
    }

    /**
     * Transfers energy in or out of the system using the input and output slots.
     */
    protected void transferEnergyFromWorkSlots()
    {
        // Ensure the inventory is valid
        if( this.inventory == null )
        {
            return;
        }

        // Get the input stack
        final ItemStack inputStack = this.inventory.getStackInSlot( ContainerEnergyTerminalDuality.INPUT_INV_INDEX );

        // Ensure the input stack is not empty
        if( ( inputStack == null ) )
        {
            // Nothing in input slot.
            return;
        }

        // Is the output slot full?
        if( this.outputSlot.getHasStack() )
        {
            ItemStack outputStack = this.outputSlot.getStack();
            if( outputStack.getCount() >= outputStack.getMaxStackSize() )
            {
                // Output slot is full.
                return;
            }
        }

        // Reset the work counter
        this.workCounter = 0;

        // Get the action source
        final BaseActionSource actionSource = this.getActionSource();

        // Copy the input
        final ItemStack container = inputStack.copy();
        container.setCount(1);

        // Loop while maximum work has not been performed, there is input, and the operation did not fail.
        ItemStack result;
        do
        {
            // Simulate the work
            result = this.transferEnergy( container, this.selectedEnergy, actionSource, Actionable.SIMULATE );

            // Did anything change?
            if( ( result == null ) || ( result == container ) )
            {
                // No work to perform
                break;
            }

            // Can the result not be merged with the output stack?
            if( !this.canMergeWithOutputSlot( result ) )
            {
                // Unable to merge
                break;
            }

            // Perform the work
            result = this.transferEnergy( container, this.selectedEnergy, actionSource, Actionable.MODULATE );

            // Merge the result
            if( this.outputSlot.getHasStack() )
            {
                // Can just increment here because canMergeWithOutputSlot explicitly checks that
                this.outputSlot.getStack().setCount(this.outputSlot.getStack().getCount()+1);
            }
            else
            {
                this.outputSlot.putStack( result );
            }

            // Is the input drained?
            if( ( inputStack.getCount()-1 ) == 0 )
            {
                this.inputSlot.putStack( null );
                break;
            }
        }
        while( this.workCounter < ContainerEnergyTerminalDuality.ENEGY_TRANSFER_PER_WORK_CYCLE );
    }

    /**
     * Gets the list of energy stacks in the container.
     *
     * @return
     */
    public Collection<IEnergyStack> getEnergyStackList()
    {
        return this.repo.getAll();
    }

    @Override
    public abstract ICraftingIssuerHost getCraftingHost();

    /**
     * Get the player that owns this container
     *
     * @return
     */
    public EntityPlayer getPlayer()
    {
        return this.player;
    }

    /**
     * Gets the energy that the player has selected.
     *
     * @return
     */
    public LiquidAIEnergy getSelectedEnergy()
    {
        return this.selectedEnergy;
    }

    /**
     * Is this container still valid for receiving updates
     * from the AE monitor?
     */
    @Override
    public final boolean isValid( final Object verificationToken )
    {
        // Get the grid
        IGrid grid = this.getHostGrid();
        if( grid != null )
        {
            // Do the hash codes match?
            if( grid.hashCode() == (Integer)verificationToken )
            {
                return true;
            }
        }

        // No longer valid (Do not call detach, this will happen automatically)
        this.energyMonitor = null;
        this.repo.clear();

        // Update the client
        this.onClientRequestFullUpdate();

        return false;
    }


    /**
     * Called when a client requests the state of the container.
     * Updates our cached list of energies
     */
    public abstract void onClientRequestFullUpdate();

    public abstract void onClientRequestSortModeChange( final EntityPlayer player, boolean backwards );

    /**
     * Called when a client sends a view mode change request.
     *
     * @param player
     */
    public abstract void onClientRequestViewModeChange( final EntityPlayer player, boolean backwards );

    /**
     * Unregister this container from the monitor.
     */
    @Override
    public void onContainerClosed( final EntityPlayer player )
    {
        // Call super
        super.onContainerClosed( player );

        // Detach from the monitor
        this.detachFromMonitor();
    }

    /**
     * Called when a player clicked on an Energy while holding an item.
     *
     * @param player
     * @param Energy
     */
    public void onInteractWithHeldItem( final EntityPlayer player, final LiquidAIEnergy Energy )
    {
        // Sanity check
        if( ( ( player == null ) || ( player.inventory.getItemStack() == null ) ) )
        {
            return;
        }

        // Get the item
        ItemStack sourceStack = player.inventory.getItemStack();

        // Create a new stack
        final ItemStack takeFrom = sourceStack.copy();
        takeFrom.setCount(1);

        // Get the action source
        final BaseActionSource actionSource = this.getActionSource();

        // Simulate the transfer
        ItemStack resultStack = this.transferEnergy( takeFrom, Energy, actionSource, Actionable.SIMULATE );

        // Was any work performed?
        if( ( resultStack == null ) || ( resultStack == takeFrom ) )
        {
            // Nothing to do.
            return;
        }

        // If the source stack size is > 1, the result will need to be put into the player inventory
        if( sourceStack.getCount() > 1 )
        {
            // Attempt to merge
            if( !this.mergeSlotWithHotbarInventory( resultStack ) )
            {
                if( !this.mergeSlotWithPlayerInventory( resultStack ) )
                {
                    // Could not merge
                    return;
                }
            }

            // Decrement the source stack
            sourceStack.setCount(sourceStack.getCount()-1);
        }
        else
        {
            // Set the source stack to the result
            sourceStack = resultStack;
        }

        // Perform the work
        this.transferEnergy( takeFrom, Energy, actionSource, Actionable.MODULATE );

        // Set what the player is holding
        player.inventory.setItemStack( sourceStack );

        // Update

        if( player instanceof EntityPlayerMP )
        {
            ( (EntityPlayerMP)player ).isChangingQuantityOnly = false;
        }
        this.detectAndSendChanges();


    }

    /**
     * Called by the gui when the Energy list arrives.
     *
     */
    public void onReceivedEnergyList( final Collection<IEnergyStack> energyStackList )
    {
        // Set the Energy list
        this.repo.copyFrom( energyStackList );
    }

    /**
     * Called by the gui when a change arrives.
     *
     * @param change
     */
    public void onReceivedEnergyListChange( final IEnergyStack change )
    {
        // Ignored server side
        if( EffectiveSide.isServerSide() )
        {
            return;
        }

        // Ensure the change is not null
        if( change == null )
        {
            return;
        }

        // Post the change
        this.repo.postChange( change );
    }

    /**
     * Called when the the selected Energy has changed.
     *
     */
    public void onReceivedSelectedEnergy( final LiquidAIEnergy selectedEnergy )
    {
        // Is this server side?
        if( EffectiveSide.isServerSide() )
        {
            this.setHostSelectedEnergy( selectedEnergy );
        }
        else
        {
            this.selectedEnergy = selectedEnergy;
        }
    }

    /**
     * Checks if the transfer sound should play.
     * if checkWorkSlots is true the type will be automatically determined.
     *
     * @param player
     * @param checkWorkSlots
     * @param type
     * 0 = splash, 1 = paper
     */

    public void playTransferSound( final EntityPlayer player, final boolean checkWorkSlots, int type )
    {
        if( checkWorkSlots )
        {
            // Get the itemstack in the output slot
            ItemStack itemStack = this.outputSlot.getStack();

            // Is there anything in the second slot?
            if( itemStack != null )
            {

              type = 0;

                // Has the count changed?
                if( this.audioStackSizeTracker == itemStack.getCount() )
                {
                    // Nothing changed
                    return;
                }

                // Set the count
                this.audioStackSizeTracker = itemStack.getCount();

            }
            else
            {
                // Reset the count
                this.audioStackSizeTracker = 0;
                return;
            }

        }

        // Has enough time passed to play the sound again?
        if( ( System.currentTimeMillis() - this.lastSoundPlaytime ) > ContainerEnergyTerminalDuality.MINIMUM_SOUND_WAIT )
        {


            // Set the playtime
            this.lastSoundPlaytime = System.currentTimeMillis();
        }
    }

    @Override
    public void putStackInSlot( final int slotNumber, final ItemStack stack )
    {
        // Call super
        super.putStackInSlot( slotNumber, stack );

        // Is this client side?
        if( ( this.outputSlot.slotNumber == slotNumber ) && EffectiveSide.isClientSide() )
        {
            this.playTransferSound( null, true, 0 );
        }
    }

    /**
     * Called when the user has clicked on an Energy.
     * Sends that change to the server for validation.
     */
    @Override
    public void setSelectedEnergy( final LiquidAIEnergy selectedEnergy )
    {

    }

    @Override
    public ItemStack transferStackInSlot( final EntityPlayer player, final int slotNumber )
    {
        // Get the slot that was shift-clicked
        Slot slot = this.getSlotOrNull( slotNumber );

        // Is there a valid slot with and item?
        if( ( slot != null ) && ( slot.getHasStack() ) )
        {
            boolean didMerge = false;

            // Get the itemstack in the slot
            ItemStack slotStack = slot.getStack();

            // Was the slot clicked the input slot or output slot?
            if( ( slot == this.inputSlot ) || ( slot == this.outputSlot ) )
            {
                // Attempt to merge with the player inventory
                didMerge = this.mergeSlotWithPlayerInventory( slotStack );
            }
            // Was the slot clicked in the player or hotbar inventory?
            else if( this.slotClickedWasInPlayerInventory( slotNumber ) || this.slotClickedWasInHotbarInventory( slotNumber ) )
            {
                // Is the item valid for the input slot?
                if( this.inputSlot.isItemValid( slotStack ) )
                {
                    // Attempt to merge with the input slot
                    didMerge = this.mergeItemStack( slotStack, this.inputSlot.slotNumber, this.inputSlot.slotNumber + 1, false );
                }

                // Did we merge?
                if( !didMerge )
                {
                    didMerge = this.swapSlotInventoryHotbar( slotNumber, slotStack );
                }

            }

            if( didMerge )
            {
                // Did the merger drain the stack?
                if( slotStack.getCount() == 0 )
                {
                    // Set the slot to have no item
                    slot.putStack( null );
                }
                else
                {
                    // Inform the slot its stack changed;
                    slot.onSlotChanged();
                }
                this.detectAndSendChanges();
            }

        }

        return null;
    }
}

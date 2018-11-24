package AppliedIntegrations.Parts;

import AppliedIntegrations.API.IInventoryUpdateReceiver;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Container.ContainerPartEnergyIOBus;
import AppliedIntegrations.Gui.GuiEnergyIO;

import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketServerFilter;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.Utils.EffectiveSide;

import AppliedIntegrations.Utils.AIPrivateInventory;
import appeng.api.AEApi;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.definitions.IMaterials;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.PartItemStack;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

import static cpw.mods.fml.relauncher.Side.CLIENT;

/**
 * @Author Azazell
 */

public abstract class AIOPart
        extends AIPart
        implements IGridTickable, IEnergyMachine, IAEAppEngInventory,IInventoryUpdateReceiver
{
    /**
     * How much energy can be transfered per second.
     */
    private final static int BASE_TRANSFER_PER_SECOND = 4;

    /**
     * How much additional energy can be transfered per upgrade.
     */
    private final static int ADDITIONAL_TRANSFER_PER_SECOND = 8;

    private final static int MINIMUM_TICKS_PER_OPERATION = 10;

    private final static int MAXIMUM_TICKS_PER_OPERATION = 40;

    private final static int MAXIMUM_TRANSFER_PER_SECOND = 64;

    private final static int MINIMUM_TRANSFER_PER_SECOND = 1;

    protected int maxTransfer;
    /**
     * Maximum number of filter slots.
     */
    private final static int MAX_FILTER_SIZE = 9;

    private final static int BASE_SLOT_INDEX = 4;

    private final static int[] TIER2_INDEXS = { 0, 2, 6, 8 };

    private final static int[] TIER1_INDEXS = { 1, 3, 5, 7 };

    private final static int UPGRADE_INVENTORY_SIZE = 4;

    /**
     * How much AE power is required to keep the part active.
     */
    private static final double IDLE_POWER_DRAIN = 0.7;

    /**
     * Default redstone mode for the bus.
     */
    private static final RedstoneMode DEFAULT_REDSTONE_MODE = RedstoneMode.IGNORE;

    /**
     * NBT Keys
     */
    private static final String NBT_KEY_REDSTONE_MODE = "redstoneMode", NBT_KEY_FILTER_NUMBER = "EnergyFilter#",
            NBT_KEY_UPGRADE_INV = "upgradeInventory";

    private boolean lastRedstone;

    private int[] availableFilterSlots = { AIOPart.BASE_SLOT_INDEX };

    private List<ContainerPartEnergyIOBus> listeners = new ArrayList<ContainerPartEnergyIOBus>();

    /**
     * How the bus responds to redstone.
     */
    private RedstoneMode redstoneMode = AIOPart.DEFAULT_REDSTONE_MODE;

    /**
     * Network source representing this part.
     */
    protected MachineSource asMachineSource;

    protected List<LiquidAIEnergy> filteredEnergies = new ArrayList<LiquidAIEnergy>( AIOPart.MAX_FILTER_SIZE );

    protected IEnergyReceiver facingContainer;

    protected byte filterSize;
    protected byte speedState;

    protected byte upgradeSpeedCount = 0;

    protected boolean redstoneControlled;

    public AIOPart(final PartEnum associatedPart, final SecurityPermissions... interactionPermissions )
    {
        super( associatedPart, interactionPermissions );

        // Initialize the list
        for(int index = 0; index < AIOPart.MAX_FILTER_SIZE; index++ )
        {
            this.filteredEnergies.add( null );
        }

        // Create the source
        this.asMachineSource = new MachineSource( this );
        maxTransfer = 5000*10*upgradeSpeedCount;
    }

    private boolean canDoWork()
    {
        boolean canWork = true;

        if( this.redstoneControlled )
        {
            switch ( this.getRedstoneMode() )
            {
                case HIGH_SIGNAL:
                    canWork = this.isReceivingRedstonePower();

                    break;
                case IGNORE:
                    break;

                case LOW_SIGNAL:
                    canWork = !this.isReceivingRedstonePower();

                    break;
                case SIGNAL_PULSE:
                    canWork = false;
                    break;
            }
        }

        return canWork;

    }

    protected int getTransferAmountPerSecond()
    {
        return BASE_TRANSFER_PER_SECOND + ( this.upgradeSpeedCount * ADDITIONAL_TRANSFER_PER_SECOND );
    }



    private void resizeAvailableArray()
    {
        // Resize the available slots
        this.availableFilterSlots = new int[1 + ( this.filterSize * 4 )];

        // Add the base slot
        this.availableFilterSlots[0] = AIOPart.BASE_SLOT_INDEX;

        if( this.filterSize < 2 )
        {
            // Reset tier 2 slots
            for(int i = 0; i < AIOPart.TIER2_INDEXS.length; i++ )
            {
                this.filteredEnergies.set( AIOPart.TIER2_INDEXS[i], null );
            }

            if( this.filterSize < 1 )
            {
                // Reset tier 1 slots
                for(int i = 0; i < AIOPart.TIER1_INDEXS.length; i++ )
                {
                    this.filteredEnergies.set( AIOPart.TIER1_INDEXS[i], null );
                }
            }
            else
            {
                // Tier 1 slots
                System.arraycopy( AIOPart.TIER1_INDEXS, 0, this.availableFilterSlots, 1, 4 );
            }
        }
        else
        {
            // Add both
            System.arraycopy( AIOPart.TIER1_INDEXS, 0, this.availableFilterSlots, 1, 4 );
            System.arraycopy( AIOPart.TIER2_INDEXS, 0, this.availableFilterSlots, 5, 4 );
        }
    }
    private AIPrivateInventory upgradeInventory = new AIPrivateInventory("ME Energy Export/Import Bus", 4,
            1, this) {

        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemStack) {
            if (itemStack == null)
                return false;
            if (AEApi.instance().definitions().materials().cardCapacity().isSameAs(itemStack))
                return true;
            else if (AEApi.instance().definitions().materials().cardSpeed().isSameAs(itemStack))
                return true;
            else if (AEApi.instance().definitions().materials().cardRedstone().isSameAs(itemStack))
                return true;
            return false;
        }
    };
    @Override
    public void onInventoryChanged() {
        this.filterSize = 0;
        this.redstoneControlled = false;
        this.speedState = 0;
        for (int i = 0; i < this.upgradeInventory.getSizeInventory(); i++) {
            ItemStack currentStack = this.upgradeInventory.getStackInSlot(i);
            if (currentStack != null) {
                if (AEApi.instance().definitions().materials().cardCapacity().isSameAs(currentStack))
                    this.filterSize++;
                if (AEApi.instance().definitions().materials().cardRedstone().isSameAs(currentStack))
                    this.redstoneControlled = true;
                if (AEApi.instance().definitions().materials().cardSpeed().isSameAs(currentStack))
                    this.speedState++;
            }
        }
    }
    private void notifyListenersOfFilterEnergyChange()
    {
        for( ContainerPartEnergyIOBus listener : this.listeners )
        {
            int i=0;
            for(LiquidAIEnergy energy : this.filteredEnergies) {
                TileEntity host = this.getHostTile();
                NetworkHandler.sendToServer(new PacketServerFilter(energy,i,host.xCoord,host.yCoord,host.zCoord,this.getSide(),host.getWorldObj()));
                i++;
            }
        }
    }
    private void notifyListenersOfFilterSizeChange()
    {
        for( ContainerPartEnergyIOBus listener : this.listeners )
        {
            listener.setFilterSize( this.filterSize );
        }
    }

    @Override
    public AIPrivateInventory getUpgradeInventory() {
        return this.upgradeInventory;
    }
    private void updateUpgradeState()
    {
        int oldFilterSize = this.filterSize;

        this.filterSize = 0;
        this.redstoneControlled = false;
        this.upgradeSpeedCount = 0;

        IMaterials aeMaterals = AEApi.instance().definitions().materials();
            for( int i = 0; i < this.upgradeInventory.getSizeInventory(); i++ )
            {
                ItemStack slotStack = this.upgradeInventory.getStackInSlot( i );

                if( slotStack != null )
                {
                    if( aeMaterals.cardCapacity().isSameAs( slotStack ) )
                    {
                        this.filterSize++ ;
                    }

                    else if( aeMaterals.cardSpeed().isSameAs( slotStack ) )
                    {
                        this.upgradeSpeedCount++ ;
                    }
                }
            }

            // Did the filter size change?
            if( oldFilterSize != this.filterSize )
            {
                this.resizeAvailableArray();
            }

            // Is this client side?
            if( EffectiveSide.isClientSide() )
            {
                return;
            }
            this.notifyListenersOfFilterSizeChange();

        // Did the filter size change?
        if( oldFilterSize != this.filterSize )
        {
            this.resizeAvailableArray();
        }

        // Is this client side?
        if( EffectiveSide.isClientSide() )
        {
            return;
        }


    }

    public boolean addFilteredEnergyFromItemstack( final EntityPlayer player, final ItemStack itemStack )
    {
        LiquidAIEnergy itemEnergy = Utils.getEnergyFromItemStack(itemStack);

        if( itemEnergy != null )
        {
            // Are we already filtering this energy?
            if( this.filteredEnergies.contains( itemEnergy ) )
            {
                return true;
            }

            // Add to the first open slot
            for( int avalibleIndex = 0; avalibleIndex < this.availableFilterSlots.length; avalibleIndex++ )
            {
                int filterIndex = this.availableFilterSlots[avalibleIndex];

                // Is this space empty?
                if( this.filteredEnergies.get( filterIndex ) == null )
                {
                    // Is this server side?
                    if( EffectiveSide.isServerSide() )
                    {
                        // Set the filter
                        this.updateFilter( itemEnergy, filterIndex);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public void addListener( final ContainerPartEnergyIOBus container )
    {
        if( !this.listeners.contains( container ) )
        {
            this.listeners.add( container );
        }
    }

    public abstract boolean energyTransferAllowed( LiquidAIEnergy energy );

    @Override
    public int cableConnectionRenderTo()
    {
        return 5;
    }

    public abstract boolean doWork( int transferAmount );

    @Override
    public final Object getClientGuiElement( final EntityPlayer player )
    {

        return new GuiEnergyIO(
                (Container)this.getServerGuiElement(player),this.getX()
                ,this.getY(),this.getZ(),this.getSide(),this,player);
    }

    @Override
    public void getDrops( List<ItemStack> drops, boolean wrenched) {
        for (ItemStack stack : upgradeInventory.slots) {
            if (stack == null)
                continue;
            drops.add(stack);
        }
    }

    /**
     * Determines how much power the part takes for just
     * existing.
     */
    @Override
    public double getIdlePowerUsage()
    {
        return AIOPart.IDLE_POWER_DRAIN;
    }

    /**
     * Produces a small amount of light when active.
     */
    @Override
    public int getLightLevel()
    {
        return( this.isActive() ? 4 : 0 );
    }

    public RedstoneMode getRedstoneMode()
    {
        return this.redstoneMode;
    }

    @Override
    public Object getServerGuiElement( final EntityPlayer player )
    {
        return new ContainerPartEnergyIOBus( this, player );
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode arg0 )
    {
        return new TickingRequest( MINIMUM_TICKS_PER_OPERATION, MAXIMUM_TICKS_PER_OPERATION, false, false );
    }

    public boolean isVoidAllowed()
    {
        return false;
    }

    @Override
    public boolean onActivate( final EntityPlayer player, final Vec3 position )
    {
        boolean activated = super.onActivate( player, position );
        this.updateUpgradeState();
        return activated;
    }

    @Override
    public void onChangeInventory(final IInventory inv, final int slot, final InvOperation mc, final ItemStack removedStack,
                                  final ItemStack newStack )
    {

    }

    /**
     * Called when a player has clicked the redstone button in the gui.
     *
     * @param player
     */
    public void onClientRequestChangeRedstoneMode( final EntityPlayer player )
    {
        // Get the current ordinal, and increment it
        int nextOrdinal = this.redstoneMode.ordinal() + 1;



    }

    /**
     * Called when a client gui is requesting a full update.
     *
     * @param player
     */
    public void onClientRequestFilterList( final EntityPlayer player )
    {

    }

    @Override
    public void onNeighborChanged()
    {
        // Ignored client side
        if( EffectiveSide.isClientSide() )
        {
            return;
        }

        // Set that we are not facing a container
        this.facingContainer = null;

        // Get the tile we are facing
        TileEntity tileEntity = this.getFacingTile();

        // Are we facing a container?
        if( tileEntity instanceof IEnergyReceiver)
        {
            this.facingContainer = (IEnergyReceiver)tileEntity;
        }

        // Is the bus pulse controlled?
        if( this.redstoneMode == RedstoneMode.SIGNAL_PULSE )
        {
            // Did the state of the redstone change?
            if( this.isReceivingRedstonePower() != this.lastRedstone )
            {
                // Set the previous redstone state
                this.lastRedstone = this.isReceivingRedstonePower();

                // Do work
                this.doWork( this.getTransferAmountPerSecond() );
            }
        }
    }

    /**
     * Called client-side to keep the client-side part in sync
     * with the server-side part. This aids in keeping the
     * gui in sync even in high network lag enviroments.
     *
     * @param filteredEnergies
     */
    @SideOnly(CLIENT)
    public void onReceiveFilterList( final List<LiquidAIEnergy> filteredEnergies )
    {
        this.filteredEnergies = filteredEnergies;
    }

    /**
     * Called client-side to keep the client-side part in sync
     * with the server-side part. This aids in keeping the
     * gui in sync even in high network lag enviroments.
     *
     * @param filterSize
     */
    @SideOnly(CLIENT)
    public void onReceiveFilterSize( final byte filterSize )
    {
        this.filterSize = filterSize;

        this.resizeAvailableArray();
    }

    @Override
    public void readFromNBT( final NBTTagCompound data )
    {
        // Call super
        super.readFromNBT( data );

        // Read redstone mode
        if( data.hasKey( NBT_KEY_REDSTONE_MODE ) )
        {

        }

        // Read filters
        for( int index = 0; index < MAX_FILTER_SIZE; index++ )
        {
            if( data.hasKey( NBT_KEY_FILTER_NUMBER + index ) )
            {
                // Get the energy
                this.filteredEnergies.set( index, LiquidAIEnergy.energies.get( data.getString( NBT_KEY_FILTER_NUMBER + index ) ) );
            }
        }
        data.setTag("upgradeInventory", this.upgradeInventory.writeToNBT());
        // Read upgrade inventory
        this.upgradeInventory.readFromNBT(data.getTagList("upgradeInventory",
                10));
    }

    public void removeListener( final ContainerPartEnergyIOBus container )
    {
        this.listeners.remove( container );
    }

    /**
     * Called when the internal inventory changes.
     */
    @Override
    public void saveChanges()
    {
        this.markForSave();
    }

    @Override
    public final void updateFilter(LiquidAIEnergy energy, int index)
    {
        // Set the filter
        this.filteredEnergies.set( index, energy );
        this.notifyListenersOfFilterEnergyChange();

    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall )
    {
        if( this.canDoWork() )
        {
            // Calculate the amount to transfer per second
            int transferAmountPerSecond = this.getTransferAmountPerSecond();

            // Calculate amount to transfer this operation
            int transferAmount = (int)( transferAmountPerSecond * ( ticksSinceLastCall / 20.F ) );

            // Clamp
            if( transferAmount < MINIMUM_TRANSFER_PER_SECOND )
            {
                transferAmount = MINIMUM_TRANSFER_PER_SECOND;
            }
            else if( transferAmount > MAXIMUM_TRANSFER_PER_SECOND )
            {
                transferAmount = MAXIMUM_TRANSFER_PER_SECOND;
            }

            if( this.doWork( transferAmount ) )
            {
                return TickRateModulation.URGENT;
            }
        }
        this.notifyListenersOfFilterEnergyChange();

        return TickRateModulation.IDLE;
    }

    @Override
    public void writeToNBT( final NBTTagCompound data, final PartItemStack saveType )
    {
        // Call super
        super.writeToNBT( data, saveType );

        if( ( saveType == PartItemStack.World ) || ( saveType == PartItemStack.Wrench ) )
        {
            // Write each filter
            for( int i = 0; i < MAX_FILTER_SIZE; i++ )
            {
                LiquidAIEnergy energy = this.filteredEnergies.get( i );
                if( energy != null )
                {
                    data.setString( NBT_KEY_FILTER_NUMBER + i, energy.getTag() );
                }
            }

            if( saveType == PartItemStack.World ) {
                // Write the redstone mode
                if (this.redstoneMode != DEFAULT_REDSTONE_MODE) {
                    data.setInteger(NBT_KEY_REDSTONE_MODE, this.redstoneMode.ordinal());
                }

                data.setTag("upgradeInventory", this.upgradeInventory.writeToNBT());

            }
        }
    }
}

package AppliedIntegrations.Parts;

import AppliedIntegrations.API.IInventoryHost;
import AppliedIntegrations.API.Storage.EnumCapabilityType;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Container.ContainerPartEnergyIOBus;
import AppliedIntegrations.Gui.AIGuiHandler;

import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Network.Packets.PacketServerFilter;
import AppliedIntegrations.Utils.EffectiveSide;

import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.AEApi;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.definitions.IMaterials;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.PartItemStack;
import appeng.me.helpers.MachineSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;
import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

/**
 * @Author Azazell
 */

public abstract class AIOPart
        extends AIPart
        implements IGridTickable, IEnergyMachine, IAEAppEngInventory, IInventoryHost {
    /**
     * Constant fields
     */
    // How much energy can be transfered in second
    private final static int BASE_ENERGY_TRANSFER = 40;

    // How much energy transfer one upgrade adds
    private final static int TRANSFER_PER_UPGRADE = 80;

    // How much minimum ticks machine need to operate
    private final static int MINIMUM_TICKS_PER_OPERATION = 2;

    // How much max ticks machine needs to opreate
    private final static int MAXIMUM_TICKS_PER_OPERATION = 40;

    // Maximum transfer per second
    private final static int MAXIMUM_TRANSFER_PER_SECOND = 6400;

    // Mininmum transfer per second
    private final static int MINIMUM_TRANSFER_PER_SECOND = 24;

    // Current max transfer
    protected int maxTransfer;

    // Size of filter
    private final static int MAX_FILTER_SIZE = 9;

    private final static int BASE_SLOT_INDEX = 4;

    private final static int[] TIER2_INDEXS = { 0, 2, 6, 8 };

    private final static int[] TIER1_INDEXS = { 1, 3, 5, 7 };

    private final static int UPGRADE_INVENTORY_SIZE = 4;


    // Passive ae drain
    private static final double IDLE_POWER_DRAIN = 0.7;

    private EntityPlayer player;

    private static final RedstoneMode DEFAULT_REDSTONE_MODE = RedstoneMode.IGNORE;

    private static final String NBT_KEY_REDSTONE_MODE = "redstoneMode", NBT_KEY_FILTER_NUMBER = "EnergyFilter#",
            NBT_KEY_UPGRADE_INV = "upgradeInventory";

    private boolean lastRedstone;

    private int[] availableFilterSlots = { AIOPart.BASE_SLOT_INDEX };

    // List of all container listeners
    private List<ContainerPartEnergyIOBus> listeners = new ArrayList<ContainerPartEnergyIOBus>();

    // Current mode
    private RedstoneMode redstoneMode = AIOPart.DEFAULT_REDSTONE_MODE;

    // Machine source of this machine
    protected MachineSource asMachineSource;

    protected List<LiquidAIEnergy> filteredEnergies = new ArrayList<LiquidAIEnergy>( AIOPart.MAX_FILTER_SIZE );

    protected TileEntity adjacentEnergyStorage;

    protected byte filterSize;
    protected byte speedState;

    protected byte upgradeSpeedCount = 0;

    protected boolean redstoneControlled;
    private boolean updateRequested;

    public AIOPart(final PartEnum associatedPart, final SecurityPermissions... interactionPermissions )
    {
        super( associatedPart, interactionPermissions );

        // Change transfer
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
        return BASE_ENERGY_TRANSFER + ( this.upgradeSpeedCount * TRANSFER_PER_UPGRADE);
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
    private AIGridNodeInventory upgradeInventory = new AIGridNodeInventory("ME Energy Export/Import Bus", 4,
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
        if(player != null) {
            int i = 0;
            for (LiquidAIEnergy energy : this.filteredEnergies) {
                TileEntity host = this.getHostTile();
                NetworkHandler.sendTo(new PacketServerFilter(energy, i, host.getPos().getX()
                        , host.getPos().getY(), host.getPos().getZ(), this.getSide().getFacing(), host.getWorld()), (EntityPlayerMP) this.player);
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
    public AIGridNodeInventory getUpgradeInventory() {
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
    public TickingRequest getTickingRequest(final IGridNode arg0 )
    {
        return new TickingRequest( MINIMUM_TICKS_PER_OPERATION, MAXIMUM_TICKS_PER_OPERATION, false, false );
    }

    @Override
    public boolean onActivate(final EntityPlayer player, EnumHand hand, final Vec3d position )
    {
        super.onActivate( player, hand, position );
        if(getLogicalSide() == SERVER) {
            if (!player.isSneaking()) {
                this.updateUpgradeState();

                // Open gui trough handler
                // AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiIOPart, player, getSide(), getHostTile().getPos());

                // Make part update gui's coordinates
                this.updateRequested = true;

                // Update player
                this.player = player;
                return true;
            }
        }

        return false;
    }

    @Override
    public void onChangeInventory(final IInventory inv, final int slot, final InvOperation mc, final ItemStack removedStack,
                                  final ItemStack newStack )
    {

    }

    @Override
    public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos blockPos, BlockPos blockPos1) {
        // Ignored client side
        if( EffectiveSide.isClientSide() )
        {
            return;
        }

        // Set that we are not facing a container
        this.adjacentEnergyStorage = null;

        // Get the tile we are facing
        TileEntity tileEntity = this.getFacingTile();

        // Check not null
        if(tileEntity == null)
            return;

        // Iterate over all capabiltiy types
        for(EnumCapabilityType type : EnumCapabilityType.values) {
            // Iterate over all capabilities
            for(Capability capability : type.capabilities) {
                // Check if tile has one of type's capabilities
                if (tileEntity.hasCapability(capability, getSide().getFacing())) {
                    this.adjacentEnergyStorage = tileEntity;
                }
            }
        }

        // Is the bus pulse controlled?
        if( this.redstoneMode == RedstoneMode.SIGNAL_PULSE )
        {
            // Did the stateProp of the redstone change?
            if( this.isReceivingRedstonePower() != this.lastRedstone )
            {
                // Set the previous redstone stateProp
                this.lastRedstone = this.isReceivingRedstonePower();
            }
        }
    }

    // Client sided filter list sync
    @SideOnly(CLIENT)
    public void onReceiveFilterList( final List<LiquidAIEnergy> filteredEnergies )
    {
        this.filteredEnergies = filteredEnergies;
    }

    // Filter size sync
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

    public abstract TickRateModulation doWork(int toTransfer, IGridNode node);

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall )
    {
        if(updateRequested){
            Gui g = Minecraft.getMinecraft().currentScreen;
            if(g != null){
                NetworkHandler.sendTo(new PacketCoordinateInit(this),
                        (EntityPlayerMP) player);
                updateRequested = false;
            }
        }

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

            return doWork(transferAmount, node);
        }

        this.notifyListenersOfFilterEnergyChange();
        return TickRateModulation.IDLE;
    }


    @Override
    public void writeToNBT( final NBTTagCompound data, final PartItemStack saveType )
    {
        // Call super
        super.writeToNBT( data, saveType );

        if( ( saveType == PartItemStack.WORLD ) || ( saveType == PartItemStack.WRENCH ) )
        {
            // Counter
            int i = 0;

            // Write each filter
            for( LiquidAIEnergy energy : filteredEnergies) {
                if( energy != null )
                {
                    data.setString( NBT_KEY_FILTER_NUMBER + i, energy.getTag() );
                }
                i++;
            }

            if( saveType == PartItemStack.WORLD ) {
                // Write the redstone mode
                if (this.redstoneMode != DEFAULT_REDSTONE_MODE) {
                    data.setInteger(NBT_KEY_REDSTONE_MODE, this.redstoneMode.ordinal());
                }

                data.setTag("upgradeInventory", this.upgradeInventory.writeToNBT());

            }
        }
    }
}

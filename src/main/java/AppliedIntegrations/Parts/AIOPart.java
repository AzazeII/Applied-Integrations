package AppliedIntegrations.Parts;

import AppliedIntegrations.API.IInventoryHost;
import AppliedIntegrations.API.Storage.EnergyRepo;
import AppliedIntegrations.API.Storage.EnumCapabilityType;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Container.part.ContainerPartEnergyIOBus;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Network.Packets.PacketFilterServerToClient;
import AppliedIntegrations.Network.Packets.PacketFullSync;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.ChangeHandler;
import appeng.api.AEApi;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.definitions.IMaterials;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.PartItemStack;
import appeng.core.sync.GuiBridge;
import appeng.helpers.IPriorityHost;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;
import static appeng.api.util.AEPartLocation.INTERNAL;
import static net.minecraft.init.Items.AIR;
import static net.minecraftforge.fml.relauncher.Side.SERVER;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

/**
 * @Author Azazell
 */

public abstract class AIOPart
        extends AIPart
        implements IGridTickable, IEnergyMachine, IInventoryHost, IPriorityHost {
    /**
     * Constant fields
     */
    // How much energy can be transfered in second
    private final static int BASE_ENERGY_TRANSFER = 4000;

    // How much energy transfer one upgrade adds
    private final static int TRANSFER_PER_UPGRADE = 8090;

    // How much minimum ticks machine need to operate
    private final static int MINIMUM_TICKS_PER_OPERATION = 2;

    // How much max ticks machine needs to operate
    private final static int MAXIMUM_TICKS_PER_OPERATION = 40;

    // Maximum transfer per second
    private final static int MAXIMUM_TRANSFER_PER_SECOND = 36000;

    // Mininmum transfer per second
    private final static int MINIMUM_TRANSFER_PER_SECOND = 4000;

    // Current max transfer
    protected int maxTransfer;

    // Size of filter
    private final static int MAX_FILTER_SIZE = 9;

    private final static int BASE_SLOT_INDEX = 4;

    private final static int[] TIER2_INDEXS = { 0, 2, 6, 8 };

    private final static int[] TIER1_INDEXS = { 1, 3, 5, 7 };


    // Passive ae drain
    private static final double IDLE_POWER_DRAIN = 0.7;

    private EntityPlayer player;

    private static final RedstoneMode DEFAULT_REDSTONE_MODE = RedstoneMode.IGNORE;

    private static final String NBT_KEY_REDSTONE_MODE = "redstoneMode";
    private static final String NBT_KEY_FILTER_NUMBER = "EnergyFilter#";

    private boolean lastRedstone;

    private int[] availableFilterSlots = { AIOPart.BASE_SLOT_INDEX };

    // list of all container listeners
    private List<ContainerPartEnergyIOBus> listeners = new ArrayList<ContainerPartEnergyIOBus>();

    // Current mode
    private RedstoneMode redstoneMode = AIOPart.DEFAULT_REDSTONE_MODE;

    protected List<LiquidAIEnergy> filteredEnergies = new ArrayList<LiquidAIEnergy>( AIOPart.MAX_FILTER_SIZE );

    protected TileEntity adjacentEnergyStorage;

    private byte filterSize;
    private byte upgradeSpeedCount = 0;

    private boolean redstoneControlled;
    private boolean updateRequested;
    private List<ChangeHandler<LiquidAIEnergy>> filteredEnergiesChangeHandler = new ArrayList<>();

    private int priority = 0;

    public AIOPart(final PartEnum associatedPart, final SecurityPermissions... interactionPermissions )
    {
        super( associatedPart, interactionPermissions );

        // Change transfer
        maxTransfer = 5000*10*upgradeSpeedCount;

        // Pre-fill filtered energies
        for(int i = 0; i < MAX_FILTER_SIZE; i++) {
            // Fill filtered energies list
            filteredEnergies.add(i, null);

            // Fill handler list
            filteredEnergiesChangeHandler.add(new ChangeHandler<>());
        }
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

    protected int getTransferAmountPerSecond() {
        return BASE_ENERGY_TRANSFER + ( this.upgradeSpeedCount * TRANSFER_PER_UPGRADE);
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
        //=========+Reset+=========//
        // Set current filter size to zero
        this.filterSize = 0;

        // Trigger redstone control
        this.redstoneControlled = false;

        // Set speed to 0
        this.upgradeSpeedCount = 0;
        //=========+Reset+=========//

        // Iterate until i equal to stack size
        for (int i = 0; i < this.upgradeInventory.getSizeInventory(); i++) {

            // Get current stack from slot
            ItemStack currentStack = this.upgradeInventory.getStackInSlot(i);

            // Check not air
            if (currentStack.getItem() != AIR) {
                // Check if current stack is capacity card stack
                if (AEApi.instance().definitions().materials().cardCapacity().isSameAs(currentStack)) {
                    // Increase filter size
                    this.filterSize++;
                }

                // Check if current stack is redstone card stack
                if (AEApi.instance().definitions().materials().cardRedstone().isSameAs(currentStack)) {
                    // Trigger restone control
                    this.redstoneControlled = true;
                }

                // Check if current stack is speed card stack
                if (AEApi.instance().definitions().materials().cardSpeed().isSameAs(currentStack)) {
                    // Increase speed
                    this.upgradeSpeedCount++;
                }
            }
        }

        // Request full state update
        notifyListenersOfStateUpdate(filterSize, redstoneControlled, upgradeSpeedCount);
    }

    private void notifyListenersOfStateUpdate(byte filterSize, boolean redstoneControlled, byte upgradeSpeedCount) {
        if(player != null){
            NetworkHandler.sendTo(new PacketFullSync(filterSize, redstoneControlled, upgradeSpeedCount, this), (EntityPlayerMP) this.player);
        }
    }

    private void notifyListenersOfFilterEnergyChange(int i, LiquidAIEnergy energy) {
        if(player != null) {
            NetworkHandler.sendTo(new PacketFilterServerToClient(energy, i, this), (EntityPlayerMP) this.player);
        }
    }

    @Override
    public AIGridNodeInventory getUpgradeInventory() {
        return this.upgradeInventory;
    }

    public boolean addFilteredEnergyFromItemstack( final EntityPlayer player, final ItemStack itemStack ) {
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
                    if( !getWorld().isRemote )
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

    public void addListener( final ContainerPartEnergyIOBus container ) {
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
    public TickingRequest getTickingRequest(final IGridNode arg0 ) {
        return new TickingRequest( MINIMUM_TICKS_PER_OPERATION, MAXIMUM_TICKS_PER_OPERATION, false, false );
    }

    @Override
    public boolean onActivate(final EntityPlayer player, EnumHand hand, final Vec3d position ) {
        super.onActivate( player, hand, position );
        // Activation logic is server sided
        if(getLogicalSide() == SERVER) {
            if (!player.isSneaking()) {
                // Open gui trough handler
                AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiIOPart, player, getSide(), getHostTile().getPos());

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
    public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos blockPos, BlockPos blockPos1) {
        // Ignored client side
        if( getWorld().isRemote){
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

    @Override
    public void readFromNBT( final NBTTagCompound data ) {
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

    @Override
    public final void updateFilter(LiquidAIEnergy energy, int index) {
        // Set the filter
        this.filteredEnergies.set( index, energy );

        this.notifyListenersOfFilterEnergyChange(index, energy);
    }

    public abstract TickRateModulation doWork(int toTransfer, IGridNode node);

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall ) {
        if(updateRequested){
            Gui g = Minecraft.getMinecraft().currentScreen;
            if(g != null){
                NetworkHandler.sendTo(new PacketCoordinateInit(this),
                        (EntityPlayerMP) player);
            }
        }

        // Iterate until i equal to filter size
        for(int i = 0; i < MAX_FILTER_SIZE; i++){

            // Create effectively final i
            int finalI = i;

            // Check if energy changed
            filteredEnergiesChangeHandler.get(i).onChange(filteredEnergies.get(i), (energy -> {
                // Notify listener
                this.notifyListenersOfFilterEnergyChange(finalI, energy);
            }));

            // Check if update requested (player opened GUI)
            if(updateRequested){
                // Notify listener
                this.notifyListenersOfFilterEnergyChange(finalI, filteredEnergies.get(i));

            }
        }

        // Check if update was requested
        if(updateRequested)
            // Trigger request
            updateRequested = false;

        if( this.canDoWork() ) {
            // Calculate the amount to transfer per second
            int transferAmountPerSecond = this.getTransferAmountPerSecond();

            // Get transfer from ticks since last call, divided by one second multiplied by current transfer amount
            int transferAmount = (int)( transferAmountPerSecond * ( ticksSinceLastCall / 20.F ) );

            // Normalize transfer
            transferAmount = Math.min(transferAmount, MAXIMUM_TRANSFER_PER_SECOND);
            transferAmount = Math.max(transferAmount, MINIMUM_TRANSFER_PER_SECOND);

            return doWork(transferAmount, node);
        }

        return TickRateModulation.IDLE;
    }


    @Override
    public void writeToNBT( final NBTTagCompound data, final PartItemStack saveType ) {
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

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(int newValue) {
        this.priority = newValue;
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return getItemStack(PartItemStack.BREAK);
    }

    @Override
    public GuiBridge getGuiBridge() {
        return null;
    }
}

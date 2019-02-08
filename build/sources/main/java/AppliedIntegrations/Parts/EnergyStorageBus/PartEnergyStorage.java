package AppliedIntegrations.Parts.EnergyStorageBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import AppliedIntegrations.API.IEnergyDuality;
import AppliedIntegrations.API.IInventoryHost;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Network.Packets.PacketServerFilter;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerEnergyStorage;
import AppliedIntegrations.Gui.GuiEnergyStoragePart;
import AppliedIntegrations.Inventory.HandlerEnergyStorageBusBase;
import AppliedIntegrations.Inventory.HandlerEnergyStorageBusDuality;
import AppliedIntegrations.Parts.IAEAppEngInventory;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Parts.InvOperation;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.Utils.EffectiveSide;
import AppliedIntegrations.Utils.AIGridNodeInventory;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkStorageEvent;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.*;
import appeng.api.util.AECableType;
import appeng.core.sync.GuiBridge;
import appeng.helpers.IPriorityHost;
import cofh.redstoneflux.impl.EnergyStorage;
import ic2.api.energy.tile.IEnergyEmitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static AppliedIntegrations.API.LiquidAIEnergy.RF;
import static AppliedIntegrations.AppliedIntegrations.AI;
import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;

/**
 * @Author Azazell
 */
public class PartEnergyStorage
		extends AIPart
		implements IEnergyDuality,IGridTickable, ICellContainer, IEnergyMachine, IAEAppEngInventory, IPriorityHost, IInventoryHost {

	public static final int FILTER_SIZE = 18;

	/**
	 * How much AE power is required to keep the part active.
	 */
	private static final double IDLE_POWER_DRAIN = 1.0D;


	public boolean canTransfer = false;
	private int capacity=   1000000;
	private int maxTransfer = capacity;
	protected EnergyStorage storage = new EnergyStorage(capacity, maxTransfer);
	/**
	 * NBT Keys
	 */
	private static final String NBT_KEY_PRIORITY = "Priority", NBT_KEY_FILTER = "FilterEnergies#", NBT_KEY_UPGRADES = "UpgradeInventory";

	/**
	 * "Cell" handler for the storage bus.
	 */
	private final HandlerEnergyStorageBusBase handler = new HandlerEnergyStorageBusDuality( this );

	/**
	 * Filter list
	 */
	public final ArrayList<LiquidAIEnergy> filteredEnergies = new ArrayList<LiquidAIEnergy>( this.FILTER_SIZE );

	/**
	 * Upgrade inventory
	 */
	private final AIGridNodeInventory upgradeInventory = new AIGridNodeInventory("StorageBusUpgradeInv",5,1,this );

	/**
	 * Storage bus priority
	 */
	private int priority = 0;
	private TileEntity facingContainer;
	private boolean updateRequested;
	public Vector<ContainerEnergyStorage> listeners = new Vector<>();
	private EntityPlayer player;

	/**
	 * Creates the bus
	 */
	public PartEnergyStorage()
	{
		// Call super
		super( PartEnum.EnergyStorageBus, SecurityPermissions.EXTRACT, SecurityPermissions.INJECT );

		// Pre-fill the list with nulls
		for( int index = 0; index < this.FILTER_SIZE; index++ ) {
			this.filteredEnergies.add(null);
		}
	}

	/**
	 * Updates the handler on the inverted state.
	 */
	private void updateInverterState()
	{
		boolean inverted = AEApi.instance().definitions().materials().cardInverter().isSameAs( this.upgradeInventory.getStackInSlot( 0 ) );
		this.handler.setInverted( inverted );
	}

	/**
	 * Adds a new filter from the specified itemstack.
	 *
	 * @param player
	 * @param itemStack
	 * @return
	 */
	public boolean addFilteredEnergyFromItemstack( final EntityPlayer player, final ItemStack itemStack )
	{
		// Get the Energy of the item
		LiquidAIEnergy itemEnergy = Utils.getEnergyFromItemStack(itemStack);

		// Is there an Energy?
		if( itemEnergy != null )
		{
			// Are we already filtering this Energy?
			if( this.filteredEnergies.contains( itemEnergy ) )
			{
				return true;
			}

			// Add to the first open slot
			for( int index = 0; index < this.FILTER_SIZE; index++ )
			{
				// Is this space empty?
				if( this.filteredEnergies.get( index ) == null )
				{
					// Set the filter
					this.updateFilter( itemEnergy,index);

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Ignored
	 */
	@Override
	public void blinkCell( final int slot )
	{
		// Ignored
	}

	/**
	 * Extracts power from the network proportional to the specified Energy
	 * amount.
	 *
	 * @param EnergyAmount
	 * @param mode
	 * @return
	 */
	public boolean extractPowerForEnergyTransfer( final int EnergyAmount, final Actionable mode )
	{
		// Get the energy grid
		IEnergyGrid eGrid = this.getGridBlock().getEnergyGrid();

		// Ensure we have a grid
		if( eGrid == null )
		{
			return false;
		}

		// Calculate amount of power to take
		double powerDrain =  0.3 * EnergyAmount;

		// Extract
		return( eGrid.extractAEPower( powerDrain, mode, PowerMultiplier.CONFIG ) >= powerDrain );
	}

	/**
	 * Gets the 'cell' handler for the storage bus.
	 */
	@Override
	public List<IMEInventoryHandler> getCellArray( final IStorageChannel channel )
	{
		// Create a new list
		List<IMEInventoryHandler> list = new ArrayList<IMEInventoryHandler>();

		// Is this the energy channel?
		if( channel == getChannel() )
		{
			// Add our handler
			list.add( this.handler );
		}

		// Return the list
		return list;

	}

	/**
	 * Returns the client portion of the gui.
	 */
	@Override
	public Object getClientGuiElement( final EntityPlayer player ) {
		return new GuiEnergyStoragePart( (ContainerEnergyStorage)getServerGuiElement(player),this, player);
	}

	/**
	 * What do we drop when removed from the world.
	 */
	@Override
	public void getDrops( final List<ItemStack> drops, final boolean wrenched )
	{
		// Get the upgrade card
		ItemStack slotStack = this.upgradeInventory.getStackInSlot( 0 );

		// Is it not null?
		if( ( slotStack != null ) && ( slotStack.getCount() > 0 ) )
		{
			// Add to the drops
			drops.add( slotStack );
		}
	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {
		return 0;
	}

	/**
	 * Returns the Energy in the filter slot.
	 *
	 * @return
	 */
	@Nullable
	public LiquidAIEnergy getFilteredEnergy(final int slotIndex )
	{
		return this.filteredEnergies.get( slotIndex );
	}

	/**
	 * Determines how much power the part takes for just existing.
	 */
	@Override
	public double getIdlePowerUsage()
	{
		return this.IDLE_POWER_DRAIN;
	}

	/**
	 * Does not produce light.
	 */
	@Override
	public int getLightLevel()
	{
		return 0;
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	/**
	 * Gets the priority for this storage bus.
	 */
	@Override
	public int getPriority()
	{
		return this.priority;
	}

	/**
	 * Gets the server part of the gui.
	 */
	@Override
	public Object getServerGuiElement( final EntityPlayer player )
	{
		return new ContainerEnergyStorage( this, player );
	}

	/**
	 * Sets how often we would like ticks.
	 */
	@Override
	public TickingRequest getTickingRequest( final IGridNode node )
	{
		// We would like a tick ever 20 MC ticks
		return new TickingRequest( 20, 20, false, false );
	}

	/**
	 * Gets the inventory that holds our upgrades.
	 *
	 * @return
	 */
	public AIGridNodeInventory getUpgradeInventory()
	{
		return this.upgradeInventory;
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand hand, Vec3d position) {
		if (player.isSneaking()) {
			return false;
		}

		if(getLogicalSide().isServer()) {
			if (!this.getHostTile().getWorld().isRemote) {
				player.openGui(AppliedIntegrations.instance, 3, this.getHostTile().getWorld(),
						this.getHostTile().getPos().getX(), this.getHostTile().getPos().getY(), this.getHostTile().getPos().getZ());
				this.updateRequested = true;
				this.player = player;

				this.tickingRequest(getGridNode(), 20);
			}
		}
		return true;
	}
	/**
	 * Called when the upgrade inventory changes.
	 */
	@Override
	public void onChangeInventory( final IInventory inv, final int arg1, final InvOperation arg2, final ItemStack arg3, final ItemStack arg4 )
	{
		this.updateInverterState();
	}

	@MENetworkEventSubscribe
	public void updateChannels(MENetworkChannelsChanged channel) {
		IGridNode node = getGridNode();
		if (node != null) {
			boolean isNowActive = node.isActive();
			if (isNowActive != isActive()) {
				onNeighborChanged(null, getHostTile().getPos(), null);
				getHost().markForUpdate();
			}
		}
		if (node == null) {
			return;
		}
		IGrid grid = node.getGrid();
		if (grid == null) {
			return;
		}
		IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
		if (storageGrid == null) {
			return;
		}
			node.getGrid().postEvent(
					new MENetworkStorageEvent(storageGrid.getInventory(getChannel()),
							getChannel()));
			node.getGrid().postEvent(new MENetworkCellArrayUpdate());
	}




	/**
	 * /** Reads the part data from NBT
	 */
	@Override
	public void readFromNBT( final NBTTagCompound data )
	{
		// Call super
		super.readFromNBT( data );

		// Read the priority
		if( data.hasKey( this.NBT_KEY_PRIORITY ) )
		{
			this.priority = data.getInteger( this.NBT_KEY_PRIORITY );
		}

		// Read the filter list
		for( int index = 0; index < this.FILTER_SIZE; index++ )
		{
			if( data.hasKey( this.NBT_KEY_FILTER + index ) )
			{
				this.filteredEnergies.set( index, LiquidAIEnergy.energies.get( data.getString( this.NBT_KEY_FILTER + index ) ) );
			}
			else
			{
				this.filteredEnergies.set( index, null );
			}
		}




		// Update the handler filter list
		this.handler.setPrioritizedEnergies( this.filteredEnergies);
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox( 3, 3, 15, 13, 13, 16 );
		bch.addBox( 2, 2, 14, 14, 14, 15 );
		bch.addBox( 5, 5, 12, 11, 11, 14 );
	}

	@Override
	public void saveChanges()
	{
		this.markForSave();
	}

	/**
	 * Sets one of the filters.
	 */
	@Override
	public void updateFilter( final LiquidAIEnergy Energy, final int index )
	{

		// Update filtered energies
		this.filteredEnergies.set( index, Energy );

		// Update the handler
		this.handler.setPrioritizedEnergies( this.filteredEnergies);

		// Mark for save
		this.markForSave();
	}

	@Override
	public void setPriority( final int priority ) {
		this.priority = priority;
	}

	@Override
	public ItemStack getItemStackRepresentation() {
		return null;
	}

	@Override
	public GuiBridge getGuiBridge() {
		return null;
	}

	/**
	 * Called periodically by AE2. Passes the tick to the handler.
	 */
	@Override
	public TickRateModulation tickingRequest( final IGridNode node, final int TicksSinceLastCall )
	{
		// Update all energies in GUI
		for(int i = 0; i < this.FILTER_SIZE; i++){
			// Request gui update for all energies, and for null
			this.requestGuiUpdate(filteredEnergies.get(i), i);
		}
		// Update the handler.
		this.handler.tickingRequest( node, TicksSinceLastCall );
		// Simulate neighborChange
		this.onNeighborChanged(null, getHostTile().getPos(), null);

		// If update requested
		if (updateRequested) {
			// Then update gui, using packet system
			Gui g = Minecraft.getMinecraft().currentScreen;
			if (g instanceof GuiEnergyStoragePart) {
				// send packet
				NetworkHandler.sendTo(new PacketCoordinateInit(getX(), getY(), getZ(), getHostTile().getWorld(), getSide().getFacing()),
						(EntityPlayerMP) this.player);
				updateRequested = false;
			}
		}

		// Keep chugging along
		return TickRateModulation.SAME;
	}

	private void requestGuiUpdate(LiquidAIEnergy energy, int index) {
		if(player != null)
			NetworkHandler.sendTo(new PacketServerFilter(energy, index, getX(), getY(), getZ(), getSide().getFacing(), getHostTile().getWorld()), (EntityPlayerMP) this.player);
	}

	/**
	 * Writes the storage busses state to NBT.
	 */
	@Override
	public void writeToNBT( final NBTTagCompound data, final PartItemStack saveType )
	{
		// Call super
		super.writeToNBT( data, saveType );

		// Only write NBT data if saving, or wrenched.
		if( ( saveType != PartItemStack.WORLD ) && ( saveType != PartItemStack.WRENCH ) )
		{
			return;
		}

		// Write the filters
		boolean hasFilters = false;
		for( int index = 0; index < this.FILTER_SIZE; index++ )
		{
			LiquidAIEnergy Energy = this.filteredEnergies.get( index );

			if( Energy != null )
			{
				data.setString( this.NBT_KEY_FILTER + index, Energy.getTag() );
				hasFilters = true;
			}
		}

		// Only save the rest if filters are set, or world save
		if( hasFilters || ( saveType == PartItemStack.WORLD ) ) {
			// Write the priority
			if (this.priority != 0) {
				data.setInteger(this.NBT_KEY_PRIORITY, this.priority);
			}


		}


	}

	@Override
	public ResourceLocation[] getModels() {
		return new ResourceLocation[0];
	}


	@Override
	public void onInventoryChanged() {

	}

	@Override
	public double injectAEPower(double amt, Actionable mode) {
		return 0;
	}

	@Override
	public double getAEMaxPower() {
		return 0;
	}

	@Override
	public double getAECurrentPower() {
		return 0;
	}

	@Override
	public boolean isAEPublicPowerStorage() {
		return false;
	}

	@Override
	public AccessRestriction getPowerFlow() {
		return null;
	}

	@Override
	public double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
		return 0;
	}

	@Override
	public int receiveEnergy(EnumFacing p0, int p1, boolean p2) {
		return 0;
	}

	@Override
	public int getEnergyStored(EnumFacing p0) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing p0) {
		return 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return false;
	}

	@Override
	public double getDemandedEnergy() {
		return 0;
	}

	@Override
	public int getSinkTier() {
		return 0;
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		return 0;
	}

	@Override
	public double acceptEnergy(EnumFacing enumFacing, double v, boolean b) {
		return 0;
	}

	@Override
	public boolean canReceiveEnergy(EnumFacing side) {
		return true;
	}


	public void saveContainer(TileEntity tl) {
		this.facingContainer = tl;
	}
	public TileEntity getFacingContainer(){
		return this.facingContainer;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {

	}

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		return false;
	}

	@Override
	public int extractEnergy(EnumFacing enumFacing, int i, boolean b) {
		return 0;
	}
}


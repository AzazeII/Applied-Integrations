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
import AppliedIntegrations.Render.TextureManager;
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
import appeng.api.parts.IPartRenderHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.*;
import appeng.helpers.IPriorityHost;
import cofh.api.energy.EnergyStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nullable;

import static AppliedIntegrations.API.LiquidAIEnergy.RF;
import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;

/**
 * @Author Azazell
 */
public class PartEnergyStorage
		extends AIPart
		implements IEnergyDuality,IGridTickable, ICellContainer, IEnergyMachine, IAEAppEngInventory, IPriorityHost, IInventoryHost {

	public static final int FILTER_SIZE = 9;

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
	private final ArrayList<LiquidAIEnergy> filteredEnergies = new ArrayList<LiquidAIEnergy>( this.FILTER_SIZE );

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

	/**
	 * Creates the bus
	 */
	public PartEnergyStorage()
	{
		// Call super
		super( PartEnum.EnergyStorageBus, SecurityPermissions.EXTRACT, SecurityPermissions.INJECT );

		// Pre-fill the list with nulls
		for( int index = 0; index < this.FILTER_SIZE; index++ )
		{
			this.filteredEnergies.add( null );
		}
		this.filteredEnergies.add(RF);
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
	 * How far out from the cable bus to draw the cable graphic.
	 */
	@Override
	public int cableConnectionRenderTo()
	{
		return 3;
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
	@Override
	public IIcon getBreakingTexture()
	{
		return TextureManager.ENERGY_STORAGE_BUS.getTextures()[0];
	}

	/**
	 * Gets the 'cell' handler for the storage bus.
	 */
	@Override
	public List<IMEInventoryHandler> getCellArray( final StorageChannel channel )
	{
		// Create a new list
		List<IMEInventoryHandler> list = new ArrayList<IMEInventoryHandler>();

		// Is this the fluid channel?
		if( channel == StorageChannel.FLUIDS )
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
		if( ( slotStack != null ) && ( slotStack.stackSize > 0 ) )
		{
			// Add to the drops
			drops.add( slotStack );
		}
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
	public boolean onActivate(EntityPlayer player, Vec3 position) {
		if (player.isSneaking()) {
			return false;
		}

		if(getLogicalSide().isServer()) {
			if (!this.getHostTile().getWorldObj().isRemote) {
				player.openGui(AppliedIntegrations.instance, 3, this.getHostTile().getWorldObj(),
						this.getHostTile().xCoord, this.getHostTile().yCoord, this.getHostTile().zCoord);
				this.updateRequested = true;
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
				onNeighborChanged();
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
					new MENetworkStorageEvent(storageGrid.getFluidInventory(),
							StorageChannel.FLUIDS));
			node.getGrid().postEvent(new MENetworkCellArrayUpdate());
	}
	/**
	 * /** Updates the grid and handler that a neighbor has changed.
	 */
	@Override
	public void onNeighborChanged()
	{
		// Send grid update event on server side
		if( EffectiveSide.isServerSide() && this.isActive() )
		{
			// Update the handler
			if( this.handler.onNeighborChange() )
			{
				// Send the update event
				this.postGridUpdateEvent();
			}
		}
	}

	/**
	 * Notifies the grid that the storage bus contents have changed.
	 */
	public void postGridUpdateEvent()
	{
		// Does the storage bus have a grid node?
		if( this.getActionableNode() != null )
		{
			// Get the grid.
			IGrid grid = this.getActionableNode().getGrid();

			// Does the grid node have a grid?
			if( grid != null )
			{
				// Post an update to the grid

			}
		}
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
	@SideOnly( Side.CLIENT )
	public void renderInventory( final IPartRenderHelper rh, final RenderBlocks renderer )
	{
		IIcon side = TextureManager.ENERGY_STORAGE_BUS.getTextures()[2];
		IIcon back = TextureManager.BUS_BACK.getTexture();
		rh.setTexture( side,side, back, TextureManager.ENERGY_STORAGE_BUS.getTexture(),side, side );

		rh.setBounds( 3, 3, 15, 13, 13, 16 );
		rh.renderInventoryBox( renderer );

		rh.setBounds( 2, 2, 14, 14, 14, 15 );
		rh.renderInventoryBox( renderer );

		rh.setBounds( 5, 5, 12, 11, 11, 14 );
		rh.renderInventoryBox( renderer );
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void renderStatic( final int x, final int y, final int z, final IPartRenderHelper rh, final RenderBlocks renderer )
	{
		IIcon side = TextureManager.ENERGY_STORAGE_BUS.getTextures()[2];
		IIcon back = TextureManager.BUS_BACK.getTexture();
		IIcon aeSide = TextureManager.BUS_AESIDEDBACK.getTexture();;
		// Rendering main part
		rh.setTexture( side, side, back, TextureManager.ENERGY_STORAGE_BUS.getTexture(), side, side );
		rh.setBounds( 3, 3, 15, 13, 13, 16 );
		rh.renderBlock( x, y, z, renderer );
		rh.setBounds( 2, 2, 14, 14, 14, 15 );
		rh.renderBlock( x, y, z, renderer );
		rh.setBounds( 5, 5, 12, 11, 11, 13 );
		rh.renderBlock( x, y, z, renderer );

			rh.setBounds(5, 5, 13, 11, 11, 14);
			rh.renderBlock(x, y, z, renderer);


	}

	@Override
	public void saveChanges()
	{
		this.markForSave();
	}

	/**
	 * Ensures the storage bus gets saved.
	 */
	@Override
	public void saveChanges( final IMEInventory inventory )
	{
		this.saveChanges();
	}

	/**
	 * Sets one of the filters.
	 */
	@Override
	public void updateFilter( final LiquidAIEnergy Energy, final int index )
	{
		this.filteredEnergies.set( index, Energy );

		// Is this server side?
		if( EffectiveSide.isServerSide() )
		{
			// Update the handler
			this.handler.setPrioritizedEnergies( this.filteredEnergies);

			// Update the grid
			this.postGridUpdateEvent();

			// Mark for save
			this.markForSave();
		}
	}

	@Override
	public void setPriority( final int priority )
	{
		this.priority = priority;
		this.postGridUpdateEvent();
	}

	/**
	 * Called periodically by AE2. Passes the tick to the handler.
	 */
	@Override
	public TickRateModulation tickingRequest( final IGridNode node, final int TicksSinceLastCall )
	{
		// Update the handler.
		this.handler.tickingRequest( node, TicksSinceLastCall );
		this.onNeighborChanged();

		if(updateRequested) {
			for (ContainerEnergyStorage storage : this.listeners) {
				Gui g = Minecraft.getMinecraft().currentScreen;
				if (g instanceof GuiEnergyStoragePart) {
					NetworkHandler.sendTo(new PacketCoordinateInit(getX(),getY(),getZ(),getHostTile().getWorldObj(),getSide()),
							(EntityPlayerMP)storage.player);
					updateRequested = false;
				}
			}
		}

		// Keep chugging along
		return TickRateModulation.SAME;
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
		if( ( saveType != PartItemStack.World ) && ( saveType != PartItemStack.Wrench ) )
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
		if( hasFilters || ( saveType == PartItemStack.World ) ) {
			// Write the priority
			if (this.priority != 0) {
				data.setInteger(this.NBT_KEY_PRIORITY, this.priority);
			}


		}


	}


	@Override
	public void onInventoryChanged() {

	}

	@Override
	public ArrayList<String> getMessages(World world, int i, int i1, int i2, int i3) {
		return null;
	}

	@Override
	public boolean addPower(int i, int i1, long l, ForgeDirection forgeDirection) {
		return false;
	}

	@Override
	public boolean canReadFrom(ForgeDirection forgeDirection) {
		return false;
	}

	@Override
	public boolean isReceiving() {
		return false;
	}

	@Override
	public int getMinTorque(int i) {
		return 0;
	}

	@Override
	public int getOmega() {
		return 0;
	}

	@Override
	public int getTorque() {
		return 0;
	}

	@Override
	public long getPower() {
		return 0;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getIORenderAlpha() {
		return 0;
	}

	@Override
	public void setIORenderAlpha(int i) {

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
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int receiveEnergy(ForgeDirection p0, int p1, boolean p2) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection p0) {
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection p0) {
		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
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
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		return 0;
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return false;
	}

	@Override
	public double getOfferedEnergy() {
		return 0;
	}

	@Override
	public void drawEnergy(double amount) {

	}

	@Override
	public int getSourceTier() {
		return 0;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return false;
	}

	@Override
	public double transferEnergyToAcceptor(ForgeDirection side, double amount) {
		return 0;
	}

	@Override
	public boolean canReceiveEnergy(ForgeDirection side) {
		return true;
	}

	@Override
	public double getEnergy() {
		return 0;
	}

	@Override
	public void setEnergy(double energy) {

	}

	@Override
	public double getMaxEnergy() {
		return 0;
	}

	public void saveContainer(TileEntity tl) {
		this.facingContainer = tl;
	}
	public TileEntity getFacingContainer(){
		return this.facingContainer;
	}
}


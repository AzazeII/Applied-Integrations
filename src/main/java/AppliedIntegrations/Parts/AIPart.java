package AppliedIntegrations.Parts;
import AppliedIntegrations.AIConfigOPT;
import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.Utils.AIUtils;
import AppliedIntegrations.Utils.EffectiveSide;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.AEPartGridBlock;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.*;
import appeng.api.storage.IMEMonitor;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.MachineSource;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author Azazell
 */
public abstract class AIPart
		implements IPart, IGridHost, IActionHost, IPowerChannelState
{
	// NBT Tag
	private final static String NBT_KEY_OWNER = "Owner";

	// Constant value; Brightness of terminal face
	protected final static int ACTIVE_FACE_BRIGHTNESS = 0xD000D0;

	// Constant value; Brightness of active terminal
	protected final static int ACTIVE_TERMINAL_LIGHT_LEVEL = 9;

	// Interaction permissions of this part
	private final SecurityPermissions[] interactionPermissions;

	// Host of this gridblock
	protected IPartHost host;

	// Tile representation of part
	protected TileEntity hostTile;

	// Side where part connected
	private AEPartLocation cableSide;

	// Does this machine has channel, and can work?
	private boolean isActive;

	// Does this machine has power?
	private boolean isPowered;

	// Player id of owner
	private int ownerID;

	// Item, that creates this part
	public final ItemStack associatedItem;

	// Node representation of part
	protected IGridNode node;

	// Grid block where part placed
	protected AEPartGridBlock gridBlock;

	protected int capacity = AIConfigOPT.interfaceMaxStorage;
	protected int maxTransfer = 500000;

	public int getX(){
		if(this.getHost()!=null)
			return this.getHost().getLocation().x;
		return 0;
	}
	public int getY(){
		if(this.getHost()!=null)
			return this.getHost().getLocation().y;
		return 0;
	}
	public int getZ(){
		if(this.getHost()!=null)
			return this.getHost().getLocation().z;
		return 0;
	}
	public AIPart(final PartEnum associatedPart, final SecurityPermissions ... interactionPermissions )
	{
		// Set the associated item
		this.associatedItem = associatedPart.getStack();

		// Set clearance
		if( ( interactionPermissions != null ) && ( interactionPermissions.length > 0 ) )
		{
			this.interactionPermissions = interactionPermissions;
		}
		else
		{
			this.interactionPermissions = null;
		}
			// Create the grid block
		if(EffectiveSide.isServerSide()) {
			this.gridBlock = new AEPartGridBlock(this);
		}else{
			this.gridBlock = null;
		}
	}

	private void updateStatus()
	{
		// Ignored client side
		if( EffectiveSide.isClientSide() )
		{
			return;
		}

		// Do we have a node?
		if( this.node != null )
		{
			// Get the active stateProp
			boolean currentlyActive = this.node.isActive();

			// Has that stateProp changed?
			if( currentlyActive != this.isActive )
			{
				// Set our active stateProp
				this.isActive = currentlyActive;

				// Mark the host for an update
				this.host.markForUpdate();
			}
		}

		// Fire the neighbor changed event
		this.onNeighborChanged(null, null, null);
	}
	protected abstract AIGridNodeInventory getUpgradeInventory();

	protected boolean doesPlayerHavePermission( final EntityPlayer player, final SecurityPermissions permission )
	{
		if( EffectiveSide.isClientSide() )
		{
			return false;
		}

		// Get the security grid
		ISecurityGrid sGrid = this.gridBlock.getSecurityGrid();

		// Did we get the grid?
		if( sGrid == null )
		{
			// No security grid to check against.
			return false;
		}

		// Return the permission
		return sGrid.hasPermission( player, permission );
	}



	protected boolean doesPlayerHavePermission( final int playerID, final SecurityPermissions permission )
	{
		if( EffectiveSide.isClientSide() )
		{
			return false;
		}

		// Get the security grid
		ISecurityGrid sGrid = this.gridBlock.getSecurityGrid();

		// Did we get the grid?
		if( sGrid == null )
		{
			// No security grid to check against.
			return false;
		}

		// Return the permission
		return sGrid.hasPermission( playerID, permission );
	}


	protected TileEntity getFacingTile()
	{
		if( this.hostTile == null )
		{
			return null;
		}

		// Get the world
		World world = this.hostTile.getWorld();

		// Get our location
		int x = this.hostTile.getPos().getX();
		int y = this.hostTile.getPos().getY();
		int z = this.hostTile.getPos().getZ();

		// Get the tile entity we are facing
		return world.getTileEntity( new BlockPos(x + this.cableSide.xOffset, y + this.cableSide.yOffset, z + this.cableSide.zOffset ));
	}



	@Override
	public void addToWorld()
	{
		// Ignored on client side
		if( EffectiveSide.isClientSide() )
		{
			return;
		}
		this.gridBlock = new AEPartGridBlock(this);
		this.node = AEApi.instance().grid().createGridNode( this.gridBlock );

		// Set the player id
		this.node.setPlayerID( this.ownerID );

		// Update stateProp
		if( ( this.hostTile != null ) && ( this.host != null ) && ( this.hostTile.getWorld() != null ) )
		{
			try
			{
				this.node.updateState();
			}
			catch( Exception e )
			{
				AILog.error( e, "Machine (%s) was unable to update it's node. The part may not function correctly",
						this.associatedItem.getDisplayName() );
			}
		}

		// Update the part
		this.updateStatus();
	}

	@Override
	public boolean canBePlacedOn( final BusSupport type )
	{
		// Can only be placed on normal cable
		return type == BusSupport.CABLE;
	}

	@Override
	public boolean canConnectRedstone()
	{
		return false;
	}

	@Override
	public IGridNode getActionableNode()
	{
		return this.node;
	}

	@Override
	public abstract void getBoxes( IPartCollisionHelper helper );

	@Override
	public AECableType getCableConnectionType( final AEPartLocation dir )
	{
		return AECableType.SMART;
	}

	@Override
	public void getDrops( final List<ItemStack> drops, final boolean wrenched )
	{
	}

	@Override
	public final IGridNode getExternalFacingNode()
	{
		return null;
	}

	public AEPartGridBlock getGridBlock()
	{
		return this.gridBlock;
	}

	@Override
	public IGridNode getGridNode()
	{
		return this.node;
	}

	@Override
	public IGridNode getGridNode( final AEPartLocation direction )
	{
		return getGridNode();
	}

	public final IPartHost getHost()
	{
		return this.host;
	}


	public final TileEntity getHostTile()
	{
		return this.hostTile;
	}

	public abstract double getIdlePowerUsage();

	@Override
	public ItemStack getItemStack( final PartItemStack type )
	{
		// Get the itemstack
		ItemStack itemStack = this.associatedItem.copy();

		// Save NBT data if the part was wrenched
		if( type == PartItemStack.WRENCH )
		{
			// Create the item tag
			NBTTagCompound itemNBT = new NBTTagCompound();

			// Write the data
			this.writeToNBT( itemNBT, PartItemStack.WRENCH );

			// Set the tag
			if( !itemNBT.hasNoTags() )
			{
				itemStack.setTagCompound( itemNBT );
			}
		}

		return itemStack;
	}


	@Override
	public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos blockPos, BlockPos blockPos1) {

	}

	public final DimensionalCoord getLocation()
	{
		return new DimensionalCoord( this.hostTile.getWorld(), this.hostTile.getPos().getX(), this.hostTile.getPos().getY(), this.hostTile.getPos().getZ() );
	}

	public AEPartLocation getSide()
	{
		return this.cableSide;
	}

	public String getUnlocalizedName()
	{
		return this.associatedItem.getUnlocalizedName() + ".name";
	}

	@Override
	public boolean isActive()
	{
		// Are we server side?
		if( EffectiveSide.isServerSide() )
		{
			// Do we have a node?
			if( this.node != null )
			{
				// Get it's activity
				this.isActive = this.node.isActive();
			}
			else
			{
				this.isActive = false;
			}
		}

		return this.isActive;
	}

	@Override
	public boolean isLadder( final EntityLivingBase entity )
	{
		return false;
	}

	@Override
	public abstract int getLightLevel();
	public final boolean isPartUseableByPlayer( final EntityPlayer player )
	{
		if( EffectiveSide.isClientSide() )
		{
			return false;
		}

		// Null check host
		if( ( this.hostTile == null ) || ( this.host == null ) )
		{
			return false;
		}

		// Does the host still exist in the world and the player in range of it?
		if( !AIUtils.canPlayerInteractWith( player, this.hostTile ) )
		{
			return false;
		}

		// Is the part still attached?
		if( this.host.getPart( this.cableSide ) != this )
		{
			return false;
		}

		// Are there any permissions to check?
		if( this.interactionPermissions != null )
		{
			// Get the security grid
			ISecurityGrid sGrid = this.gridBlock.getSecurityGrid();
			if( sGrid == null )
			{
				// Security grid was unaccessible.
				return false;
			}

			// Check each permission
			for( SecurityPermissions perm : this.interactionPermissions )
			{
				if( !sGrid.hasPermission( player, perm ) )
				{
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean isPowered()
	{
		try
		{
			// Server side?
			if( EffectiveSide.isServerSide() && ( this.gridBlock != null ) )
			{
				// Get the energy grid
				IEnergyGrid eGrid = this.gridBlock.getEnergyGrid();
				if( eGrid != null )
				{
					this.isPowered = eGrid.isNetworkPowered();
				}
				else
				{
					this.isPowered = false;
				}
			}
		}
		catch( Exception e )
		{
			// Network unavailable, return cached value.
		}

		return this.isPowered;
	}




	public boolean isReceivingRedstonePower()
	{
		if( this.host != null )
		{
			// Get redstone stateProp
			return this.host.hasRedstone( this.cableSide );
		}
		return false;
	}

	@Override
	public boolean isSolid()
	{
		return false;
	}


	public final void markForSave()
	{
		// Ensure there is a host
		if( this.host != null )
		{
			// Mark
			this.host.markForSave();
		}
	}

	public final void markForUpdate()
	{
		if( this.host != null )
		{
			this.host.markForUpdate();
		}
	}
	public List<String> getWailaBodey(NBTTagCompound tag, List<String> oldList) {
		return oldList;
	}

	public NBTTagCompound getWailaTag(NBTTagCompound tag) {
		return tag;
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Is the player sneaking?
		if( player.isSneaking() )
		{
			return false;
		}

		// Is this server side?
		if( EffectiveSide.isServerSide() )
		{
			// Launch the gui
			AppliedIntegrations.launchGui( this, player, this.hostTile.getWorld(), this.hostTile.getPos().getX(), this.hostTile.getPos().getY(), this.hostTile.getPos().getZ() );
		}
		return true;
	}

	@Override
	public void onPlacement(EntityPlayer player, EnumHand hand, ItemStack stack, AEPartLocation side) {
		// Set the owner
		this.ownerID = AEApi.instance().registries().players().getID( player.getGameProfile() );
	}

	@Override
	public boolean onShiftActivate(EntityPlayer entityPlayer, EnumHand enumHand, Vec3d vec3d) {
		return false;
	}

	@Override
	public void randomDisplayTick(World world, BlockPos blockPos, Random random) {

	}

	@Override
	public void readFromNBT( final NBTTagCompound data )
	{
		// Read the owner
		if( data.hasKey( AIPart.NBT_KEY_OWNER ) )
		{
			this.ownerID = data.getInteger( AIPart.NBT_KEY_OWNER );
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean readFromStream( final ByteBuf stream ) throws IOException
	{
		// Cache old values
		boolean oldActive = this.isActive;
		boolean oldPowered = this.isPowered;

		// Read the new values
		this.isActive = stream.readBoolean();
		this.isPowered = stream.readBoolean();

		// Redraw if they don't match.
		return( ( oldActive != this.isActive ) || ( oldPowered != this.isPowered ) );
	}

	@Override
	public void removeFromWorld()
	{
		if( this.node != null )
		{
			this.node.destroy();
		}
	}

	@Override
	public int isProvidingStrongPower()
	{
		return 0;
	}

	@Override
	public int isProvidingWeakPower()
	{
		return 0;
	}
	@Override
	public boolean requireDynamicRender()
	{
		return false;
	}

	@Override
	public void securityBreak()
	{
		List<ItemStack> drops = new ArrayList<ItemStack>();

		// Get this item
		drops.add( this.getItemStack( PartItemStack.BREAK ) );

		// Get the drops for this part
		this.getDrops( drops, false );



		// Remove the part
		this.host.removePart( this.cableSide, false );

	}

	@Override
	public final void setPartHostInfo( final AEPartLocation side, final IPartHost host, final TileEntity tile )
	{
		this.cableSide = side;
		this.host = host;
		this.hostTile = tile;

	}

	@MENetworkEventSubscribe
	public final void setPower( final MENetworkPowerStatusChange event )
	{
		this.updateStatus();
	}

	public void setupPartFromItem( final ItemStack itemPart )
	{
		if( itemPart.hasTagCompound() )
		{
			this.readFromNBT( itemPart.getTagCompound() );
		}
	}

	@MENetworkEventSubscribe
	public void updateChannels( final MENetworkChannelsChanged event )
	{
		this.updateStatus();
	}

	@Override
	public void writeToNBT( final NBTTagCompound data )
	{
		// Assume world saving.
		this.writeToNBT( data, PartItemStack.WORLD );
	}

	public void writeToNBT( final NBTTagCompound data, final PartItemStack saveType )
	{
		if( saveType == PartItemStack.WORLD )
		{
			// Set the owner ID
			data.setInteger( AIPart.NBT_KEY_OWNER, this.ownerID );
		}

	}

	@Override
	public void writeToStream( final ByteBuf stream ) throws IOException
	{
		// Write active
		stream.writeBoolean( this.isActive() );

		// Write powered
		stream.writeBoolean( this.isPowered() );
	}

	//*---------*Storage features*---------*//
	public IEnergyTunnel getChannel(){
		return AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class);
	}

    public IMEMonitor<IAEEnergyStack> getEnergyProvidingInventory() {
		IGridNode n = getGridNode();
		if (n == null)
			return null;
		IGrid g = n.getGrid();
		if (g == null)
			return null;
		IStorageGrid storage = g.getCache(IStorageGrid.class);
		if (storage == null)
			return null;
		IMEMonitor<IAEEnergyStack> energyStorage = storage.getInventory(this.getChannel());
		if (energyStorage == null)
			return null;
		return energyStorage;
    }

	/**
	 * @param resource
	 * 	Resource to be extracted
	 * @param actionable
	 * 	Simulate of Modulate?
	 * @return
	 * 	amount extracted
	 */
	public int ExtractEnergy(EnergyStack resource, Actionable actionable) {
		// Extract energy from MEInventory
		IAEEnergyStack extracted = getEnergyProvidingInventory().extractItems(
				AEEnergyStack.fromStack(resource), actionable, new MachineSource(this));

		if (extracted == null)
			return 0;
		return (int)(extracted.getStackSize());
	}

	/**
	 * @param resource
	 * 	Resource to be injected
	 * @param actionable
	 * 	Simulate or modulate?
	 * @return
	 *  amount not added
	 */
	public int InjectEnergy(EnergyStack resource, Actionable actionable) {
		// Insert energy to MEInventory
		IAEEnergyStack notInjected = getEnergyProvidingInventory().injectItems(
				AEEnergyStack.fromStack(resource), actionable, new MachineSource(this));

		// Check for null result
		if (notInjected == null)
			// Return original amount
			return (int) resource.amount;
		// Return original amount - not injected
		return (int) (resource.amount - notInjected.getStackSize());
	}
}
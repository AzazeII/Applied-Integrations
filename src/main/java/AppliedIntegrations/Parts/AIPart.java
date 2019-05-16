package AppliedIntegrations.Parts;


import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.grid.AEEnergyStack;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.*;
import appeng.api.storage.IMEMonitor;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

/**
 * @Author Azazell
 */
public abstract class AIPart implements IPart, IGridHost, IActionHost, IPowerChannelState, ISyncHost, IGridProxyable {
	// Constant value; Brightness of active terminal
	protected final static int ACTIVE_TERMINAL_LIGHT_LEVEL = 9;

	// NBT Tag
	private final static String NBT_KEY_OWNER = "Owner";

	// Item, that creates this host
	public final ItemStack associatedItem;

	// Host of this gridblock
	protected IPartHost host;

	// tile representation of host
	protected TileEntity hostTile;

	// Node representation of host
	protected IGridNode node;

	// Side where host connected
	private AEPartLocation cableSide;

	// Does this machine has channel, and can work?
	private boolean isActive;

	// Does this machine has power?
	private boolean isPowered;

	// Player id of owner
	private int ownerID;

	// grid block where host placed
	private AENetworkProxy proxy;

	public AIPart(final PartEnum associatedPart) {
		// Set the associated item
		this.associatedItem = associatedPart.getStack();

		// Update proxy
		this.proxy = new AENetworkProxy(this, "AIPart Proxy", associatedItem, true);

		// Configure proxy
		this.proxy.setValidSides(EnumSet.noneOf(EnumFacing.class));
		this.proxy.setFlags(GridFlags.REQUIRE_CHANNEL);
	}

	public int getX() {

		if (this.getHost() != null) {
			return this.getHost().getLocation().x;
		}
		return 0;
	}

	public final IPartHost getHost() {

		return this.host;
	}

	public int getY() {

		if (this.getHost() != null) {
			return this.getHost().getLocation().y;
		}
		return 0;
	}

	public int getZ() {

		if (this.getHost() != null) {
			return this.getHost().getLocation().z;
		}
		return 0;
	}

	protected abstract AIGridNodeInventory getUpgradeInventory();

	protected TileEntity getFacingTile() {

		if (this.hostTile == null) {
			return null;
		}

		// Get the world
		World world = this.hostTile.getWorld();

		// Get our location
		int x = this.hostTile.getPos().getX();
		int y = this.hostTile.getPos().getY();
		int z = this.hostTile.getPos().getZ();

		// Get the tile entity we are facing
		return world.getTileEntity(new BlockPos(x + this.cableSide.xOffset, y + this.cableSide.yOffset, z + this.cableSide.zOffset));
	}

	@Override
	public IGridNode getActionableNode() {

		return this.node;
	}

	@Override
	public abstract void getBoxes(IPartCollisionHelper helper);

	@Override
	public IGridNode getGridNode(final AEPartLocation direction) {

		return getGridNode();
	}

	@Override
	public AECableType getCableConnectionType(final AEPartLocation dir) {

		return AECableType.SMART;
	}

	@Override
	public void securityBreak() {

		List<ItemStack> drops = new ArrayList<ItemStack>();

		// Get this item
		drops.add(this.getItemStack(PartItemStack.BREAK));

		// Get the drops for this host
		this.getDrops(drops, false);


		// Remove the host
		this.host.removePart(this.cableSide, false);
	}

	@Override
	public ItemStack getItemStack(final PartItemStack type) {
		// Get the itemstack
		ItemStack itemStack = this.associatedItem.copy();

		// Save NBT data if the host was wrenched
		if (type == PartItemStack.WRENCH) {
			// Create the item tag
			NBTTagCompound itemNBT = new NBTTagCompound();

			// Write the data
			this.writeToNBT(itemNBT, PartItemStack.WRENCH);

			// Set the tag
			if (!itemNBT.hasNoTags()) {
				itemStack.setTagCompound(itemNBT);
			}
		}

		return itemStack;
	}

	@Override
	public boolean requireDynamicRender() {

		return false;
	}

	@Override
	public boolean isSolid() {

		return false;
	}

	@Override
	public boolean canConnectRedstone() {

		return false;
	}

	@Override
	public void writeToNBT(final NBTTagCompound data) {
		// Assume world saving.
		this.writeToNBT(data, PartItemStack.WORLD);
	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		// Read the owner
		if (data.hasKey(AIPart.NBT_KEY_OWNER)) {
			this.ownerID = data.getInteger(AIPart.NBT_KEY_OWNER);
		}
	}

	@Override
	public abstract int getLightLevel();

	@Override
	public boolean isLadder(final EntityLivingBase entity) {

		return false;
	}

	@Override
	public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos blockPos, BlockPos blockPos1) {

	}

	@Override
	public int isProvidingStrongPower() {

		return 0;
	}

	@Override
	public int isProvidingWeakPower() {

		return 0;
	}

	@Override
	public void writeToStream(final ByteBuf stream) throws IOException {
		// Write active
		stream.writeBoolean(this.isActive());

		// Write powered
		stream.writeBoolean(this.isPowered());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean readFromStream(final ByteBuf stream) throws IOException {
		// Cache old values
		boolean oldActive = this.isActive;
		boolean oldPowered = this.isPowered;

		// Read the new values
		this.isActive = stream.readBoolean();
		this.isPowered = stream.readBoolean();

		// Redraw if they don't match.
		return ((oldActive != this.isActive) || (oldPowered != this.isPowered));
	}

	@Override
	public IGridNode getGridNode() {

		return this.node;
	}

	@Override
	public void removeFromWorld() {

		if (this.node != null) {
			this.node.destroy();
		}
	}

	@Override
	public void addToWorld() {
		// Ignored on client side
		if (getWorld().isRemote) {
			return;
		}

		this.node = AEApi.instance().grid().createGridNode(this.proxy);

		// Set the player id
		this.node.setPlayerID(this.ownerID);

		// Update stateProp
		if ((this.hostTile != null) && (this.host != null) && (this.hostTile.getWorld() != null)) {
			try {
				this.node.updateState();
			} catch (Exception e) {
				AILog.error(e, "Machine (%s) was unable to update it's node. The host may not function correctly", this.associatedItem.getDisplayName());
			}
		}

		// Update the host
		this.updateStatus();
	}

	private void updateStatus() {
		// Ignored client side
		if (getWorld().isRemote) {
			return;
		}

		// Do we have a node?
		if (this.node != null) {
			// Get the active stateProp
			boolean currentlyActive = this.node.isActive();

			// Has that stateProp changed?
			if (currentlyActive != this.isActive) {
				// Set our active stateProp
				this.isActive = currentlyActive;

				// Mark the host for an update
				this.host.markForUpdate();
			}
		}

		// Fire the neighbor changed event
		this.onNeighborChanged(null, null, null);
	}

	public final TileEntity getHostTile() {

		return this.hostTile;
	}

	@Override
	public final IGridNode getExternalFacingNode() {

		return null;
	}

	@Override
	public final void setPartHostInfo(final AEPartLocation side, final IPartHost host, final TileEntity tile) {

		this.cableSide = side;
		this.host = host;
		this.hostTile = tile;
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Is the player sneaking?
		if (player.isSneaking()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean onShiftActivate(EntityPlayer entityPlayer, EnumHand enumHand, Vec3d vec3d) {

		return false;
	}

	@Override
	public void getDrops(final List<ItemStack> drops, final boolean wrenched) {

	}

	@Override
	public void randomDisplayTick(World world, BlockPos blockPos, Random random) {

	}

	@Override
	public void onPlacement(EntityPlayer player, EnumHand hand, ItemStack stack, AEPartLocation side) {
		// Set the owner
		this.ownerID = AEApi.instance().registries().players().getID(player.getGameProfile());
	}

	@Override
	public boolean canBePlacedOn(final BusSupport type) {
		// Can only be placed on normal cable
		return type == BusSupport.CABLE;
	}

	public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {

		if (saveType == PartItemStack.WORLD) {
			// Set the owner ID
			data.setInteger(AIPart.NBT_KEY_OWNER, this.ownerID);
		}
	}

	public abstract double getIdlePowerUsage();

	@Override
	public AENetworkProxy getProxy() {

		return proxy;
	}

	public final DimensionalCoord getLocation() {

		return new DimensionalCoord(this.hostTile.getWorld(), this.hostTile.getPos().getX(), this.hostTile.getPos().getY(), this.hostTile.getPos().getZ());
	}

	@Override
	public void gridChanged() {

	}

	@Override
	public boolean isPowered() {

		try {
			// Server side?
			if (!getWorld().isRemote && (this.proxy != null)) {
				// Get the energy grid
				IEnergyGrid eGrid = this.proxy.getEnergy();

				if (eGrid != null) {
					this.isPowered = eGrid.isNetworkPowered();
				} else {
					this.isPowered = false;
				}
			}
		} catch (Exception e) {
			// Network unavailable, return cached value.
		}

		return this.isPowered;
	}

	@Override
	public boolean isActive() {
		// Are we server side?
		if (!getWorld().isRemote) {
			// Do we have a node?
			if (this.node != null) {
				// Get it's activity
				this.isActive = this.node.isActive();
			} else {
				this.isActive = false;
			}
		}

		return this.isActive;
	}

	public boolean isReceivingRedstonePower() {
		// Check host not null
		if (this.host != null) {
			// Get redstone stateProp
			return this.host.hasRedstone(this.cableSide);
		}

		return false;
	}

	public final void markForSave() {
		// Ensure there is a host
		if (this.host != null) {
			// Mark
			this.host.markForSave();
		}
	}

	public final void markForUpdate() {

		if (this.host != null) {
			this.host.markForUpdate();
		}
	}

	@MENetworkEventSubscribe
	public final void setPower(final MENetworkPowerStatusChange event) {

		this.updateStatus();
	}

	public void setupPartFromItem(final ItemStack itemPart) {

		if (itemPart.hasTagCompound()) {
			this.readFromNBT(itemPart.getTagCompound());
		}
	}

	@MENetworkEventSubscribe
	public void updateChannels(final MENetworkChannelsChanged event) {

		this.updateStatus();
	}

	@Override
	public BlockPos getPos() {

		return getHostTile().getPos();
	}

	@Override
	public World getWorld() {

		return getHostTile().getWorld();
	}

	@Override
	public AEPartLocation getSide() {

		return cableSide;
	}

	/**
	 * @param resource   Resource to be extracted
	 * @param actionable Simulate of Modulate?
	 * @return amount extracted
	 */
	public int ExtractEnergy(EnergyStack resource, Actionable actionable) {
		// Extract energy from MEInventory
		IAEEnergyStack extracted = getEnergyInventory().extractItems(AEEnergyStack.fromStack(resource), actionable, new MachineSource(this));

		if (extracted == null) {
			return 0;
		}
		return (int) (extracted.getStackSize());
	}

	public IMEMonitor<IAEEnergyStack> getEnergyInventory() {

		IGridNode n = getGridNode();
		if (n == null) {
			return null;
		}
		IGrid g = n.getGrid();

		IStorageGrid storage = g.getCache(IStorageGrid.class);

		IMEMonitor<IAEEnergyStack> energyStorage = storage.getInventory(this.getChannel());
		if (energyStorage == null) {
			return null;
		}
		return energyStorage;
	}

	//*---------*Storage features*---------*//
	public IEnergyStorageChannel getChannel() {

		return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
	}

	/**
	 * @param resource   Resource to be injected
	 * @param actionable Simulate or modulate?
	 * @return amount not added
	 */
	public int InjectEnergy(EnergyStack resource, Actionable actionable) {
		// Insert energy to MEInventory
		IAEEnergyStack notInjected = getEnergyInventory().injectItems(AEEnergyStack.fromStack(resource), actionable, new MachineSource(this));

		// Check for null result
		if (notInjected == null)
		// Return original amount
		{
			return (int) resource.amount;
		}
		// Return original amount - not injected
		return (int) (resource.amount - notInjected.getStackSize());
	}
}
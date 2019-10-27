package AppliedIntegrations.Parts;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.grid.AEEnergyStack;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.RedstoneMode;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.GridFlags;
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
import appeng.util.Platform;
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

import static appeng.api.config.RedstoneMode.LOW_SIGNAL;
import static appeng.api.config.RedstoneMode.SIGNAL_PULSE;

/**
 * @Author Azazell
 */
public abstract class AIPart implements IPart, IGridHost, IActionHost, IPowerChannelState, ISyncHost, IGridProxyable {
	// Constant value; Brightness of active terminal
	protected final static int ACTIVE_TERMINAL_LIGHT_LEVEL = 9;

	// NBT Tag
	private final static String NBT_KEY_OWNER = "Owner";

	// Item, that creates this host
	private final ItemStack associatedItem;

	// Host of this gridblock
	protected IPartHost host;

	// tile representation of host
	protected TileEntity hostTile;

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

	protected boolean canDoWork(RedstoneMode mode) {
		boolean canWork = true;

		if (mode == RedstoneMode.HIGH_SIGNAL) {
			canWork = this.isReceivingRedstonePower();
		}

		if (mode == LOW_SIGNAL) {
			canWork = !this.isReceivingRedstonePower();
		}

		if (mode == SIGNAL_PULSE) {
			canWork = false;
		}

		return canWork;
	}

	@Override
	public IGridNode getActionableNode() {
		return this.getProxy().getNode();
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
		return this.getProxy().getNode();
	}

	@Override
	public void removeFromWorld() {
		if (this.getProxy().getNode() != null) {
			this.getProxy().getNode().destroy();
		}
	}

	@Override
	public void addToWorld() {
		// Ignored on client side
		if (getHostWorld().isRemote) {
			return;
		}

		// Make proxy ready
		getProxy().onReady();

		// Set the player id
		getProxy().getNode().setPlayerID(this.ownerID);

		// Update stateProp
		if ((this.hostTile != null) && (this.host != null)) {
			getProxy().getNode().updateState();
		}

		// Update the host
		this.updateStatus();
	}

	private void updateStatus() {
		// Ignored client side
		if (getHostWorld().isRemote) {
			return;
		}

		// Do we have a node?
		if (this.getProxy().getNode() != null) {
			// Get the active stateProp
			boolean currentlyActive = this.getProxy().isActive();

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
	public void getDrops(final List<ItemStack> drops, final boolean wrenched) {}

	@Override
	public void randomDisplayTick(World world, BlockPos blockPos, Random random) { }

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
			if (Platform.isServer() && (this.proxy != null)) {
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
		if (Platform.isServer()) {
			// Do we have a node?
			if (this.getProxy().getNode() != null) {
				// Get it's activity
				this.isActive = this.getProxy().isActive();
			} else {
				this.isActive = false;
			}
		}

		return this.isActive;
	}

	public boolean isReceivingRedstonePower() {
		// Delegate call to host
		if (this.host != null) {
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

	@MENetworkEventSubscribe
	public void updateChannels(final MENetworkChannelsChanged event) {
		this.updateStatus();
	}

	@Override
	public BlockPos getHostPos() {
		return getHostTile().getPos();
	}

	@Override
	public World getHostWorld() {

		return getHostTile().getWorld();
	}

	@Override
	public AEPartLocation getHostSide() {

		return cableSide;
	}

	public IMEMonitor<IAEEnergyStack> getEnergyInventory() {
		IGridNode n = getGridNode();

		if (n == null) {
			return null;
		}

		IStorageGrid storage = n.getGrid().getCache(IStorageGrid.class);

		return storage.getInventory(this.getChannel());
	}

	//*---------*Storage features*---------*//
	public IEnergyStorageChannel getChannel() {

		return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
	}

	/**
	 * @param resource   Resource to be extracted
	 * @param actionable Simulate of Modulate?
	 * @return amount extracted
	 */
	public int extractEnergy(EnergyStack resource, Actionable actionable) {
		// Extract energy from MEInventory
		IAEEnergyStack extracted = getEnergyInventory().extractItems(AEEnergyStack.fromStack(resource), actionable, new MachineSource(this));

		// Check not null
		if (extracted == null) {
			return 0;
		}

		// Return amount extracted
		return (int) (extracted.getStackSize());
	}


	/**
	 * @param resource   Resource to be injected
	 * @param actionable Simulate or modulate?
	 * @return amount not added
	 */
	public int injectEnergy(EnergyStack resource, Actionable actionable) {
		// Insert energy to MEInventory
		IAEEnergyStack notInjected = getEnergyInventory().injectItems(AEEnergyStack.fromStack(resource), actionable, new MachineSource(this));

		// Check for null result
		if (notInjected == null) {
			// Return original amount
			return (int) resource.amount;
		}

		// Return original amount - not injected
		return (int) (resource.amount - notInjected.getStackSize());
	}
}
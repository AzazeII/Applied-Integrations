package AppliedIntegrations.tile;


import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.grid.AEEnergyStack;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkEvent;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.me.helpers.MachineSource;
import appeng.util.Platform;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * @Author Azazell
 */
@Optional.InterfaceList(value = { // ()____()
		@Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IKineticSource", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IHeatSource", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage", modid = "Mekanism", striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "Mekanism", striprefs = true),
		@Optional.Interface(iface = "cofh.api.energy.EnergyStorage", modid = "CoFHAPI", striprefs = true),
		@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = "CoFHAPI", striprefs = true),
		@Optional.Interface(iface = "cofh.api.energy.IEnergyHandler", modid = "CoFHAPI", striprefs = true),
		@Optional.Interface(iface = "Reika.RotaryCraft.api.Interfaces.Transducerable", modid = "RotaryCraft", striprefs = true),
		@Optional.Interface(iface = "Reika.RotaryCraft.api.Power.AdvancedShaftPowerReceiver", modid = "RotaryCraft", striprefs = true)})
public abstract class AITile extends TileEntity implements IActionHost, ITickable, IGridProxyable, ISyncHost {
	protected boolean loaded = false;

	private AENetworkProxy proxy = new AENetworkProxy(this, "AITileProxy", getMachineStack(), true);

	public void postCellInventoryEvent() {
		postGridEvent(new MENetworkCellArrayUpdate());
	}

	public void postGridEvent(MENetworkEvent event) {
		IGridNode node = getGridNode(AEPartLocation.INTERNAL);
		if (node != null) {
			IGrid grid = node.getGrid();
			postGridEvent(grid, event);
		}
	}

	public void postGridEvent(IGrid iGrid, MENetworkEvent event) {
		if (iGrid == null) {
			return;
		}

		iGrid.postEvent(event);
	}

	public void postCellInventoryEvent(IGrid iGrid) {
		if (iGrid == null) {
			return;
		}

		postGridEvent(iGrid, new MENetworkCellArrayUpdate());
	}

	public void createProxyNode() {
		this.proxy.setFlags(GridFlags.REQUIRE_CHANNEL);
		this.proxy.setColor(AEColor.TRANSPARENT);
		this.proxy.setValidSides(EnumSet.allOf(EnumFacing.class));
		this.proxy.setIdlePowerUsage(1);
		this.proxy.onReady();
		this.proxy.getNode().updateState();
	}

	public void destroyProxyNode() {
		getProxy().invalidate();
	}

	private ItemStack getMachineStack() {
		for (BlocksEnum block : BlocksEnum.values()) {
			if (block.tileEnum.clazz == this.getClass()) {
				return new ItemStack(block.itemBlock);
			}
		}

		return null;
	}

	public void notifyBlock() {

	}

	protected IGrid getNetwork() {
		return getGridNode().getGrid();
	}

	public IGridNode getGridNode() {
		return getGridNode(AEPartLocation.INTERNAL);
	}

	/**
	 * @param resource   Resource to be extracted
	 * @param actionable Simulate of Modulate?
	 * @return amount extracted
	 */
	public int extractEnergy(EnergyStack resource, Actionable actionable) throws GridAccessException {
		// Extract energy from MEInventory
		IAEEnergyStack extracted = getProxy().getStorage().getInventory(getEnergyChannel())
				.extractItems(AEEnergyStack.fromStack(resource), actionable, new MachineSource(this));
		if (extracted == null) {
			return 0;
		}

		return (int) (extracted.getStackSize());
	}

	private IEnergyStorageChannel getEnergyChannel() {
		return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
	}

	/**
	 * @param resource   Resource to be injected
	 * @param actionable Simulate or modulate?
	 * @return amount injected
	 */
	public int injectEnergy(EnergyStack resource, Actionable actionable) throws GridAccessException {
		if (getProxy().getNode() == null) {
			return 0;
		}

		IStorageGrid storage = getProxy().getStorage();
		IAEEnergyStack returnAmount = storage.getInventory(this.getEnergyChannel()).injectItems(getEnergyChannel().createStack(
				resource), actionable, new MachineSource(this));

		if (returnAmount == null) {
			return (int) resource.amount;
		}
		return (int) (resource.amount - returnAmount.getStackSize());
	}

	@Override
	public IGridNode getGridNode(@Nonnull AEPartLocation dir) {
		if (getProxy().getNode() == null) {
			getProxy().getNode();
		}

		return getProxy().getNode();
	}

	@Nonnull
	@Override
	public AECableType getCableConnectionType(AEPartLocation dir) {
		return AECableType.DENSE_SMART;
	}

	@Override
	public void securityBreak() {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		this.getProxy().writeToNBT(compound);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.getProxy().readFromNBT(compound);
	}


	@Nonnull
	@Override
	public DimensionalCoord getLocation() {
		return new DimensionalCoord(this);
	}


	@Override
	public void gridChanged() {

	}

	@Override
	public AENetworkProxy getProxy() {
		return proxy;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		destroyProxyNode();
	}

	@Override
	public void onChunkUnload() {
		destroyProxyNode();
	}

	@Nonnull
	@Override
	public IGridNode getActionableNode() {
		if (this.getProxy().getNode() == null) {
			createProxyNode();
		}

		return getProxy().getNode();
	}

	@Override
	public void update() {
		if (!loaded && hasWorld() && Platform.isServer()) {
			loaded = true;
			createProxyNode();
		}
	}

	@Override
	public BlockPos getHostPos() {
		return pos;
	}

	@Override
	public World getHostWorld() {
		return world;
	}

	@Override
	public AEPartLocation getHostSide() {
		return AEPartLocation.INTERNAL;
	}
}

package AppliedIntegrations.Parts.Energy;

import java.util.*;


import AppliedIntegrations.API.IEnergyInterface;
import AppliedIntegrations.API.Storage.EnumCapabilityType;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Helpers.IntegrationsHelper;
import AppliedIntegrations.Inventory.Handlers.HandlerEnergyStorageBusContainer;
import AppliedIntegrations.Inventory.Handlers.HandlerEnergyStorageBusInterface;
import AppliedIntegrations.Parts.*;
import AppliedIntegrations.Utils.AIGridNodeInventory;

import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.*;
import appeng.api.util.AECableType;
import ic2.api.energy.tile.IEnergySink;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;
import teamroots.embers.power.EmberCapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.Ember;
import static AppliedIntegrations.API.Storage.LiquidAIEnergy.J;

/**
 * @Author Azazell
 */
public class PartEnergyStorage
		extends AIPart
		implements ICellContainer, IMEMonitorHandlerReceiver<IAEEnergyStack>, IGridTickable  {

	public static final int FILTER_SIZE = 18;
	private final Vector<LiquidAIEnergy> filteredEnergies = new Vector<>();
	private IMEInventoryHandler<IAEEnergyStack> handler;
	private boolean lastActive = false;

	/**
	 * Creates the bus
	 */
	public PartEnergyStorage()
	{
		// Call super
		this( PartEnum.EnergyStorageBus, SecurityPermissions.EXTRACT, SecurityPermissions.INJECT );

		// Pre-fill the list with nulls
		for( int index = 0; index < this.FILTER_SIZE; index++ ) {
			this.filteredEnergies.add(null);
		}
	}

    protected PartEnergyStorage(PartEnum manaStorage, SecurityPermissions inject, SecurityPermissions extract) {
		super(manaStorage, inject, extract);
	}

	@Override
	public void onNeighborChanged(IBlockAccess access, BlockPos pos, BlockPos neighbor) {
		if (pos == null || neighbor == null)
			return;
		if (pos.offset(this.getSide().getFacing()).equals(neighbor) && this.getGridNode() != null) {
			IGrid grid = this.getGridNode().getGrid();
			if (grid != null) {
				grid.postEvent(new MENetworkCellArrayUpdate());
			}
		}

		if (getFacingContainer() != null) {
			if (getFacingContainer() instanceof IEnergyInterface) {
				handler = new HandlerEnergyStorageBusInterface();
			} else if (getFacingContainer().hasCapability(CapabilityEnergy.ENERGY, getSide().getFacing())) {
				handler = new HandlerEnergyStorageBusContainer(this, getFacingContainer(), EnumCapabilityType.FE);
			} else if (IntegrationsHelper.instance.isLoaded(Ember) && getFacingContainer().hasCapability(EmberCapabilityProvider.emberCapability, getSide().getFacing())) {
				handler = new HandlerEnergyStorageBusContainer(this, getFacingContainer(), EnumCapabilityType.Ember);
			} else if (IntegrationsHelper.instance.isLoaded(LiquidAIEnergy.J) && getFacingContainer().hasCapability(Capabilities.ENERGY_ACCEPTOR_CAPABILITY, getSide().getFacing())) {
				handler = new HandlerEnergyStorageBusContainer(this, getFacingContainer(), EnumCapabilityType.Joules);
			} else if (IntegrationsHelper.instance.isLoaded(LiquidAIEnergy.EU) && getFacingContainer() instanceof IEnergySink) {
				handler = new HandlerEnergyStorageBusContainer(this, getFacingContainer(), EnumCapabilityType.EU);
			}
		}
	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {
		return 2;
	}


	@Override
	public TickingRequest getTickingRequest( final IGridNode node )
	{
		// We would like a tick ever 20 MC ticks
		return new TickingRequest( 20, 20, false, false );
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int i) {
		return TickRateModulation.SAME;
	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() {
		return null;
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox( 3, 3, 15, 13, 13, 16 );
		bch.addBox( 2, 2, 14, 14, 14, 15 );
		bch.addBox( 5, 5, 12, 11, 11, 14 );
	}

	@Override
	public double getIdlePowerUsage() {
		return 0;
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered())
			if (this.isActive())
				return PartModelEnum.STORAGE_BUS_HAS_CHANNEL;
			else
				return PartModelEnum.STORAGE_BUS_ON;
		return PartModelEnum.STORAGE_BUS_OFF;
	}

	@Override
	public void blinkCell(int i) {

	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
		if (channel != this.getChannel() || this.handler == null)
			return new LinkedList<>();
		LinkedList<IMEInventoryHandler> list = new LinkedList<>();

		list.add(this.handler);

		return list;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public boolean isValid(Object o) {
		return false;
	}

	@Override
	public void postChange(IBaseMonitor<IAEEnergyStack> iBaseMonitor, Iterable<IAEEnergyStack> iterable, IActionSource iActionSource) {

	}

	@Override
	public void onListUpdate() {

	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {

	}

	public TileEntity getFacingContainer() {
		TileEntity candidate = getFacingTile();

		if(candidate == null)
			return null;
		for(Capability capability : getAllowedCappabilities()){
			if(candidate.hasCapability(capability, getSide().getFacing()))
				return candidate;
		}

		return null;
	}

	private List<Capability> getAllowedCappabilities() {
		ArrayList<Capability> capabilities = new ArrayList<>();
		capabilities.add(CapabilityEnergy.ENERGY);

		// (If loaded -> add to allowed) blocks:
		if(IntegrationsHelper.instance.isLoaded(Ember))
			capabilities.add(EmberCapabilityProvider.emberCapability);
		if(IntegrationsHelper.instance.isLoaded(J)) {
			capabilities.add(Capabilities.ENERGY_STORAGE_CAPABILITY);
			capabilities.add(Capabilities.ENERGY_OUTPUTTER_CAPABILITY);
			capabilities.add(Capabilities.ENERGY_ACCEPTOR_CAPABILITY);
		}

		return capabilities;
	}

	public boolean extractPowerForEnergyTransfer(int drained, Actionable simulate) {
		return false;
	}

	public void saveContainer(TileEntity tileEntity) {

	}

	@MENetworkEventSubscribe
	public void updateChannels(final MENetworkChannelsChanged changedChannels) {
		final boolean currentActive = this.getGridNode().isActive();
		if (this.lastActive != currentActive) {
			this.lastActive = currentActive;
			getGridNode().getGrid().postEvent(new MENetworkCellArrayUpdate());
			this.host.markForUpdate();
		}
	}
}


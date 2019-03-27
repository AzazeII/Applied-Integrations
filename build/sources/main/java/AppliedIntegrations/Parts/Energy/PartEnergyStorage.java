package AppliedIntegrations.Parts.Energy;

import java.util.*;


import AppliedIntegrations.AIConfig;
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
import appeng.api.util.AEPartLocation;
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
import static java.util.Collections.singletonList;

/**
 * @Author Azazell
 */
public class PartEnergyStorage
		extends AIPart
		implements ICellContainer, IGridTickable  {

	// Size of filter
	public static final int FILTER_SIZE = 18;

	// List of all energies filtered
	private final Vector<LiquidAIEnergy> filteredEnergies = new Vector<>();

	// Handler for tile/interface
	private IMEInventoryHandler<IAEEnergyStack> handler;

	// Was active?
	private boolean lastActive = false;

	public PartEnergyStorage()
	{
		// Call super
		this( PartEnum.EnergyStorageBus, SecurityPermissions.EXTRACT, SecurityPermissions.INJECT );

		// Iterate until filter size
		for( int index = 0; index < this.FILTER_SIZE; index++ ) {
			// Fill vector
			this.filteredEnergies.add(null);
		}
	}

	// Called to allow mana storage bus extend from this bus
    protected PartEnergyStorage(PartEnum manaStorage, SecurityPermissions inject, SecurityPermissions extract) {
		super(manaStorage, inject, extract);
	}

	public void postCellEvent(){
		// Get node
		IGridNode node = getGridNode(AEPartLocation.INTERNAL);
		// Check notNull
		if (node != null) {
			// Get grid
			IGrid grid = node.getGrid();
			// Check not null
			if(grid != null) {
				// Post update
				grid.postEvent(new MENetworkCellArrayUpdate());
			}
		}
	}

	@Override
	public void onNeighborChanged(IBlockAccess access, BlockPos pos, BlockPos neighbor) {
		// Check not null
		if (pos == null || neighbor == null)
			return;
		// Check if changed neighbor was next to storage bus's side
		if (pos.offset(this.getSide().getFacing()).equals(neighbor)) {
			// Notify cell array
			postCellEvent();
		}

		// Check not null
		if (getFacingContainer() != null) {
			if (getFacingContainer() instanceof IEnergyInterface) {
				handler = new HandlerEnergyStorageBusInterface((IEnergyInterface)getFacingContainer());

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
		// Check if channel present working channel, and handler not null
		if (channel != this.getChannel() || this.handler == null)
			return new LinkedList<>();

		// Return only one handler for tile
		return singletonList(handler);
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
		// Check if inventory not null
		if (iCellInventory != null)
			// Persist inventory
			iCellInventory.persist();
		// Mark dirty
		getHostTile().getWorld().markChunkDirty(getHostTile().getPos(), getHostTile());
	}

	public TileEntity getFacingContainer() {
		// Create candidate
		TileEntity candidate = getFacingTile();

		// Check not null
		if(candidate == null)
			return null;

		// Iterate over capabilities
		for(Capability capability : getAllowedCappabilities()) {
			// Check if candidate has capability
			if(candidate.hasCapability(capability, getSide().getFacing()))
				// Return candidate
				return candidate;
		}

		return null;
	}

	private List<Capability> getAllowedCappabilities() {
		// Create capability list
		ArrayList<Capability> capabilities = new ArrayList<>();

		// Add FE by default
		capabilities.add(CapabilityEnergy.ENERGY);

		// (If loaded -> add to allowed) blocks:
		if(IntegrationsHelper.instance.isLoaded(Ember) && AIConfig.enablEmberFeatures)
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


	@MENetworkEventSubscribe
	public void updateChannels(final MENetworkChannelsChanged changedChannels) {
		final boolean currentActive = this.getGridNode().isActive();
		if (this.lastActive != currentActive) {
			this.lastActive = currentActive;
			this.host.markForUpdate();

			postCellEvent();
		}
	}
}


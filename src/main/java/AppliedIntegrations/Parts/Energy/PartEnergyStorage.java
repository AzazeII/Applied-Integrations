package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.AIConfig;
import AppliedIntegrations.API.IEnergyInterface;
import AppliedIntegrations.API.Storage.EnergyRepo;
import AppliedIntegrations.API.Storage.EnumCapabilityType;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Container.part.ContainerEnergyStorage;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Helpers.IntegrationsHelper;
import AppliedIntegrations.Inventory.Handlers.HandlerEnergyStorageBusContainer;
import AppliedIntegrations.Inventory.Handlers.HandlerEnergyStorageBusInterface;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketAccessModeServerToClient;
import AppliedIntegrations.Network.Packets.PacketFilterServerToClient;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.ChangeHandler;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.tile.networking.TileCableBus;
import ic2.api.energy.tile.IEnergySink;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import teamroots.embers.power.EmberCapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.*;
import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;
import static AppliedIntegrations.Gui.AIGuiHandler.GuiEnum.GuiStoragePart;
import static java.util.Collections.singletonList;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

/**
 * @Author Azazell
 */
public class PartEnergyStorage
		extends AIPart
		implements ICellContainer, IGridTickable, IEnergyMachine {

	// Size of filter
	public static final int FILTER_SIZE = 18;

	// List of all energies filtered
	public final List<LiquidAIEnergy> filteredEnergies = new LinkedList<>();

	// Handler for onChange event of access
	private final ChangeHandler<AccessRestriction> accessRestrictionChangeHandler = new ChangeHandler<>();

	// Current access restrictions of handler
	public AccessRestriction access = AccessRestriction.READ_WRITE;

	// Handler for tile/interface
	private IMEInventoryHandler<IAEEnergyStack> handler;

	// Was active?
	private boolean lastActive = false;

	// List of all container - listeners
	public List<ContainerEnergyStorage> linkedListeners = new ArrayList<>();
	private boolean updateRequested;
	private List<ChangeHandler<LiquidAIEnergy>> filteredEnergiesChangeHandler = new ArrayList<>();

	public PartEnergyStorage()
	{
		// Call super
		this( PartEnum.EnergyStorageBus, SecurityPermissions.EXTRACT, SecurityPermissions.INJECT );

		// Iterate until filter size
		for( int index = 0; index < this.FILTER_SIZE; index++ ) {
			// Fill vector
			this.filteredEnergies.add(null);

			// Fill list
			this.filteredEnergiesChangeHandler.add(new ChangeHandler<>());
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

	public void setAccess(AccessRestriction access) {
		this.access = access;

		// Notify grid
		this.postCellEvent();
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
		if (getFacingTile() != null) {
			// Check for energy interface
			if (getFacingTile() instanceof IEnergyInterface) {
				handler = new HandlerEnergyStorageBusInterface((IEnergyInterface)getFacingTile(), this);

			// Check for part tile
			} else if(getFacingTile() instanceof TileCableBus){
				// Get interface candidate
				TileCableBus maybeInterface = (TileCableBus)getFacingTile();

				// Check if candidate instanceof IEnergyInterface
				if(maybeInterface.getPart(getSide().getOpposite()) instanceof IEnergyInterface){
					handler = new HandlerEnergyStorageBusInterface((IEnergyInterface)((TileCableBus) getFacingTile()).getPart(getSide().getOpposite()), this);
				}

			// Check for all energy types:
			} else if (getFacingTile().hasCapability(CapabilityEnergy.ENERGY, getSide().getFacing())) {
				handler = new HandlerEnergyStorageBusContainer(this, getFacingTile(), EnumCapabilityType.FE);
			} else if (IntegrationsHelper.instance.isLoaded(Ember) && getFacingTile().hasCapability(EmberCapabilityProvider.emberCapability, getSide().getFacing())) {
				handler = new HandlerEnergyStorageBusContainer(this, getFacingTile(), EnumCapabilityType.Ember);
			} else if (IntegrationsHelper.instance.isLoaded(LiquidAIEnergy.J) && getFacingTile().hasCapability(Capabilities.ENERGY_ACCEPTOR_CAPABILITY, getSide().getFacing())) {
				handler = new HandlerEnergyStorageBusContainer(this, getFacingTile(), EnumCapabilityType.Joules);
			} else if (IntegrationsHelper.instance.isLoaded(LiquidAIEnergy.EU) && getFacingTile() instanceof IEnergySink) {
				handler = new HandlerEnergyStorageBusContainer(this, getFacingTile(), EnumCapabilityType.EU);
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
		// Update every 20 ticks
		return new TickingRequest( 20, 20, false, false );
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int ticksSinceLastCall) {
		// Iterate over all listeners
		for(ContainerEnergyStorage listener : linkedListeners) {
			// Iterate over all filtered energies
			for (int i = 0; i < FILTER_SIZE; i++) {
				// Create effectively final variable
				int finalI = i;

				// Create on change event
				filteredEnergiesChangeHandler.get(i).onChange(filteredEnergies.get(i), (energy -> {
					// Sync with client
					NetworkHandler.sendTo(new PacketFilterServerToClient(energy, finalI, this), (EntityPlayerMP) listener.player);
				}));

				// Check if update was requested
				if(updateRequested)
					// Sync with client
					NetworkHandler.sendTo(new PacketFilterServerToClient(filteredEnergies.get(i), finalI, this), (EntityPlayerMP) listener.player);
			}

			// Check if energy was changed
			accessRestrictionChangeHandler.onChange(access, (accessRestriction -> {
				// Sync with client
				NetworkHandler.sendTo(new PacketAccessModeServerToClient(access, this), (EntityPlayerMP) listener.player);
			}));

			// Check if update was requested
			if(updateRequested)
				// Sync with client
				NetworkHandler.sendTo(new PacketAccessModeServerToClient(access, this), (EntityPlayerMP) listener.player);
		}
		// Reset update request
		updateRequested = false;

		return TickRateModulation.SAME;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		// Iterate for filter size
		for(int i = 0; i < FILTER_SIZE; i++) {
			// Read string
			String energyTag = tag.getString("#ENERGY" + i);

			// Check not "null"
			if(energyTag.equals("null"))
				// Set null energy
				filteredEnergies.set(i, null);
			else
				// Otherwise get filtered energy from map
				filteredEnergies.set(i, energies.get(energyTag));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		// Iterate for filter size
		for(int i = 0; i < FILTER_SIZE; i++) {
			// Check not null
			if (filteredEnergies.get(i) != null)
				// Write energy
				tag.setString("#ENERGY" + i, filteredEnergies.get(i).getTag());
			else
				// Write "null"
				tag.setString("#ENERGY" + i, "null");
		}
	}

	@Override
    public AIGridNodeInventory getUpgradeInventory() {
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

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Activation logic is server sided
		if (getLogicalSide() == SERVER) {
			if (!player.isSneaking()) {
				// Open gui
				AIGuiHandler.open(GuiStoragePart, player, getSide(), getHostTile().getPos());

				// Request filter update
				updateRequested = true;

				// Render click
				return true;
			}
		}

		return false;
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

	@Override
	public void updateFilter(LiquidAIEnergy energy, int index) {
		filteredEnergies.set(index, energy);
	}
}


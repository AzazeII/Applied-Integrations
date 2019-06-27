package AppliedIntegrations.Parts.Energy;
import AppliedIntegrations.Container.part.ContainerEnergyStorage;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Inventory.Handlers.HandlerEnergyStorageBusContainer;
import AppliedIntegrations.Inventory.Handlers.HandlerEnergyStorageBusInterface;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketAccessModeServerToClient;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFilterServerToClient;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.api.IEnergyInterface;
import AppliedIntegrations.api.IInventoryHost;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.EnumCapabilityType;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
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
import appeng.api.parts.PartItemStack;
import appeng.api.storage.ICellContainer;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.core.sync.GuiBridge;
import appeng.tile.networking.TileCableBus;
import appeng.util.Platform;
import ic2.api.energy.tile.IEnergySink;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static AppliedIntegrations.Gui.AIGuiHandler.GuiEnum.GuiStoragePart;
import static AppliedIntegrations.api.Storage.LiquidAIEnergy.energies;
import static AppliedIntegrations.grid.Implementation.AIEnergy.EU;
import static AppliedIntegrations.grid.Implementation.AIEnergy.J;
import static java.util.Collections.singletonList;

/**
 * @Author Azazell
 */
public class PartEnergyStorage extends AIPart implements ICellContainer, IGridTickable, IEnergyMachine, IInventoryHost, IPriorityHostExtended {

	// Size of filter
	public static final int FILTER_SIZE = 18;

	// list of all energies filtered
	public final List<LiquidAIEnergy> filteredEnergies = new LinkedList<>();

	// Handler for onChange event of access
	private final ChangeHandler<AccessRestriction> accessRestrictionChangeHandler = new ChangeHandler<>();

	// Current access restrictions of handler
	public AccessRestriction access = AccessRestriction.READ_WRITE;

	// list of all container - listeners
	public List<ContainerEnergyStorage> linkedListeners = new ArrayList<>();

	// Handler for tile/interface
	private IMEInventoryHandler<IAEEnergyStack> handler;

	// Was active?
	private boolean lastActive = false;

	// current priority of host
	private int priority = 0;

	private boolean updateRequested;

	private List<ChangeHandler<LiquidAIEnergy>> filteredEnergiesChangeHandler = new ArrayList<>();

	private AIGridNodeInventory upgradeInventory = new AIGridNodeInventory("ME Energy Export/Import Bus", 4, 1, this) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemStack) {
			return AEApi.instance().definitions().materials().cardInverter().isSameAs(itemStack);
		}
	};

	public PartEnergyStorage() {
		// Call super
		this(PartEnum.EnergyStorageBus, SecurityPermissions.EXTRACT, SecurityPermissions.INJECT);

		// Iterate until filter size
		for (int index = 0; index < FILTER_SIZE; index++) {
			// Fill vector
			this.filteredEnergies.add(null);

			// Fill list
			this.filteredEnergiesChangeHandler.add(new ChangeHandler<>());
		}
	}

	// Called to allow mana storage bus extend from this bus
	protected PartEnergyStorage(PartEnum manaStorage, SecurityPermissions inject, SecurityPermissions extract) {

		super(manaStorage);
	}

	public void setAccess(AccessRestriction access) {

		this.access = access;

		// Notify grid
		this.postCellEvent();
	}

	public void postCellEvent() {
		// Get node
		IGridNode node = getGridNode(AEPartLocation.INTERNAL);
		// Check notNull
		if (node != null) {
			// Get grid
			IGrid grid = node.getGrid();

			// Post update
			grid.postEvent(new MENetworkCellArrayUpdate());
		}
	}

	private IMEInventoryHandler<IAEEnergyStack> generateNewHandler(TileEntity tile) {
		// Check for energy interface
		if (tile instanceof IEnergyInterface) {
			handler = new HandlerEnergyStorageBusInterface((IEnergyInterface) tile, this);

			// Check for host tile
		} else if (tile instanceof TileCableBus) {
			// Get interface candidate
			TileCableBus maybeInterface = (TileCableBus) tile;

			// Check if candidate instanceof IEnergyInterface
			if (maybeInterface.getPart(getHostSide().getOpposite()) instanceof IEnergyInterface) {
				handler = new HandlerEnergyStorageBusInterface((IEnergyInterface) ((TileCableBus) tile).getPart(
						getHostSide().getOpposite()), this);
			}

			// Check for all energy types:
		} else if (IntegrationsHelper.instance.isLoaded(J, false) && tile.hasCapability(Capabilities.ENERGY_ACCEPTOR_CAPABILITY,
				getHostSide().getFacing().getOpposite())) { // 1. Joules
			// Create handler for joules
			handler = new HandlerEnergyStorageBusContainer(this, tile, EnumCapabilityType.Joules);
		} else if (tile.hasCapability(CapabilityEnergy.ENERGY, getHostSide().getFacing().getOpposite())) { // 2. FE
			// Create handler for FE
			handler = new HandlerEnergyStorageBusContainer(this, tile, EnumCapabilityType.FE);
		} else if (IntegrationsHelper.instance.isLoaded(EU, false) && tile instanceof IEnergySink) { // 3. EU
			// Create handler for EU
			handler = new HandlerEnergyStorageBusContainer(this, tile, EnumCapabilityType.EU);
		}

		return handler;
	}

	private IMEInventoryHandler<IAEEnergyStack> getHandler() {
		return handler == null && getFacingTile() != null ? generateNewHandler(getFacingTile()) : handler;
	}

	@Override
	public TickingRequest getTickingRequest(final IGridNode node) {
		// Update every 20 ticks
		return new TickingRequest(20, 20, false, false);
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int ticksSinceLastCall) {
		// Iterate over all listeners
		for (ContainerEnergyStorage listener : linkedListeners) {
			// Iterate over all filtered energies
			for (int i = 0; i < FILTER_SIZE; i++) {
				// Create effectively final variable
				int finalI = i;

				// Create on change event
				filteredEnergiesChangeHandler.get(i).onChange(filteredEnergies.get(i), (energy -> {
					// Sync with client
					NetworkHandler.sendTo(new PacketFilterServerToClient(energy, finalI, this),
							(EntityPlayerMP) listener.player);
				}));

				// Check if update was requested
				if (updateRequested) {
					// Sync with client
					NetworkHandler.sendTo(new PacketFilterServerToClient(filteredEnergies.get(i), finalI, this),
							(EntityPlayerMP) listener.player);
				}
			}

			// Check if energy was changed
			accessRestrictionChangeHandler.onChange(access, (accessRestriction -> {
				// Sync with client
				NetworkHandler.sendTo(new PacketAccessModeServerToClient(access, this),
						(EntityPlayerMP) listener.player);
			}));

			// Check if update was requested
			if (updateRequested) {
				// Sync with client
				NetworkHandler.sendTo(new PacketAccessModeServerToClient(access, this),
						(EntityPlayerMP) listener.player);
			}
		}

		// Reset update request
		updateRequested = false;

		return TickRateModulation.SAME;
	}

	@Override
	public AIGridNodeInventory getUpgradeInventory() {

		return upgradeInventory;
	}

	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox(3, 3, 15, 13, 13, 16);
		bch.addBox(2, 2, 14, 14, 14, 15);
		bch.addBox(5, 5, 12, 11, 11, 14);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		// Iterate for filter size
		for (int i = 0; i < FILTER_SIZE; i++) {
			// Check not null
			if (filteredEnergies.get(i) != null) {
				// Write energy
				tag.setString("#ENERGY" + i, filteredEnergies.get(i).getTag());
			} else {
				// Write "null"
				tag.setString("#ENERGY" + i, "null");
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		// Iterate for filter size
		for (int i = 0; i < FILTER_SIZE; i++) {
			// Read string
			String energyTag = tag.getString("#ENERGY" + i);

			// Check not "null"
			if (energyTag.equals("null")) {
				// Set null energy
				filteredEnergies.set(i, null);
			} else {
				// Otherwise get filtered energy from map
				filteredEnergies.set(i, energies.get(energyTag));
			}
		}
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public void onNeighborChanged(IBlockAccess access, BlockPos pos, BlockPos neighbor) {
		// Check not null
		if (pos == null || neighbor == null) {
			return;
		}

		// Get facing tile
		TileEntity tile = getFacingTile();

		// Check not null
		if (tile != null) {
			// Generate new handler for facing tile
			generateNewHandler(tile);

			// Notify grid
			postCellEvent();
		}
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Activation logic is server sided
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				// Open gui
				AIGuiHandler.open(GuiStoragePart, player, getHostSide(), getHostTile().getPos());

				// Request filter update
				updateRequested = true;

				// Render click
				return true;
			}
		}

		return false;
	}

	@MENetworkEventSubscribe
	public void updateChannels(final MENetworkChannelsChanged changedChannels) {
		// Get current activity
		final boolean currentActive = this.getGridNode().isActive();

		// Check if activity changed
		if (this.lastActive != currentActive) {
			// Update last activity
			this.lastActive = currentActive;

			// Mark host for update
			this.host.markForUpdate();

			// Make network know cell array should be updated
			postCellEvent();
		}
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {
		return 2;
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered()) {
			if (this.isActive()) {
				return PartModelEnum.STORAGE_BUS_HAS_CHANNEL;
			} else {
				return PartModelEnum.STORAGE_BUS_ON;
			}
		}
		return PartModelEnum.STORAGE_BUS_OFF;
	}

	@Override
	public void blinkCell(int i) {

	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel<?> channel) {
		// Get handler
		IMEInventoryHandler<IAEEnergyStack> handler = getHandler();

		// Check if channel present working channel, and handler not null
		if (channel != this.getChannel() || handler == null) {
			return new LinkedList<>();
		}

		// Return only one handler for tile
		return singletonList(handler);
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
		// Ignored
		return null;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
		// Check if inventory not null
		if (iCellInventory != null)
		// Persist inventory
		{
			iCellInventory.persist();
		}
		// Mark dirty
		getHostTile().getWorld().markChunkDirty(getHostTile().getPos(), getHostTile());
	}

	@Override
	public void updateFilter(LiquidAIEnergy energy, int index) {

		filteredEnergies.set(index, energy);
	}

	@Override
	public void onInventoryChanged() {

	}

	@Nonnull
	@Override
	public AIGuiHandler.GuiEnum getGui() {

		return GuiStoragePart;
	}
}


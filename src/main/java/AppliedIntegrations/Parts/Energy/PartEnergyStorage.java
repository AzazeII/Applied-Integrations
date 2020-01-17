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
import AppliedIntegrations.api.IEnumHost;
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
public class PartEnergyStorage extends AIPart implements ICellContainer, IGridTickable, IEnergyMachine, IInventoryHost, IPriorityHostExtended,
		IEnumHost {
	public static final int FILTER_SIZE = 18;

	public final List<LiquidAIEnergy> filteredEnergies = new LinkedList<>();
	private final ChangeHandler<AccessRestriction> accessRestrictionChangeHandler = new ChangeHandler<>();
	public AccessRestriction access = AccessRestriction.READ_WRITE;
	public List<ContainerEnergyStorage> linkedListeners = new ArrayList<>();

	// Our current handler,
	// May be container handler if machine next to us is just a normal energy container
	// Or IEnergyInterface handler if machine is energy interface, so storage bus should bridge it's network with network of interface
	private IMEInventoryHandler<IAEEnergyStack> handler;

	private boolean lastActive = false;
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
		this(PartEnum.EnergyStorageBus, SecurityPermissions.EXTRACT, SecurityPermissions.INJECT);

		for (int index = 0; index < FILTER_SIZE; index++) {
			this.filteredEnergies.add(null);
			this.filteredEnergiesChangeHandler.add(new ChangeHandler<>());
		}
	}

	protected PartEnergyStorage(PartEnum manaStorage, SecurityPermissions inject, SecurityPermissions extract) {
		super(manaStorage);
	}

	private void postCellEvent() {
		IGridNode node = getGridNode(AEPartLocation.INTERNAL);
		if (node != null) {
			IGrid grid = node.getGrid();
			grid.postEvent(new MENetworkCellArrayUpdate());
		}
	}

	private IMEInventoryHandler<IAEEnergyStack> generateNewHandler(TileEntity tile) {
		// Here we pick handler for given tile
		if (tile instanceof IEnergyInterface) {
			handler = new HandlerEnergyStorageBusInterface((IEnergyInterface) tile, this);

		} else if (tile instanceof TileCableBus) {
			TileCableBus maybeInterface = (TileCableBus) tile;

			if (maybeInterface.getPart(getHostSide().getOpposite()) instanceof IEnergyInterface) {
				handler = new HandlerEnergyStorageBusInterface((IEnergyInterface) ((TileCableBus) tile).getPart(
						getHostSide().getOpposite()), this);
			}

		} else if (IntegrationsHelper.instance.isLoaded(J, false) && tile.hasCapability(Capabilities.ENERGY_ACCEPTOR_CAPABILITY,
				getHostSide().getFacing().getOpposite())) {
			handler = new HandlerEnergyStorageBusContainer(this, tile, EnumCapabilityType.Joules);
		} else if (tile.hasCapability(CapabilityEnergy.ENERGY, getHostSide().getFacing().getOpposite())) {
			handler = new HandlerEnergyStorageBusContainer(this, tile, EnumCapabilityType.FE);
		} else if (IntegrationsHelper.instance.isLoaded(EU, false) && tile instanceof IEnergySink) {
			handler = new HandlerEnergyStorageBusContainer(this, tile, EnumCapabilityType.EU);
		}

		return handler;
	}

	private IMEInventoryHandler<IAEEnergyStack> getHandler() {
		return handler == null && getFacingTile() != null ? generateNewHandler(getFacingTile()) : handler;
	}

	@Override
	public TickingRequest getTickingRequest(final IGridNode node) {
		return new TickingRequest(20, 20, false, false);
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode iGridNode, int ticksSinceLastCall) {
		for (ContainerEnergyStorage listener : linkedListeners) {
			for (int i = 0; i < FILTER_SIZE; i++) {
				int finalI = i;

				filteredEnergiesChangeHandler.get(i).onChange(filteredEnergies.get(i), (energy -> {
					NetworkHandler.sendTo(new PacketFilterServerToClient(energy, finalI, this),
							(EntityPlayerMP) listener.player);
				}));

				if (updateRequested) {
					NetworkHandler.sendTo(new PacketFilterServerToClient(filteredEnergies.get(i), finalI, this),
							(EntityPlayerMP) listener.player);
				}
			}

			accessRestrictionChangeHandler.onChange(access, (accessRestriction -> {
				NetworkHandler.sendTo(new PacketAccessModeServerToClient(access, this),
						(EntityPlayerMP) listener.player);
			}));

			if (updateRequested) {
				NetworkHandler.sendTo(new PacketAccessModeServerToClient(access, this),
						(EntityPlayerMP) listener.player);
			}
		}

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
		for (int i = 0; i < FILTER_SIZE; i++) {
			if (filteredEnergies.get(i) != null) {
				tag.setString("#ENERGY" + i, filteredEnergies.get(i).getTag());
			} else {
				tag.setString("#ENERGY" + i, "null");
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		for (int i = 0; i < FILTER_SIZE; i++) {
			String energyTag = tag.getString("#ENERGY" + i);

			if (energyTag.equals("null")) {
				filteredEnergies.set(i, null);
			} else {
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
		if (pos == null || neighbor == null) {
			return;
		}

		TileEntity tile = getFacingTile();
		if (tile != null) {
			generateNewHandler(tile);
			postCellEvent();
		}
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				AIGuiHandler.open(GuiStoragePart, player, getHostSide(), getHostTile().getPos());
				updateRequested = true;
				return true;
			}
		}

		return false;
	}

	@MENetworkEventSubscribe
	public void updateChannels(final MENetworkChannelsChanged changedChannels) {
		final boolean currentActive = this.getGridNode().isActive();

		// Notify network about cell array change when channels got updated. This makes network knows that we must be included in network storage
		if (this.lastActive != currentActive) {
			this.lastActive = currentActive;
			this.host.markForUpdate();
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
		IMEInventoryHandler<IAEEnergyStack> handler = getHandler();
		if (channel != this.getChannel() || handler == null) {
			return new LinkedList<>();
		}

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
		return null;
	}

	@Override
	public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
		// Writes NBT of inventory
		if (iCellInventory != null) {
			iCellInventory.persist();
		}
		getHostTile().getWorld().markChunkDirty(getHostTile().getPos(), getHostTile());
	}

	@Override
	public void updateFilter(LiquidAIEnergy energy, int index) {
		filteredEnergies.set(index, energy);
	}

	@Override
	public void onInventoryChanged() {}

	@Nonnull
	@Override
	public AIGuiHandler.GuiEnum getGui() {
		return GuiStoragePart;
	}

	@Override
	public void setEnumVal(Enum val) {
		this.access = (AccessRestriction) val;
		this.postCellEvent();
	}
}


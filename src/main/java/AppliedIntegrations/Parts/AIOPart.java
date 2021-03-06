package AppliedIntegrations.Parts;
import AppliedIntegrations.Container.part.ContainerPartEnergyIOBus;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Inventory.Manager.UpgradeInventoryManager;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFilterServerToClient;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.api.IEnumHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.EnumCapabilityType;
import appeng.api.config.RedstoneMode;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.util.AECableType;
import appeng.core.sync.GuiBridge;
import appeng.util.Platform;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.Inventory.AIGridNodeInventory.validateStack;
import static appeng.api.config.RedstoneMode.IGNORE;

/**
 * @Author Azazell
 */

public abstract class AIOPart extends AIPart implements IGridTickable, IEnergyMachine, IPriorityHostExtended, IEnumHost {
	public final static int MAX_FILTER_SIZE = 9;

	public List<LiquidAIEnergy> filteredEnergies = new ArrayList<>(AIOPart.MAX_FILTER_SIZE);

	private final static int MINIMUM_TICKS_PER_OPERATION = 2;
	private final static int MAXIMUM_TICKS_PER_OPERATION = 40;

	private final static int BASE_ENERGY_TRANSFER = 4000;
	private final static int MAXIMUM_TRANSFER_PER_SECOND = 36000;
	private final static int MINIMUM_TRANSFER_PER_SECOND = 4000;
	private final static int TRANSFER_PER_UPGRADE = 8090;

	private static final String NBT_KEY_REDSTONE_MODE = "redstoneMode";
	private static final String NBT_KEY_FILTER_NUMBER = "EnergyFilter#";

	protected int maxTransfer;
	protected TileEntity adjacentEnergyStorage;
	private EntityPlayer player;
	private boolean lastRedstone;

	private List<ContainerPartEnergyIOBus> listeners = new ArrayList<ContainerPartEnergyIOBus>();
	private byte upgradeSpeedCount = 0;

	private List<ChangeHandler<LiquidAIEnergy>> filteredEnergiesChangeHandler = new ArrayList<>();
	public UpgradeInventoryManager upgradeInventoryManager = new UpgradeInventoryManager(this, "ME Energy Export/Import Bus", 4, (stack) -> validateStack(stack));
	private int priority = 0;

	public AIOPart(final PartEnum associatedPart) {
		super(associatedPart);

		maxTransfer = 5000 * 10 * upgradeSpeedCount;
		for (int i = 0; i < MAX_FILTER_SIZE; i++) {
			filteredEnergies.add(i, null);
			filteredEnergiesChangeHandler.add(new ChangeHandler<>());
		}
	}

	public void addListener(final ContainerPartEnergyIOBus container) {
		if (!this.listeners.contains(container)) {
			this.listeners.add(container);
		}
	}

	@Override
	public AIGridNodeInventory getUpgradeInventory() {
		return this.upgradeInventoryManager.upgradeInventory;
	}

	@Override
	public void getBoxes(IPartCollisionHelper helper) {

	}

	@Override
	public void readFromNBT(final NBTTagCompound data) {
		super.readFromNBT(data);

		for (int index = 0; index < MAX_FILTER_SIZE; index++) {
			if (data.hasKey(NBT_KEY_FILTER_NUMBER + index)) {
				this.filteredEnergies.set(index, LiquidAIEnergy.energies.get(data.getString(NBT_KEY_FILTER_NUMBER + index)));
			}
		}

		this.upgradeInventoryManager.upgradeInventory.readFromNBT(data.getTagList("upgradeInventoryManager", 10));
	}

	/**
	 * Produces a small amount of light when active.
	 */
	@Override
	public int getLightLevel() {

		return (this.isActive() ? 4 : 0);
	}

	@Override
	public void onNeighborChanged(IBlockAccess iBlockAccess, BlockPos blockPos, BlockPos blockPos1) {
		if (getHostWorld().isRemote) {
			return;
		}

		this.adjacentEnergyStorage = null;

		TileEntity tileEntity = this.getFacingTile();
		if (tileEntity == null) {
			return;
		}

		for (EnumCapabilityType type : EnumCapabilityType.values) {
			if (type.isUsesType(tileEntity, getHostSide().getFacing().getOpposite())) {
				this.adjacentEnergyStorage = tileEntity;
				break;
			}
		}

		if (this.upgradeInventoryManager.redstoneMode == RedstoneMode.SIGNAL_PULSE) {
			if (this.isReceivingRedstonePower() != this.lastRedstone) {
				this.lastRedstone = this.isReceivingRedstonePower();
				this.processTick(20);
			}
		}
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Override
	public boolean onActivate(final EntityPlayer player, EnumHand hand, final Vec3d position) {
		super.onActivate(player, hand, position);
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiIOPart, player, getHostSide(), getHostTile().getPos());

				this.player = player;
				return true;
			}
		}

		return false;
	}

	@Override
	public void getDrops(List<ItemStack> drops, boolean wrenched) {
		for (ItemStack stack : upgradeInventoryManager.upgradeInventory.slots) {
			if (stack == null) {
				continue;
			}
			drops.add(stack);
		}
	}

	@Override
	public float getCableConnectionLength(AECableType cable) {
		return 0;
	}

	@Override
	public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {
		super.writeToNBT(data, saveType);

		if ((saveType == PartItemStack.WORLD) || (saveType == PartItemStack.WRENCH)) {
			int i = 0;

			for (LiquidAIEnergy energy : filteredEnergies) {
				if (energy != null) {
					data.setString(NBT_KEY_FILTER_NUMBER + i, energy.getTag());
				}
				i++;
			}

			if (saveType == PartItemStack.WORLD) {
				if (this.upgradeInventoryManager.redstoneMode != IGNORE) {
					data.setInteger(NBT_KEY_REDSTONE_MODE, this.upgradeInventoryManager.redstoneMode.ordinal());
				}

				data.setTag("upgradeInventoryManager", this.upgradeInventoryManager.upgradeInventory.writeToNBT());
			}
		}
	}

	@Override
	public TickingRequest getTickingRequest(final IGridNode node) {
		return new TickingRequest(MINIMUM_TICKS_PER_OPERATION, MAXIMUM_TICKS_PER_OPERATION, false, false);
	}

	@Override
	public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
		updateGui();

		if (this.canDoWork(upgradeInventoryManager.redstoneMode)) {
			return processTick(ticksSinceLastCall);
		}

		return TickRateModulation.IDLE;
	}

	private TickRateModulation processTick(int ticksSinceLastCall) {
		int transferAmountPerSecond = this.getTransferAmountPerSecond();
		int transferAmount = (int) (transferAmountPerSecond * (ticksSinceLastCall / 20.F));

		transferAmount = Math.min(transferAmount, MAXIMUM_TRANSFER_PER_SECOND);
		transferAmount = Math.max(transferAmount, MINIMUM_TRANSFER_PER_SECOND);
		return doWork(transferAmount, getProxy().getNode());
	}

	private void updateGui() {
		for (int i = 0; i < MAX_FILTER_SIZE; i++) {
			int finalI = i;
			filteredEnergiesChangeHandler.get(i).onChange(filteredEnergies.get(i), (energy -> {
				this.notifyListenersOfFilterEnergyChange(finalI, energy);
			}));

			notifyListenersOfFilterEnergyChange(i, filteredEnergies.get(i));
		}
	}

	protected int getTransferAmountPerSecond() {
		return BASE_ENERGY_TRANSFER + (this.upgradeSpeedCount * TRANSFER_PER_UPGRADE);
	}

	public abstract TickRateModulation doWork(int toTransfer, IGridNode node);

	private void notifyListenersOfFilterEnergyChange(int i, LiquidAIEnergy energy) {
		if (player != null) {
			NetworkHandler.sendTo(new PacketFilterServerToClient(energy, i, this), (EntityPlayerMP) this.player);
		}
	}

	public void removeListener(final ContainerPartEnergyIOBus container) {
		this.listeners.remove(container);
	}

	@Override
	public final void updateFilter(LiquidAIEnergy energy, int index) {
		this.filteredEnergies.set(index, energy);
		this.notifyListenersOfFilterEnergyChange(index, energy);
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

	@Nonnull
	@Override
	public AIGuiHandler.GuiEnum getGui() {
		return AIGuiHandler.GuiEnum.GuiIOPart;
	}

	@Override
	public void setEnumVal(Enum val) {
		this.upgradeInventoryManager.acceptVal(val);
		this.upgradeInventoryManager.redstoneControlled = val != IGNORE;
	}
}

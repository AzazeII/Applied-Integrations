package AppliedIntegrations.Parts.Energy;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.part.ContainerEnergyTerminal;
import AppliedIntegrations.Gui.AIGui;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Helpers.Energy.StackCapabilityHelper;
import AppliedIntegrations.Helpers.Energy.Utils;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PartGUI.PacketTerminalUpdate;
import AppliedIntegrations.Parts.AIRotatablePart;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.api.IEnumHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.AEEnergyStack;
import appeng.api.config.*;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IConfigManager;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static appeng.api.networking.ticking.TickRateModulation.SAME;

/**
 * @Author Azazell
 */
public class PartEnergyTerminal extends AIRotatablePart implements ITerminalHost, IConfigManagerHost, IGridTickable,
		IMEMonitorHandlerReceiver<IAEEnergyStack>, IEnumHost {
	private static final String TAG_IO_INVENTORY = "#IO_INVENTORY";
	public List<ContainerEnergyTerminal> listeners = new ArrayList<>();
	public LiquidAIEnergy selectedEnergy;
	private IConfigManager configManager = new ConfigManager(this);

	private boolean updateRequsted;

	private SortOrder sortingOrder = SortOrder.NAME;
	public AIGridNodeInventory energyIOInventory = new AIGridNodeInventory(AppliedIntegrations.modid + ".item.energy.cell.inventory",
			2, 64) {
		@Override
		public boolean isItemValidForSlot(final int slotID, final ItemStack itemStack) {
			return Utils.getEnergyFromItemStack(itemStack, getHostWorld()) != null;
		}
	};

	public PartEnergyTerminal() {
		super(PartEnum.EnergyTerminal);

		// Register setting for terminal
		// Sort mode (default: name)
		configManager.registerSetting(Settings.SORT_BY, SortOrder.NAME);

		// View mode (default: all)
		configManager.registerSetting(Settings.VIEW_MODE, ViewItems.ALL);

		// Sort direction (default: ascending)
		configManager.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.energyIOInventory.readFromNBT(data.getTagList(TAG_IO_INVENTORY, 10));
	}

	@Override
	public void writeToNBT(NBTTagCompound data, PartItemStack saveType) {
		super.writeToNBT(data, saveType);
		data.setTag(TAG_IO_INVENTORY, this.energyIOInventory.writeToNBT());
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand hand, Vec3d position) {
		// Check if terminal is active
		if (isActive()) {
			// Open gui
			AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiTerminalPart, player, getHostSide(), getHostPos());

			// Trigger update request
			updateRequsted = true;

			return true;
		}

		return false;
	}

	@Override
	protected AIGridNodeInventory getUpgradeInventory() {
		return null;
	}

	@Override
	public void getBoxes(final IPartCollisionHelper helper) {
		helper.addBox(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
		helper.addBox(4.0D, 4.0D, 13.0D, 12.0D, 12.0D, 14.0D);
		helper.addBox(5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 13.0D);
	}

	@Override
	public int getLightLevel() {
		// Check if active
		if (isActive()) {
			return ACTIVE_TERMINAL_LIGHT_LEVEL;
		}
		return 0;
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Override
	public float getCableConnectionLength(AECableType cable) {
		return 2;
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered()) {
			if (this.isActive()) {
				return PartModelEnum.TERMINAL_HAS_CHANNEL;
			} else {
				return PartModelEnum.TERMINAL_ON;
			}
		}
		return PartModelEnum.TERMINAL_OFF;
	}

	@Nonnull
	@Override
	public TickingRequest getTickingRequest(@Nonnull IGridNode node) {
		return new TickingRequest(1, 1, false, false);
	}

	@Nonnull
	@Override
	public TickRateModulation tickingRequest(@Nonnull IGridNode node, int ticksSinceLastCall) {
		// Check if update was requested
		if (updateRequsted) {
			// Check if we have gui to update
			if (!(Minecraft.getMinecraft().currentScreen instanceof AIGui)) {
				// Break function
				return SAME;
			}

			// Do all AE2 mechanics only on server
			if (!this.getHostWorld().isRemote) {

				// Get energy inventory
				IMEMonitor<IAEEnergyStack> inv = this.getEnergyInventory();

				// Check not null
				if (inv != null) {
					// Notify GUI first time about list, to make it show current list of all energies
					for (ContainerEnergyTerminal listener : this.listeners) {
						// Send packet over network
						NetworkHandler.sendTo(new PacketTerminalUpdate(inv.getStorageList(), sortingOrder, this), (EntityPlayerMP) listener.player);

						// Trigger request
						updateRequsted = false;
					}
				}
			}
		}

		// Here we charge input slot item
		final ItemStack inputStack = energyIOInventory.getStackInSlot(0);
		final LiquidAIEnergy energyFromItemStack = Utils.getEnergyFromItemStack(inputStack, getHostWorld());
		if (inputStack != null && energyFromItemStack != null) {
			StackCapabilityHelper helper = new StackCapabilityHelper(inputStack);

			boolean success = false;
			final int stored = helper.getStored(energyFromItemStack);
			if (selectedEnergy != energyFromItemStack || stored > 0) {
				// Extract energy into system from stack
				int storedEnergy = helper.getStored(energyFromItemStack);
				if (storedEnergy > 0) {
					int extracted = helper.extractEnergy(energyFromItemStack, storedEnergy, Actionable.MODULATE);
					if (extracted > 0) {
						injectEnergy(new EnergyStack(energyFromItemStack, extracted), Actionable.MODULATE);
						success = true;
					}
				}
			} else if (stored == 0) {
				// Inject energy from system to stack
				IAEEnergyStack storedStack =
						getEnergyInventory().getStorageList().findPrecise(AEEnergyStack.fromStack(new EnergyStack(energyFromItemStack, 0)));
				if (storedStack != null) {
					int injected = helper.injectEnergy(storedStack.getEnergy(), (int) storedStack.getStackSize(), Actionable.MODULATE);
					if (injected > 0) {
						extractEnergy(new EnergyStack(energyFromItemStack, injected), Actionable.MODULATE);
						success = true;
					}
				}
			}

			// Transfer stack from input to output slot
			if (success) {
				energyIOInventory.setInventorySlotContents(0, ItemStack.EMPTY);
				energyIOInventory.setInventorySlotContents(1, inputStack);
			}
		}

		return SAME;
	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
		// Get node
		IGridNode node = getGridNode(AEPartLocation.INTERNAL);

		// Getting Node
		if (node == null) {
			// No inventory provided
			return null;
		}

		// Getting net of node
		IGrid grid = node.getGrid();

		// Storage cache of network
		IStorageGrid storage = grid.getCache(IStorageGrid.class);

		// Get inventory of cache
		return storage.getInventory(channel);
	}

	@Override
	public void updateSetting(IConfigManager manager, Enum settingName, Enum newValue) {
		// Ignored
	}

	@Override
	public IConfigManager getConfigManager() {
		return this.configManager;
	}

	@Override
	public boolean isValid(Object verificationToken) {
		return true;
	}

	@Override
	public void postChange(IBaseMonitor<IAEEnergyStack> monitor, Iterable<IAEEnergyStack> change, IActionSource actionSource) {
		for (ContainerEnergyTerminal listener : listeners) {
			NetworkHandler.sendTo(new PacketTerminalUpdate(((IMEMonitor<IAEEnergyStack>) monitor).getStorageList(), sortingOrder, this), (EntityPlayerMP) listener.player);
		}
	}

	@Override
	public void onListUpdate() {

	}

	public SortOrder getSortOrder() {
		return this.sortingOrder;
	}

	@Override
	public void setEnumVal(Enum val) {
		this.sortingOrder = (SortOrder) val;
	}
}
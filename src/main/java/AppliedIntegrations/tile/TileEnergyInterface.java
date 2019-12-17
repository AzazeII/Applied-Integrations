package AppliedIntegrations.tile;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Helpers.EnergyInterfaceDuality;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.*;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.networking.IGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.api.util.INetworkToolAgent;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static AppliedIntegrations.grid.Implementation.AIEnergy.*;
import static appeng.api.util.AEPartLocation.INTERNAL;

/**
 * @Author Azazell
 */
public class TileEnergyInterface extends AITile implements IEnergyMachine, INetworkToolAgent, IEnergyInterface, IStorageMonitorable, IInventoryHost {
	private static final String NBT_KEY_ENERGY_TAG = "#ENERGY_SUB_TAG";
	private static final String NBT_KEY_BAR_TAG = "#BAR_SUB_TAG";

	public static int capacity = 100000;

	private LinkedHashMap<AEPartLocation, EnergyInterfaceStorage> RFStorage = new LinkedHashMap<>();
	private LinkedHashMap<AEPartLocation, EnergyInterfaceStorage> EUStorage = new LinkedHashMap<>();
	private LinkedHashMap<AEPartLocation, JouleInterfaceStorage> JOStorage = new LinkedHashMap<>();
	private LinkedHashMap<AEPartLocation, LiquidAIEnergy> filteredEnergies = new LinkedHashMap<>();
	private LinkedHashMap<AEPartLocation, LiquidAIEnergy> barMap = new LinkedHashMap<>();

	private List<ContainerEnergyInterface> containers = new ArrayList<ContainerEnergyInterface>();
	private EnergyInterfaceDuality duality = new EnergyInterfaceDuality(this);
	private boolean updateRequested;

	private AIGridNodeInventory upgradeInventory = new AIGridNodeInventory("", 1, 1, this) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemStack) {

			return validateStack(itemStack);
		}
	};

	public TileEnergyInterface() {
		// Iterate foreach side
		for (AEPartLocation dir : AEPartLocation.SIDE_LOCATIONS) {
			// Init storage from this side
			duality.initStorage(dir);
		}
	}

	public int x() {
		return super.pos.getX();
	}

	public int y() {
		return super.pos.getY();
	}

	public int z() {
		return super.pos.getZ();
	}

	public void addListener(final ContainerEnergyInterface container) {
		if (!this.containers.contains(container)) {
			this.containers.add(container);
		}
	}

	public void onActivate(EntityPlayer player, AEPartLocation side) {
		// Activation logic is server sided
		if (Platform.isServer()) {
			// Don't activate GUI if player isn't sneaking
			if (!player.isSneaking()) {
				// Open GUI
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiInterface, player, getHostSide(), getPos());

				// Request gui update
				updateRequested = true;
			}
		}
	}

	public AIGridNodeInventory getUpgradeInventory() {

		return this.upgradeInventory;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		// Iterate for each side
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			// Get tag from NBT
			NBTTagCompound energyTag = nbt.getCompoundTag(NBT_KEY_ENERGY_TAG + side.name());

			// Get index from tag
			int energyIndex = energyTag.getInteger("#AIEnergy");

			// Check not -1
			if (energyIndex == -1) {
				// Put null in map
				filteredEnergies.put(side, null);
			} else {
				// Put new entry in map using val from nbt
				filteredEnergies.put(side, LiquidAIEnergy.readFromNBT(energyTag));
			}

			// Get tag from NBT
			NBTTagCompound barTag = nbt.getCompoundTag(NBT_KEY_ENERGY_TAG + side.name());

			// Get index from tag
			int barIndex = barTag.getInteger("#AIEnergy");

			// Check not -1
			if (barIndex == -1) {
				// Put null in map
				filteredEnergies.put(side, null);
			} else {
				// Put new entry in map using val from nbt
				filteredEnergies.put(side, LiquidAIEnergy.readFromNBT(barTag));
			}

			// Iterate for each energy
			for (LiquidAIEnergy next : LiquidAIEnergy.energies.values()) {
				// Get tag from NBT
				NBTTagCompound storageTag = nbt.getCompoundTag(NBT_KEY_ENERGY_TAG + side.name() + next.getEnergyName());

				// Get storage
				IInterfaceStorageDuality storage = getEnergyStorage(next, side);

				// Check not null & instanceof NBT storage
				if (storage instanceof INBTStorage) {
					// Cast to storage
					INBTStorage inbtStorage = (INBTStorage) storage;

					// Write to NBT
					inbtStorage.readFromNBT(storageTag);
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		// Iterate for each side
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			// Create separated tag from main tag
			NBTTagCompound energyTag = new NBTTagCompound();

			// Get energy from map
			LiquidAIEnergy energy = filteredEnergies.get(side);

			// Check not null
			if (energy != null) {
				// Put energy in tag
				energy.writeToNBT(energyTag);
			} else {
				// Put null in tag
				energyTag.setInteger("#AIEnergy", -1);
			}

			// Put tag in tag
			nbt.setTag(NBT_KEY_ENERGY_TAG + side.name(), energyTag);

			// Create separated tag from main tag
			NBTTagCompound barTag = new NBTTagCompound();

			// Get energy from map
			LiquidAIEnergy bar = barMap.get(side);

			// Check not null
			if (bar != null) {
				// Put energy in tag
				bar.writeToNBT(barTag);
			} else {
				// Put null in tag
				barTag.setInteger("#AIEnergy", -1);
			}

			// Put tag in tag
			nbt.setTag(NBT_KEY_BAR_TAG + side.name(), barTag);

			// Iterate for each energy
			for (LiquidAIEnergy next : LiquidAIEnergy.energies.values()) {
				// Create sub tag
				NBTTagCompound storageTag = new NBTTagCompound();

				// Get storage
				IInterfaceStorageDuality storage = getEnergyStorage(next, side);

				// Check not null & instanceof NBT storage
				if (storage instanceof INBTStorage) {
					// Cast to storage
					INBTStorage inbtStorage = (INBTStorage) storage;

					// Write to NBT
					inbtStorage.writeToNBT(storageTag);
				}

				// Put tag into tag
				nbt.setTag(NBT_KEY_ENERGY_TAG + side.name() + next.getEnergyName(), storageTag);
			}
		}

		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return duality.hasCapability(capability);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		return duality.getCapability(capability, AEPartLocation.fromFacing(facing));
	}

	@Override
	public void onInventoryChanged() {}

	@Override
	public double getMaxTransfer(AEPartLocation side) {
		return capacity / (double) 2;
	}

	@Override
	public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {
		return filteredEnergies.get(side);
	}

	public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
		if (energy == RF) {
			return this.RFStorage.get(side);
		} else if (energy == EU) {
			return this.EUStorage.get(side);
		} else if (energy == J) {
			return this.JOStorage.get(side);
		}
		return null;
	}

	@Override
	public void doInjectDualityWork(Actionable action) throws NullNodeConnectionException, GridAccessException {
		duality.doInjectDualityWork(action);
	}

	@Override
	public void doExtractDualityWork(Actionable action) throws NullNodeConnectionException, GridAccessException {
		duality.doExtractDualityWork(action);
	}

	@Override
	public void initEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
		if (energy == RF) {
			RFStorage.put(side, new EnergyInterfaceStorage(this, side, energy, capacity, capacity / 2));
		}
		if (energy == EU) {
			EUStorage.put(side, new EnergyInterfaceStorage(this, side, energy, (int) (capacity * 0.25), capacity * 2));
		}
		if (energy == J) {
			JOStorage.put(side, new JouleInterfaceStorage(this, side, capacity * 2));
		}
	}

	@Override
	public int getMaxEnergyStored(AEPartLocation side, LiquidAIEnergy linkedMetric) {
		// Check not null
		if (getEnergyStorage(linkedMetric, side) == null) {
			return 0;
		}

		// Get max energy stored number
		Number num = getEnergyStorage(linkedMetric, side).getMaxStored();

		// Check not null
		if (num == null) {
			return 0;
		}

		// Extract int value
		return num.intValue();
	}

	@Override
	public TileEntity getFacingTile(EnumFacing side) {
		return null;
	}

	@Override
	public List<ContainerEnergyInterface> getListeners() {
		return containers;
	}

	@Override
	public void setLastInjectedEnergy(AEPartLocation side, LiquidAIEnergy energy) {
		barMap.put(side, energy);
	}

	@Override
	public void update() {
		super.update();

		// Iterate for each side of part location
		for (AEPartLocation side : AEPartLocation.values()) {
			if (barMap.get(side) != null) {
				duality.notifyListenersOfEnergyBarChange(barMap.get(side), side);
				duality.notifyListenersOfBarFilterChange(barMap.get(side));
			}
		}

		try {
			if (getGridNode() == null) {
				return;
			}

			if (this.getGridNode().isActive()) {
				doInjectDualityWork(Actionable.MODULATE);
				doExtractDualityWork(Actionable.MODULATE);
			}
		} catch (NullNodeConnectionException | GridAccessException e) {
			AILog.error(e, "Node of Tile Energy Interface, when it's active could not be null.. But it is");
		}
	}

	@Override
	public void updateFilter(LiquidAIEnergy energyInArray, int index) {
		// Update filtered energy
		this.filteredEnergies.put(AEPartLocation.fromOrdinal(index), energyInArray);
	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> iStorageChannel) {
		// Getting Node
		if (getGridNode(INTERNAL) == null) {
			return null;
		}
		// Getting net of node
		IGrid grid = getGridNode(INTERNAL).getGrid();
		// Cache of net
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		// fluidInventory of cache
		return storage.getInventory(iStorageChannel);
	}

	@Override
	public boolean showNetworkInfo(RayTraceResult rayTraceResult) {
		return false;
	}
}

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
		for (AEPartLocation dir : AEPartLocation.SIDE_LOCATIONS) {
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
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiInterface, player, getHostSide(), getPos());
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

		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			NBTTagCompound energyTag = nbt.getCompoundTag(NBT_KEY_ENERGY_TAG + side.name());

			int energyIndex = energyTag.getInteger("#AIEnergy");

			if (energyIndex == -1) {
				filteredEnergies.put(side, null);
			} else {
				filteredEnergies.put(side, LiquidAIEnergy.readFromNBT(energyTag));
			}

			NBTTagCompound barTag = nbt.getCompoundTag(NBT_KEY_ENERGY_TAG + side.name());
			int barIndex = barTag.getInteger("#AIEnergy");

			if (barIndex == -1) {
				filteredEnergies.put(side, null);
			} else {
				filteredEnergies.put(side, LiquidAIEnergy.readFromNBT(barTag));
			}

			for (LiquidAIEnergy next : LiquidAIEnergy.energies.values()) {
				NBTTagCompound storageTag = nbt.getCompoundTag(NBT_KEY_ENERGY_TAG + side.name() + next.getEnergyName());
				IInterfaceStorageDuality storage = getEnergyStorage(next, side);
				if (storage instanceof INBTStorage) {
					INBTStorage inbtStorage = (INBTStorage) storage;

					inbtStorage.readFromNBT(storageTag);
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			NBTTagCompound energyTag = new NBTTagCompound();
			LiquidAIEnergy energy = filteredEnergies.get(side);

			if (energy != null) {
				energy.writeToNBT(energyTag);
			} else {
				energyTag.setInteger("#AIEnergy", -1);
			}

			nbt.setTag(NBT_KEY_ENERGY_TAG + side.name(), energyTag);
			NBTTagCompound barTag = new NBTTagCompound();
			LiquidAIEnergy bar = barMap.get(side);

			if (bar != null) {
				bar.writeToNBT(barTag);
			} else {
				barTag.setInteger("#AIEnergy", -1);
			}

			nbt.setTag(NBT_KEY_BAR_TAG + side.name(), barTag);
			for (LiquidAIEnergy next : LiquidAIEnergy.energies.values()) {
				NBTTagCompound storageTag = new NBTTagCompound();
				IInterfaceStorageDuality storage = getEnergyStorage(next, side);
				if (storage instanceof INBTStorage) {
					INBTStorage inbtStorage = (INBTStorage) storage;
					inbtStorage.writeToNBT(storageTag);
				}

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
		if (getEnergyStorage(linkedMetric, side) == null) {
			return 0;
		}

		Number num = getEnergyStorage(linkedMetric, side).getMaxStored();
		if (num == null) {
			return 0;
		}

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
		this.filteredEnergies.put(AEPartLocation.fromOrdinal(index), energyInArray);
	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> iStorageChannel) {
		if (getGridNode(INTERNAL) == null) {
			return null;
		}

		IGrid grid = getGridNode(INTERNAL).getGrid();
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		return storage.getInventory(iStorageChannel);
	}

	@Override
	public boolean showNetworkInfo(RayTraceResult rayTraceResult) {
		return false;
	}
}

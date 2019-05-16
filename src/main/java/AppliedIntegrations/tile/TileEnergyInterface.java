package AppliedIntegrations.tile;


import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Helpers.EnergyInterfaceDuality;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFilterServerToClient;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AIGridNodeInventory;
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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;
import static AppliedIntegrations.grid.Implementation.AIEnergy.*;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

/**
 * @Author Azazell
 */
public class TileEnergyInterface extends AITile implements IEnergyMachine, INetworkToolAgent, IEnergyInterface, IStorageMonitorable, IInventoryHost {

	public static int capacity = 100000;

	private Boolean energyStates[] = new Boolean[6];

	private LinkedHashMap<AEPartLocation, EnergyInterfaceStorage> RFStorage = new LinkedHashMap<>();

	private LinkedHashMap<AEPartLocation, EnergyInterfaceStorage> EUStorage = new LinkedHashMap<>();

	private LinkedHashMap<AEPartLocation, JouleInterfaceStorage> JOStorage = new LinkedHashMap<>();

	private LinkedHashMap<AEPartLocation, EmberInterfaceStorageDuality> EmberStorage = new LinkedHashMap<>();

	private LinkedHashMap<AEPartLocation, LiquidAIEnergy> barMap = new LinkedHashMap<>();

	private EnergyInterfaceDuality duality = new EnergyInterfaceDuality(this);

	private List<ContainerEnergyInterface> linkedListeners = new ArrayList<ContainerEnergyInterface>();

	private byte outputTracker;

	private boolean updateRequested;

	private AIGridNodeInventory upgradeInventory = new AIGridNodeInventory("", 1, 1, this) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemStack) {

			return validateStack(itemStack);
		}
	};

	public TileEnergyInterface() {

		this.energyStates[1] = true;
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

		if (!this.linkedListeners.contains(container)) {
			this.linkedListeners.add(container);
		}
	}

	public void onActivate(EntityPlayer player, AEPartLocation side) {
		// Activation logic is server sided
		if (getLogicalSide() == SERVER) {
			if (!player.isSneaking()) {
				// Open GUI
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiInterfacePart, player, getSide(), getPos());

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
		outputTracker = nbt.getByte("Tracker");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		nbt.setByte("Tracker", outputTracker);
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
	public void onInventoryChanged() {

	}

	@Override
	public double getMaxTransfer(AEPartLocation side) {

		return capacity / (double) 2;
	}

	@Override
	public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {

		return null;
	}

	public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {

		if (energy == RF) {
			return this.RFStorage.get(side);
		} else if (energy == EU) {
			return this.EUStorage.get(side);
		} else if (energy == J) {
			return this.JOStorage.get(side);
		} else if (energy == Ember) {
			return this.EmberStorage.get(side);
		}
		return null;
	}

	@Override
	public void doInjectDualityWork(Actionable action) throws NullNodeConnectionException {

		duality.doInjectDualityWork(action);
	}

	@Override
	public void doExtractDualityWork(Actionable action) throws NullNodeConnectionException {

		duality.doExtractDualityWork(action);
	}

	@Override
	public void initEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {

		if (energy == RF) {
			RFStorage.put(side, new EnergyInterfaceStorage(this, capacity, capacity / 2));
		}
		if (energy == EU) {
			EUStorage.put(side, new EnergyInterfaceStorage(this, (int) (capacity * 0.25), capacity * 2));
		}
		if (energy == J) {
			JOStorage.put(side, new JouleInterfaceStorage(this, capacity * 2));
		}
		if (energy == Ember) {
			EmberStorage.put(side, new EmberInterfaceStorageDuality());
		}
	}

	@Override
	public int getMaxEnergyStored(AEPartLocation side, LiquidAIEnergy linkedMetric) {
		// Check not null
		if (getEnergyStorage(linkedMetric, side) == null) {
			return 0;
		}

		// Get max energy stored number
		Number num = (Number) getEnergyStorage(linkedMetric, side).getMaxStored();

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

		return linkedListeners;
	}

	@Override
	public void update() {

		super.update();

		if (updateRequested) {
			// Check if we have gui to update
			if (Minecraft.getMinecraft().currentScreen instanceof AIBaseGui) {
				// Init gui coordinate set
				initGuiCoordinates();

				// Force update filtered energy of gui
				notifyListenersOfFilterEnergyChange();
			}
		}

		// Iterate for each side of part location
		for (AEPartLocation side : AEPartLocation.values()) {
			// Place bar counter here, because we have not one, but six bars
			int barCounter = 0;

			// Iterate for each energy
			for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
				// Check if storage for given side and energy not null
				if (getEnergyStorage(energy, side) != null) {
					// Check if storage from given side and energy has any stored energy
					if (((Number) getEnergyStorage(energy, side).getStored()).doubleValue() > 0) {
						// Put put energy in bar map
						barMap.put(side, energy);

						// Notify client
						duality.notifyListenersOfBarFilterChange(barMap.get(side));

						// Add to counter
						barCounter += 1;
					}
				}
			}

			// Check if bar counter equal to 0
			if (barCounter == 0)
			// Put null in this bar map entry
			{
				barMap.put(side, null);
			}

			// Notify container and gui
			if (barMap.get(side) != null)
			// Bar Filter With Gui
			{
				duality.notifyListenersOfEnergyBarChange(barMap.get(side), side);
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
		} catch (NullNodeConnectionException e) {
			AILog.error(e, "Node of Tile Energy Interface, when it's active could not be null.. But it is");
		}
	}

	private void initGuiCoordinates() {
		// Iterate for each listener
		for (ContainerEnergyInterface listener : this.linkedListeners) {
			// Check not null
			if (listener != null) {
				// Send packet init
				NetworkHandler.sendTo(new PacketCoordinateInit(this), (EntityPlayerMP) listener.player);

				// Toggle request
				updateRequested = false;
			}
		}
	}

	private void notifyListenersOfFilterEnergyChange() {

		for (ContainerEnergyInterface listener : this.linkedListeners) {
			if (listener != null) {
				// Iterate for each side
				for (AEPartLocation side : AEPartLocation.values()) {
					// Iterate for each energy
					LiquidAIEnergy.energies.values().forEach((liquidAIEnergy -> {
						NetworkHandler.sendTo(new PacketFilterServerToClient(getFilteredEnergy(side), side.ordinal(), this), (EntityPlayerMP) listener.player);
					}));
				}
			}
		}
	}

	@Override
	public void updateFilter(LiquidAIEnergy energyInArray, int index) {

	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> iStorageChannel) {
		// Getting Node
		if (getGridNode(AEPartLocation.INTERNAL) == null) {
			return null;
		}
		// Getting net of node
		IGrid grid = getGridNode(AEPartLocation.INTERNAL).getGrid();
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

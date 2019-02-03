package AppliedIntegrations.Entities;

import AppliedIntegrations.API.IEnergyDuality;
import AppliedIntegrations.API.IEnergyInterface;
import AppliedIntegrations.API.IInventoryHost;
import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketProgressBar;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;

import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.api.util.INetworkToolAgent;

import appeng.me.helpers.MachineSource;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static AppliedIntegrations.API.LiquidAIEnergy.*;
import static net.minecraft.util.EnumFacing.SOUTH;

/**
 * @Author Azazell
 */
@Optional.InterfaceList(value = {
		@Optional.Interface(iface = "ic2.api.energy.*", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "cofh.api.energy.*",modid = "CoFHAPI",striprefs = true)
})
public class TileEnergyInterface extends AITile implements IEnergyMachine,IEnergyDuality, IEnergySink, IEnergyReceiver,INetworkToolAgent,IEnergyInterface,IStorageMonitorable,IInventoryHost {

	private static final boolean DualityMode = true;
	private static float Entropy;
	private Boolean energyStates[] = new Boolean[6];

	private LinkedHashMap<AEPartLocation, EnergyStorage> RFStorage = new LinkedHashMap<AEPartLocation,EnergyStorage>();
	private LinkedHashMap<AEPartLocation,EnergyStorage> EUStorage = new LinkedHashMap<AEPartLocation,EnergyStorage>();
	private LinkedHashMap<AEPartLocation,EnergyStorage> JOStorage = new LinkedHashMap<AEPartLocation,EnergyStorage>();

	private EnergyStorage Storage = new EnergyStorage(capacity, capacity/2);
	public static int EuStorage;

	public static int capacity = 100000;
	Map<Boolean,AEPartLocation> IoModes;

	private List<ContainerEnergyInterface> LinkedListeners = new ArrayList<ContainerEnergyInterface>();

	private AEPartLocation forward = AEPartLocation.INTERNAL;

	IEnergyReceiver[] adjacentHandlers = new IEnergyReceiver[6];
	byte outputTracker;
	private IMEInventory<IAEEnergyStack> destination = null;
	public boolean EnergyStates[] = new boolean[6];



	private boolean initPower;
	private IAEEnergyStack toExport;
	private boolean EUloaded = false;

	private long WattPower;
	private int torque;
	private int omega;
	private int alpha;
	private AIGridNodeInventory slotInventory = new AIGridNodeInventory("slot.inventory",9,1,this);
	private float lastTemperature;

	public TileEnergyInterface() {
			this.energyStates[1] = true;
			this.initPower = true;
			for(AEPartLocation dir : AEPartLocation.SIDE_LOCATIONS){
				RFStorage.put(dir,new EnergyStorage(capacity,capacity/2));
				EUStorage.put(dir,new EnergyStorage(capacity*4, capacity*2));
				JOStorage.put(dir,new EnergyStorage(capacity*2,capacity));
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

		public EnergyStorage getStorage() {
			return this.Storage;
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {

			super.readFromNBT(nbt);
			outputTracker = nbt.getByte("Tracker");
			Storage = new EnergyStorage(capacity, 800);
			Storage.setEnergyStored(nbt.getInteger("Energy"));
		}

		@Override
		public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

			super.writeToNBT(nbt);
			nbt.setByte("Tracker", outputTracker);
			nbt.setInteger("Energy", Storage.getEnergyStored());
			return nbt;
		}


		@Override
		public void invalidate() {
		  super.invalidate();
		  if (world != null && !world.isRemote) {
			destroyAELink();
		  }
			if (world != null && !world.isRemote) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}
		}

				@Override
				public void onChunkUnload() {
		  if (world != null && !world.isRemote) {
			destroyAELink();
		  }
		  if (world != null && !world.isRemote) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}

		}
	private void notifyListenersOfEnergyBarChange(LiquidAIEnergy Energy, int id, AEPartLocation side){
		for(ContainerEnergyInterface listener : this.LinkedListeners){
			if(listener!=null) {
				 NetworkHandler.sendTo(new PacketProgressBar(null, pos.getX(), pos.getY(), pos.getZ(),side.getFacing(),world),(EntityPlayerMP)listener.player);
			}
		}
	}
		@Override
		public void update() {
			super.update();
			if (!EUloaded && hasWorld() && !world.isRemote &&!EUloaded) {
				EUloaded = true;
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			}

			for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
				notifyListenersOfEnergyBarChange(RF, 0, side);
				notifyListenersOfEnergyBarChange(EU, 1, side);
				notifyListenersOfEnergyBarChange(J, 2, side);
			}

			try {
				if (this.DualityMode) {
					DoInjectDualityWork(Actionable.MODULATE);
				} else {
					DoInjectDualityWork(Actionable.SIMULATE);
				}
			}catch (NullNodeConnectionException error){

			}
		}


		public int InjectEnergy(IGridNode node, FluidStack resource, boolean doFill) {
			IGrid grid = node.getGrid();
			if (grid == null) {
				AILog.info("Grid cannot be initialized, WTF?");
				return 0;
			}
			IStorageGrid storage = (IStorageGrid)grid.getCache(IStorageGrid.class);
			if ((storage == null) && (node.getGrid().getCache(IStorageGrid.class) == null)) {
				AILog.info("StorageGrid cannot be initialized, WTF?");
				return 0; }
			IAEEnergyStack notRemoved;
			if (doFill) {
				notRemoved = (IAEEnergyStack)storage.getInventory(AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class)).injectItems(
						AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class).createStack(resource), Actionable.MODULATE, new MachineSource(this));
			}
			else
			{
				notRemoved = (IAEEnergyStack)storage.getInventory(AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class)).injectItems( AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class).createStack(resource), Actionable.SIMULATE, new MachineSource(this));
		}


			if (notRemoved == null)
				return resource.amount;
			return (int)(resource.amount - notRemoved.getStackSize());
		}
		public int ExtractEnergy(IGridNode node,FluidStack resource, boolean doFill) {
			if(node == null)
				return 0;
			IGrid grid = node.getGrid();
			if (grid == null) {
				AILog.info("Grid cannot be initialized, WTF?");
				return 0;
			}
			IStorageGrid storage = (IStorageGrid)grid.getCache(IStorageGrid.class);
			if ((storage == null) && (node.getGrid().getCache(IStorageGrid.class) == null)) {
				AILog.info("StorageGrid cannot be initialized, WTF?");
				return 0; }
			IAEEnergyStack notRemoved;
			if (doFill) {
				notRemoved = (IAEEnergyStack)storage.getInventory(AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class)).extractItems(
						AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class).createStack(resource), Actionable.MODULATE, new MachineSource(this));
			}
			else
			{
				notRemoved = (IAEEnergyStack)storage.getInventory(AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class)).extractItems( AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class).createStack(resource), Actionable.SIMULATE, new MachineSource(this));
			}


			if (notRemoved == null)
				return resource.amount;
			return (int)(resource.amount - notRemoved.getStackSize());
		}

		@Override
		public Object getServerGuiElement( final EntityPlayer player )
		{
			return new ContainerEnergyInterface(player,this);
		}
		@Override
		public Object getClientGuiElement( final EntityPlayer player )
		{
			return new GuiEnergyInterface((ContainerEnergyInterface) getServerGuiElement(player),this,player);
		}
		private AIGridNodeInventory upgradeInventory = new AIGridNodeInventory("", 1,
				1, this) {

			@Override
			public boolean isItemValidForSlot(int i, ItemStack itemStack) {
				if (itemStack == null)
					return false;
				if (AEApi.instance().definitions().materials().cardCapacity().isSameAs(itemStack))
					return true;
				else if (AEApi.instance().definitions().materials().cardSpeed().isSameAs(itemStack))
					return true;
				else if (AEApi.instance().definitions().materials().cardRedstone().isSameAs(itemStack))
					return true;
				return false;
			}
		};
		public AIGridNodeInventory getUpgradeInventory(){
			return this.upgradeInventory;
		}
		@Override
		public void onInventoryChanged() {

		}



	@Override
	public void DoInjectDualityWork(Actionable action) throws NullNodeConnectionException {


		IGridNode node = this.getGridNode(AEPartLocation.INTERNAL);
		if(node == null){
			throw new NullNodeConnectionException();
		}
		// Is it modulate, or matrix?
		if(action == Actionable.MODULATE) {
			for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
				if (this.getEnergyStorage(RF, side).getEnergyStored() > 0 && this.getEnergyStorage(J, side).getEnergyStored() == 0) {

					int ValuedReceive = Math.min(getEnergyStorage(RF, side).getEnergyStored(), capacity);

					int Diff = InjectEnergy(node, new FluidStack(RF, ValuedReceive), false) - ValuedReceive;
					if (Diff == 0) {
						this.getEnergyStorage(RF, side).modifyEnergyStored(-ValuedReceive);
						InjectEnergy(node, new FluidStack(RF, ValuedReceive + Diff), true);
					}
				}
				if (this.getEnergyStorage(EU, side).getEnergyStored() > 0) {

					int ValuedReceive = Math.min(getEnergyStorage(EU, side).getEnergyStored(), capacity);

					int Diff = InjectEnergy(node, new FluidStack(EU, ValuedReceive), false) - ValuedReceive;
					if (Diff == 0) {
						this.getEnergyStorage(EU, side).modifyEnergyStored(-ValuedReceive);
						InjectEnergy(node, new FluidStack(EU, ValuedReceive + Diff), true);

					}
				}
				if (this.getEnergyStorage(J, side).getEnergyStored() > 0) {
					int ValuedReceive = Math.min(getEnergyStorage(J, side).getEnergyStored(), capacity);

					int Diff = InjectEnergy(node, new FluidStack(J, ValuedReceive), false) - ValuedReceive;
					if (Diff == 0) {
						this.getEnergyStorage(J, side).modifyEnergyStored(-ValuedReceive);
						InjectEnergy(node, new FluidStack(J, ValuedReceive + Diff), true);

					}
				}
			}
		}


	}
	@Override
	public void DoExtractDualityWork(Actionable action) throws NullNodeConnectionException {

		IGridNode node = this.getGridNode(AEPartLocation.INTERNAL);
		if(node == null){
			throw new NullNodeConnectionException();
		}
		if(action == Actionable.MODULATE){
			for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
				if (this.getEnergyStorage(RF,side).getEnergyStored() > 0) {

					int ValuedExtract = Math.min(1000,capacity);

					int Diff = ExtractEnergy(node, new FluidStack(RF, ValuedExtract), false) + ValuedExtract;
					this.getEnergyStorage(RF,side).modifyEnergyStored(ValuedExtract);
					ExtractEnergy(node, new FluidStack(RF, ValuedExtract - Diff), true);
				}
				if (this.getEnergyStorage(EU,side).getEnergyStored() > 0) {

					int ValuedExtract = Math.min(1000,capacity);

					int Diff = ExtractEnergy(node, new FluidStack(EU, ValuedExtract), false) + ValuedExtract;
					this.getEnergyStorage(EU,side).modifyEnergyStored(ValuedExtract);
					ExtractEnergy(node, new FluidStack(EU, ValuedExtract - Diff), true);
				}
				if (this.getEnergyStorage(J,side).getEnergyStored() > 0) {

					int ValuedExtract = Math.min(1000,capacity);

					int Diff = ExtractEnergy(node, new FluidStack(J, ValuedExtract), false) + ValuedExtract;
					this.getEnergyStorage(J,side).modifyEnergyStored(ValuedExtract);
					ExtractEnergy(node, new FluidStack(J, ValuedExtract - Diff), true);
				}
			}

		}

	}

	/**
	 * IC2:
	 */
	@Override
	public double getDemandedEnergy() {
		return capacity-EuStorage;
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

	@Override
	public double injectEnergy(EnumFacing enumFacing, double v, double v1) {
		return 0;
	}

	@Override
	public double acceptEnergy(EnumFacing enumFacing, double v, boolean b) {
		return 0;
	}

	/**
	 * MekansimApi|Energy
	 */
	@Override
	public boolean canReceiveEnergy(EnumFacing side) {
		return true;
	}

	/**
	 * RedstoneFluxAPI:
	 */
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return this.RFStorage.get(from).receiveEnergy(maxReceive,simulate);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		// TODO Auto-generated method stub
		return this.RFStorage.get(from).extractEnergy(maxExtract,simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		// TODO Auto-generated method stub
		if(from!=null)
			return this.RFStorage.get(from).getEnergyStored();
		return this.RFStorage.get(SOUTH).getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		// TODO Auto-generated method stub
		return capacity;
	}
	private EnergyStorage getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
		if(energy == RF){
			return this.RFStorage.get(side);
		}else if(energy == EU){
			return this.EUStorage.get(side);
		}else if(energy == J){
			return this.JOStorage.get(side);
		}else{
			return null;
		}
	}

	@Override
	public double injectAEPower(double amt, Actionable mode) {
		return 0;
	}

	@Override
	public double getAEMaxPower() {
		return 0;
	}

	@Override
	public double getAECurrentPower() {
		return 0;
	}

	@Override
	public boolean isAEPublicPowerStorage() {
		return false;
	}

	@Override
	public AccessRestriction getPowerFlow() {
		return null;
	}

	@Override
	public double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
		return 0;
	}

	public void addListener( final ContainerEnergyInterface container )
	{
		if(!this.LinkedListeners.contains(container)){
			this.LinkedListeners.add(container);
		}
	}

	public void setWorkMode(AEPartLocation dir) {
		this.forward = dir;
	}

	public AIGridNodeInventory getSlotInventory() {
		return this.slotInventory;
	}

	@Override
	public LiquidAIEnergy getFilter(int index) {
		return null;
	}

	@Override
	public void updateFilter(LiquidAIEnergy energyInArray,int index) {

	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> iStorageChannel) {
		// Getting Node
		if (getGridNode(AEPartLocation.INTERNAL) == null)
			return null;
		// Getting net of node
		IGrid grid = getGridNode(AEPartLocation.INTERNAL).getGrid();
		if (grid == null)
			return null;
		// Cache of net
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		if (storage == null)
			return null;
		// fluidInventory of cache
		return storage.getInventory(iStorageChannel);
	}

	@Override
	public boolean showNetworkInfo(RayTraceResult rayTraceResult) {
		return false;
	}

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		return false;
	}
}

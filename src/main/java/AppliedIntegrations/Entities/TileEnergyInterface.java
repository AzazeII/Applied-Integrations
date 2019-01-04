package AppliedIntegrations.Entities;

import java.util.*;

import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketProgressBar;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.*;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;

import ic2.api.energy.tile.IEnergySink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import AppliedIntegrations.API.*;
import net.minecraftforge.fluids.FluidStack;

import static AppliedIntegrations.API.LiquidAIEnergy.*;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UNKNOWN;

/**
 * @Author Azazell
 */
@Optional.InterfaceList(value = {
		@Optional.Interface(iface = "ic2.api.energy.*", modid = "IC2", striprefs = true),
		@Optional.Interface(iface = "cofh.api.energy.*",modid = "CoFHAPI",striprefs = true)
})
public class TileEnergyInterface extends AITile implements IEnergyMachine,IEnergyDuality, IEnergySink,IEnergyReceiver,INetworkToolAgent,IEnergyInterface,ITileStorageMonitorable,IStorageMonitorable,IInventoryHost {

	private static final boolean DualityMode = true;
	private static float Entropy;
	private Boolean energyStates[] = new Boolean[6];

		private LinkedHashMap<ForgeDirection,EnergyStorage> RFStorage = new LinkedHashMap<ForgeDirection,EnergyStorage>();
		private LinkedHashMap<ForgeDirection,EnergyStorage> EUStorage = new LinkedHashMap<ForgeDirection,EnergyStorage>();
		private LinkedHashMap<ForgeDirection,EnergyStorage> JOStorage = new LinkedHashMap<ForgeDirection,EnergyStorage>();

		private EnergyStorage Storage = new EnergyStorage(capacity, capacity/2);
		public static int EuStorage;

		public static int capacity = 100000;
		Map<Boolean,ForgeDirection> IoModes;

		private List<ContainerEnergyInterface> LinkedListeners = new ArrayList<ContainerEnergyInterface>();

		private ForgeDirection forward = ForgeDirection.UNKNOWN;

		IEnergyReceiver[] adjacentHandlers = new IEnergyReceiver[6];
		byte outputTracker;
		private IMEInventory<IAEFluidStack> destination = null;
		public boolean EnergyStates[] = new boolean[6];



		private boolean initPower;
		private IAEFluidStack toExport;
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
			for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
				RFStorage.put(dir,new EnergyStorage(capacity,capacity/2));
				EUStorage.put(dir,new EnergyStorage(capacity*4, capacity*2));
				JOStorage.put(dir,new EnergyStorage(capacity*2,capacity));
			}
		}

		public int x() {
			return super.xCoord;
		}

		public int y() {
			return super.yCoord;
		}

		public int z() {
			return super.zCoord;
		}

		public EnergyStorage getStorage() {
			return this.Storage;
		}

		@Override
		public Packet getDescriptionPacket() {
			NBTTagCompound tag = new NBTTagCompound();
			writeToNBT(tag);
			return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
		}

		@Override
		public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
			readFromNBT(pkt.func_148857_g());
		}


		@Override
		public void readFromNBT(NBTTagCompound nbt) {

			super.readFromNBT(nbt);
			outputTracker = nbt.getByte("Tracker");
			Storage = new EnergyStorage(capacity, 800);
			Storage.setEnergyStored(nbt.getInteger("Energy"));
		}

		@Override
		public void writeToNBT(NBTTagCompound nbt) {

			super.writeToNBT(nbt);
			nbt.setByte("Tracker", outputTracker);
			nbt.setInteger("Energy", Storage.getEnergyStored());
		}


		@Override
		public void invalidate() {
		  super.invalidate();
		  if (worldObj != null && !worldObj.isRemote) {
			destroyAELink();
		  }
			if (worldObj != null && !worldObj.isRemote) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}
		}

				@Override
				public void onChunkUnload() {
		  if (worldObj != null && !worldObj.isRemote) {
			destroyAELink();
		  }
		  if (worldObj != null && !worldObj.isRemote) {
				MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
			}

		}
	private void notifyListenersOfEnergyBarChange(LiquidAIEnergy Energy,int id,ForgeDirection side){
		for(ContainerEnergyInterface listener : this.LinkedListeners){
			if(listener!=null) {
				 NetworkHandler.sendTo(new PacketProgressBar(null,xCoord,yCoord,zCoord,UNKNOWN,worldObj),(EntityPlayerMP)listener.player);
			}
		}
	}
		@Override
		public void updateEntity() {
			super.updateEntity();
			if (!EUloaded && hasWorldObj() && !worldObj.isRemote &&!EUloaded) {
				EUloaded = true;
				MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			}
			worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
			for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
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
				IAEFluidStack notRemoved;
				if (doFill) {
					notRemoved = (IAEFluidStack)storage.getFluidInventory().injectItems(
							AEApi.instance().storage().createFluidStack(resource), Actionable.MODULATE, new MachineSource(this));
				}
				else
				{
					notRemoved = (IAEFluidStack)storage.getFluidInventory().injectItems( AEApi.instance().storage().createFluidStack(resource), Actionable.SIMULATE, new MachineSource(this));
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
				IAEFluidStack notRemoved;
				if (doFill) {
					notRemoved = (IAEFluidStack)storage.getFluidInventory().extractItems(
							AEApi.instance().storage().createFluidStack(resource), Actionable.MODULATE, new MachineSource(this));
				}
				else
				{
					notRemoved = (IAEFluidStack)storage.getFluidInventory().extractItems( AEApi.instance().storage().createFluidStack(resource), Actionable.SIMULATE, new MachineSource(this));
				}


				if (notRemoved == null)
					return resource.amount;
				return (int)(resource.amount - notRemoved.getStackSize());
			}

			@Override
		public IStorageMonitorable getMonitorable(ForgeDirection side, BaseActionSource src) {
			// TODO Auto-generated method stub
			return this;
		}
		@Override
		public boolean showNetworkInfo(MovingObjectPosition where) {
			return true;
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


			IGridNode node = this.getGridNode(ForgeDirection.UNKNOWN);
			if(node == null){
				throw new NullNodeConnectionException();
			}
			// Is it modulate, or matrix?
			if(action == Actionable.MODULATE) {
				for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
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

			IGridNode node = this.getGridNode(ForgeDirection.UNKNOWN);
			if(node == null){
				throw new NullNodeConnectionException();
			}
			if(action == Actionable.MODULATE){
				for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
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
	 * Rotary Craft:
	 */
	@Override
	public boolean addPower(int i, int i1, long l, ForgeDirection forgeDirection) {
		this.torque = i;
		this.omega = i1;
		this.WattPower = l;
		return true;
	}
	@Override
	public ArrayList<String> getMessages(World world, int i, int i1, int i2, int i3) {
		final String out;
		if( this.WattPower >= 1000000000 )
		{
			out = String.format( "Receiving %.3f GW @ %d rad/s.", this.WattPower / 1000000000.0D, this.omega );
		}
		else if( this.WattPower >= 1000000 )
		{
			out = String.format( "Receiving %.3f MW @ %d rad/s.", this.WattPower / 1000000.0D, this.omega );
		}
		else if( this.WattPower >= 1000 )
		{
			out = String.format( "Receiving %.3f kW @ %d rad/s.", this.WattPower / 1000.0D, this.omega );
		}
		else
		{
			out = String.format( "Receiving %d WA @ %d rad/s.", this.WattPower, this.omega );
		}

		final ArrayList<String> messages = new ArrayList<String>( 1 );
		messages.add( out );
		return messages;
	}
	@Override
	public boolean canReadFrom(ForgeDirection forgeDirection) {
		return true;
	}

	@Override
	public boolean isReceiving() {
		return true;
	}

	@Override
	public int getMinTorque(int i) {
		return 1;
	}

	@Override
	public int getOmega() {
		return this.omega;
	}

	@Override
	public int getTorque() {
		return this.torque;
	}

	@Override
	public long getPower() {
		return this.WattPower;
	}

	@Override
	public String getName() {
		return "ME Energy Interface";
	}

	@Override
	public int getIORenderAlpha() {
		return this.alpha;
	}

	@Override
	public void setIORenderAlpha(int i) {
		this.alpha = i;
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
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		return this.EUStorage.get(directionFrom).receiveEnergy((int)amount,false);
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return true;
	}
	@Override
	public double getOfferedEnergy() {
		return 0;
	}

	@Override
	public void drawEnergy(double amount) {

	}

	@Override
	public int getSourceTier() {
		return 0;
	}

	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return false;
	}
	/**
	 * MekansimApi|Energy
	 */
	@Override
	public double transferEnergyToAcceptor(ForgeDirection side, double amount) {
		return this.JOStorage.get(side).receiveEnergy((int)amount,false);
	}

	@Override
	public boolean canReceiveEnergy(ForgeDirection side) {
		return true;
	}

	@Override
	public double getEnergy() {
		return this.EUStorage.get(ForgeDirection.UNKNOWN).getEnergyStored();
	}

	@Override
	public void setEnergy(double energy) {

	}

	@Override
	public double getMaxEnergy() {
		return this.capacity*4;
	}
	/**
	 * RedstoneFluxAPI:
	 */
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return this.RFStorage.get(from).receiveEnergy(maxReceive,simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		// TODO Auto-generated method stub
		return this.RFStorage.get(from).extractEnergy(maxExtract,simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		// TODO Auto-generated method stub
		if(from!=ForgeDirection.UNKNOWN)
			return this.RFStorage.get(from).getEnergyStored();
		return this.RFStorage.get(SOUTH).getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		// TODO Auto-generated method stub
		return capacity;
	}
	private EnergyStorage getEnergyStorage(LiquidAIEnergy energy, ForgeDirection side) {
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

	@Override
	public IMEMonitor<IAEItemStack> getItemInventory() {
		return null;
	}

	@Override
	public IMEMonitor<IAEFluidStack> getFluidInventory() {
		// Getting Node
		if (getGridNode(ForgeDirection.UNKNOWN) == null)
			return null;
		// Getting net of node
		IGrid grid = getGridNode(ForgeDirection.UNKNOWN).getGrid();
		if (grid == null)
			return null;
		// Cache of net
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		if (storage == null)
			return null;
		// fluidInventory of cache
		return storage.getFluidInventory();
	}

	public void setWorkMode(ForgeDirection dir) {
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
}

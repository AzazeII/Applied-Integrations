package AppliedIntegrations.tile;

import AppliedIntegrations.API.*;
import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketProgressBar;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.api.util.INetworkToolAgent;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static AppliedIntegrations.API.LiquidAIEnergy.*;
import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;

/**
 * @Author Azazell
 */
@Optional.InterfaceList(value = {
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergyEmitter", modid = "ic2", striprefs = true)
})
public class TileEnergyInterface extends AITile implements IEnergyMachine,IEnergyDuality,
		IEnergySink,INetworkToolAgent,IEnergyInterface,IStorageMonitorable,IInventoryHost {

	private static final boolean DualityMode = true;
	private Boolean energyStates[] = new Boolean[6];

	private LinkedHashMap<AEPartLocation, EnergyInterfaceStorage> RFStorage = new LinkedHashMap<>();
	private LinkedHashMap<AEPartLocation, EnergyInterfaceStorage> EUStorage = new LinkedHashMap<>();
	private LinkedHashMap<AEPartLocation, EnergyInterfaceStorage> JOStorage = new LinkedHashMap<>();

	private EnergyInterfaceStorage Storage = new EnergyInterfaceStorage(this, capacity, capacity/2);

	public static int EuStorage;

	public static int capacity = 100000;

	private List<ContainerEnergyInterface> LinkedListeners = new ArrayList<ContainerEnergyInterface>();

	private AEPartLocation forward = AEPartLocation.INTERNAL;

	byte outputTracker;
	public boolean EnergyStates[] = new boolean[6];

	private boolean EUloaded = false;

	private AIGridNodeInventory slotInventory = new AIGridNodeInventory("slot.inventory",9,1,this);

	public TileEnergyInterface() {
		this.energyStates[1] = true;
		for(AEPartLocation dir : AEPartLocation.SIDE_LOCATIONS){
			RFStorage.put(dir,new EnergyInterfaceStorage(this, capacity,capacity/2));
			EUStorage.put(dir,new EnergyInterfaceStorage(this, capacity*4, capacity*2));
			JOStorage.put(dir,new EnergyInterfaceStorage(this, capacity*2,capacity));
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

	public EnergyInterfaceStorage getStorage() {
		return this.Storage;
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

		/*try {
			if (this.DualityMode) {
				DoInjectDualityWork(Actionable.MODULATE);
			} else {
				DoInjectDualityWork(SIMULATE);
			}
		}catch (NullNodeConnectionException error){

		}*/
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

					int Diff = InjectEnergy(new FluidStack(RF, ValuedReceive), SIMULATE) - ValuedReceive;
					if (Diff == 0) {
						this.getEnergyStorage(RF, side).modifyEnergyStored(-ValuedReceive);
						InjectEnergy(new FluidStack(RF, ValuedReceive + Diff), MODULATE);
					}
				}
				if (this.getEnergyStorage(EU, side).getEnergyStored() > 0) {

					int ValuedReceive = Math.min(getEnergyStorage(EU, side).getEnergyStored(), capacity);

					int Diff = InjectEnergy(new FluidStack(EU, ValuedReceive), SIMULATE) - ValuedReceive;
					if (Diff == 0) {
						this.getEnergyStorage(EU, side).modifyEnergyStored(-ValuedReceive);
						InjectEnergy(new FluidStack(EU, ValuedReceive + Diff), MODULATE);

					}
				}
				if (this.getEnergyStorage(J, side).getEnergyStored() > 0) {
					int ValuedReceive = Math.min(getEnergyStorage(J, side).getEnergyStored(), capacity);

					int Diff = InjectEnergy(new FluidStack(J, ValuedReceive), SIMULATE) - ValuedReceive;
					if (Diff == 0) {
						this.getEnergyStorage(J, side).modifyEnergyStored(-ValuedReceive);
						InjectEnergy(new FluidStack(J, ValuedReceive + Diff), MODULATE);

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

					int Diff = ExtractEnergy(new FluidStack(RF, ValuedExtract), SIMULATE) + ValuedExtract;
					this.getEnergyStorage(RF,side).modifyEnergyStored(ValuedExtract);
					ExtractEnergy(new FluidStack(RF, ValuedExtract - Diff), MODULATE);
				}
				if (this.getEnergyStorage(EU,side).getEnergyStored() > 0) {

					int ValuedExtract = Math.min(1000,capacity);

					int Diff = ExtractEnergy(new FluidStack(EU, ValuedExtract), SIMULATE) + ValuedExtract;
					this.getEnergyStorage(EU,side).modifyEnergyStored(ValuedExtract);
					ExtractEnergy(new FluidStack(EU, ValuedExtract - Diff), MODULATE);
				}
				if (this.getEnergyStorage(J,side).getEnergyStored() > 0) {

					int ValuedExtract = Math.min(1000,capacity);

					int Diff = ExtractEnergy(new FluidStack(J, ValuedExtract), SIMULATE) + ValuedExtract;
					this.getEnergyStorage(J,side).modifyEnergyStored(ValuedExtract);
					ExtractEnergy(new FluidStack(J, ValuedExtract - Diff), MODULATE);
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

	/**
	 * RedstoneFluxAPI:
	 */

	private EnergyInterfaceStorage getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
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

	public void addListener( final ContainerEnergyInterface container )
	{
		if(!this.LinkedListeners.contains(container)){
			this.LinkedListeners.add(container);
		}
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

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T)slotInventory : super.getCapability(capability, facing);
		else
			return capability == CapabilityEnergy.ENERGY ? (T)getEnergyStorage(RF, AEPartLocation.fromFacing(facing)) : super.getCapability(capability, facing);
	}

	public void onActivate(EntityPlayer player) {
		for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			AILog.chatLog("Side: " + side.name());
			AILog.chatLog("Stored: " + getEnergyStorage(RF, side).getEnergyStored() + " RF / " + getEnergyStorage(RF, side).getMaxEnergyStored() + " RF", player);
			AILog.chatLog("Stored: " + getEnergyStorage(EU, side).getEnergyStored() + " EU / " + getEnergyStorage(EU, side).getMaxEnergyStored() + " EU", player);
			AILog.chatLog("Stored: " + getEnergyStorage(J, side).getEnergyStored() + " J / " + getEnergyStorage(J, side).getMaxEnergyStored() + " J", player);
		}
	}
}

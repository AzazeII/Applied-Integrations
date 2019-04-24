package AppliedIntegrations.tile;

import AppliedIntegrations.api.*;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Gui.Part.GuiEnergyInterface;
import AppliedIntegrations.Helpers.IntegrationsHelper;
import AppliedIntegrations.Helpers.InterfaceDuality;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketProgressBar;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
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

import static AppliedIntegrations.api.Storage.LiquidAIEnergy.*;
import static AppliedIntegrations.grid.Implementation.AIEnergy.*;
import static appeng.api.config.Actionable.SIMULATE;

/**
 * @Author Azazell
 */
public class TileEnergyInterface extends AITile implements IEnergyMachine,
		INetworkToolAgent,IEnergyInterface,IStorageMonitorable,IInventoryHost {

	private static final boolean DualityMode = true;
	private Boolean energyStates[] = new Boolean[6];

	private LinkedHashMap<AEPartLocation, EnergyInterfaceStorage> RFStorage = new LinkedHashMap<>();
	private LinkedHashMap<AEPartLocation, EnergyInterfaceStorage> EUStorage = new LinkedHashMap<>();
	private LinkedHashMap<AEPartLocation, JouleInterfaceStorage> JOStorage = new LinkedHashMap<>();
	private LinkedHashMap<AEPartLocation, EmberInterfaceStorageDuality> EmberStorage = new LinkedHashMap<>();

	private EnergyInterfaceStorage Storage = new EnergyInterfaceStorage(this, capacity, capacity/2);

	private InterfaceDuality duality = new InterfaceDuality(this);

	public static int capacity = 100000;

	private List<ContainerEnergyInterface> LinkedListeners = new ArrayList<ContainerEnergyInterface>();

	byte outputTracker;
	public boolean EnergyStates[] = new boolean[6];

	private boolean EUloaded = false;

	private AIGridNodeInventory slotInventory = new AIGridNodeInventory("slot.inventory",9,1,this);

	public TileEnergyInterface() {
		this.energyStates[1] = true;
		for(AEPartLocation dir : AEPartLocation.SIDE_LOCATIONS){
			if(IntegrationsHelper.instance.isLoaded(RF))
				RFStorage.put(dir,new EnergyInterfaceStorage(this, capacity,capacity/2));
			if(IntegrationsHelper.instance.isLoaded(EU))
				EUStorage.put(dir,new EnergyInterfaceStorage(this, (int)(capacity*0.25), capacity*2));
			if(IntegrationsHelper.instance.isLoaded(J))
				JOStorage.put(dir,new JouleInterfaceStorage(this, capacity*2));
			if(IntegrationsHelper.instance.isLoaded(Ember))
				EmberStorage.put(dir, new EmberInterfaceStorageDuality());
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
			//MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (world != null && !world.isRemote) {
			//MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
		}

	}

	private void notifyListenersOfEnergyBarChange(LiquidAIEnergy Energy, int id, AEPartLocation side){
		for(ContainerEnergyInterface listener : this.LinkedListeners){
			if(listener!=null) {
				 NetworkHandler.sendTo(new PacketProgressBar(this), (EntityPlayerMP)listener.player);
			}
		}
	}
	@Override
	public void update() {
		super.update();
		if (!EUloaded && hasWorld() && !world.isRemote) {
			EUloaded = true;
			//MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
		}

		for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			notifyListenersOfEnergyBarChange(RF, 0, side);
			notifyListenersOfEnergyBarChange(EU, 1, side);
			notifyListenersOfEnergyBarChange(J, 2, side);
		}

		try {
			if (DualityMode) {
				DoInjectDualityWork(Actionable.MODULATE);
			} else {
				DoInjectDualityWork(SIMULATE);
			}
		}catch (NullNodeConnectionException error){

		}
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
		duality.DoInjectDualityWork(action);
	}
	@Override
	public void DoExtractDualityWork(Actionable action) throws NullNodeConnectionException {
		duality.DoExtractDualityWork(action);
	}

	@Override
	public double getMaxTransfer(AEPartLocation side) {
		return capacity/(double)2;
	}

	@Override
	public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {
		return null;
	}

	/**
	 * RedstoneFluxAPI:
	 */

	public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
		if(energy == RF){
			return this.RFStorage.get(side);
		}else if(energy == EU){
			return this.EUStorage.get(side);
		}else if(energy == J){
			return this.JOStorage.get(side);
		}else if(energy == Ember){
			return this.EmberStorage.get(side);
		}
		return null;
	}

	public void addListener( final ContainerEnergyInterface container )
	{
		if(!this.LinkedListeners.contains(container)){
			this.LinkedListeners.add(container);
		}
	}

	@Override
	public LiquidAIEnergy getCurrentBar(AEPartLocation side) {
		return null;
	}

	@Override
	public TileEntity getFacingTile(EnumFacing side) {
		return null;
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
		// Cache of net
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		// fluidInventory of cache
		return storage.getInventory(iStorageChannel);
	}

	@Override
	public boolean showNetworkInfo(RayTraceResult rayTraceResult) {
		return false;
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

	public void onActivate(EntityPlayer player, AEPartLocation side) {
		AILog.chatLog("Stored: " + getEnergyStorage(RF, side).getStored() + " RF / " + getEnergyStorage(RF, side).getMaxStored() + " RF", player);
		AILog.chatLog("Stored: " + getEnergyStorage(EU, side).getStored() + " EU / " + getEnergyStorage(EU, side).getMaxStored() + " EU", player);
		AILog.chatLog("Stored: " + getEnergyStorage(J, side).getStored() + " J / " + getEnergyStorage(J, side).getMaxStored() + " J", player);
	}
}

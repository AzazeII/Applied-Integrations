package AppliedIntegrations.Parts.EnergyInterface;

import AppliedIntegrations.API.*;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Network.Packets.PacketBarChange;
import AppliedIntegrations.Network.Packets.PacketProgressBar;
import AppliedIntegrations.Network.Packets.PacketServerFilter;
import AppliedIntegrations.Parts.PartEnum;

import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Parts.IAEAppEngInventory;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Parts.InvOperation;
import AppliedIntegrations.Render.TextureManager;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Network.NetworkHandler;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkPowerStorage;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEFluidStack;

import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.IPriorityHost;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.SideOnly;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.tile.IEnergySink;

import mekanism.api.energy.IStrictEnergyAcceptor;

import net.minecraft.client.renderer.RenderBlocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

import static AppliedIntegrations.API.LiquidAIEnergy.*;
import static appeng.api.networking.ticking.TickRateModulation.IDLE;
import static cpw.mods.fml.relauncher.Side.CLIENT;

/**
 * @Author Azazell
 */
@Optional.InterfaceList(value = { //0_lol
		@Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent",modid = "IC2",striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink",modid = "IC2",striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage",modid = "Mekanism",striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true),
		@Optional.Interface(iface = "cofh.api.energy.EnergyStorage",modid = "CoFHAPI",striprefs = true),
		@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver",modid = "CoFHAPI",striprefs = true),
		@Optional.Interface(iface = "Reika.RotaryCraft.API.Interfaces.Transducerable",modid = "RotaryCraft",striprefs = true),
		@Optional.Interface(iface = "Reika.RotaryCraft.API.Power.AdvancedShaftPowerReceiver",modid = "RotaryCraft",striprefs = true),
		@Optional.Interface(iface = "mcp.mobius.waila.api.*", modid = "Waila",striprefs = true),
		@Optional.Interface(iface = " com.cout970.magneticraft.api.*",modid = "MagneticCraft"),
		@Optional.Interface(iface = "com.cout970.magneticraft.api.heat.IHeatTile", modid = "Magneticraft",striprefs = true),
		@Optional.Interface(iface = "com.cout970.magneticraft.api.heat.prefab.*",modid = "Magneticraft",striprefs = true)}

)
public class PartEnergyInterface
		extends AIPart
		implements IEnergyDuality,IInventory,IEnergyInterface,IEnergyReceiver,
		ITileStorageMonitorable, IInventoryHost,IEnergyMachine,IAEAppEngInventory, IPriorityHost,IGridTickable,IStorageMonitorable,ICraftingProvider,IPowerChannelState {

	private static boolean EUloaded = false;
	public FlowMode flowMode = FlowMode.Gui;

	// Watts
	private int omega = 0;
	private int torque = 0;
	private long WattPower = 0;
	private int alpha = 0;
	private long currentPower = 0;

	// Heat units
	private int heatBuffer = 0;
	private int transmitHeat = 0;

	private int priority;
	private int CurrentEnergyTransfer = 1000;

	protected EnergyStorage RFStorage = new EnergyStorage(capacity,maxTransfer);
	protected EnergyStorage EUStorage = new EnergyStorage(capacity*4,maxTransfer);
	protected EnergyStorage JStorage = new EnergyStorage((int)(capacity*2.5),maxTransfer);

	// AE storage
	private double fluixStorage;

	public int oldHeat;

	// Gui Units
	private boolean redstoneControlled;
	private boolean onModuleEnergyChange;
	public LiquidAIEnergy bar;
	private double temperature;
	public boolean SyncMarked = false;
	private String realContainer;
	public int hash;

	public boolean canConnectEnergy(ForgeDirection from) {
		return from==this.getSide();
	}

	public double IDLE_POWER_DRAIN = 0.5D;

	public GuiEnergyInterface LinkedGui;

    DualityMode DualityMode;

	//Linked array of containers, that syncing this Machine with server
	private List<ContainerEnergyInterface> LinkedListeners = new ArrayList<ContainerEnergyInterface>();
	public LiquidAIEnergy FilteredEnergy = null;

	public PartEnergyInterface() {
		super(PartEnum.EnergyInterface, new SecurityPermissions[]{SecurityPermissions.INJECT, SecurityPermissions.EXTRACT});
	}

    // Registring Inventory for slots of upgrades
	private AIGridNodeInventory upgradeInventory = new AIGridNodeInventory("", 1, 1, this) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			if(!(itemstack == null))
				if (AEApi.instance().definitions().materials().cardRedstone().isSameAs(itemstack))
					return true;
			return false;
		}
	};
	// drops, when wrenched
	@Override
	public void getDrops(List<ItemStack> drops, boolean wrenched) {
		for (ItemStack stack : upgradeInventory.slots) {
			if (stack != null) {
				drops.add(stack);
			}
		}
	}

	@Override
	public AIGridNodeInventory getUpgradeInventory() {
		return upgradeInventory;
	}

	// 4/16 tiles of 1 block rendering cable
	@Override
	public int cableConnectionRenderTo() {
		return 4;
	}
	// hit boxes
	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
		bch.addBox(5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 14.0D);
	}

	@Override
	public IIcon getBreakingTexture() {
		return null;
	}

	@Override
	public double getIdlePowerUsage() {
		return IDLE_POWER_DRAIN;
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	// render part in inventory
	@Override
	@SideOnly(CLIENT)
	public void renderInventory(IPartRenderHelper rh, RenderBlocks renderer) {
		IIcon icon = TextureManager.ENERGY_INTERFACE.getTextures()[2];

		rh.setTexture(icon, icon, icon, TextureManager.ENERGY_INTERFACE.getTexture(), icon, icon);

		rh.setBounds(2.0F, 2.0F, 14.0F, 14.0F, 14.0F, 16.0F);
		rh.renderInventoryBox(renderer);

		rh.setBounds(5.0F, 5.0F, 12.0F, 11.0F, 11.0F, 13.0F);
		rh.renderInventoryBox(renderer);

		rh.setBounds(5.0F, 5.0F, 13.0F, 11.0F, 11.0F, 14.0F);
		rh.renderInventoryBox(renderer);
	}
	// render part in world
	@Override
	@SideOnly(CLIENT)
	public void renderStatic(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer) {
		IIcon icon = TextureManager.ENERGY_INTERFACE.getTextures()[2];
		IIcon aeIcon = TextureManager.BUS_AESIDEDBACK.getTexture(); // = TextureManager.SidedAETexture
		// Rendering main part
		rh.setTexture(icon, icon, TextureManager.BUS_BACK.getTexture(), TextureManager.ENERGY_INTERFACE.getTexture(), icon, icon);
		rh.setBounds(2.0F, 2.0F, 14.0F, 14.0F, 14.0F, 16.0F);
		rh.renderBlock(x, y, z, renderer);
		rh.setBounds(5.0F, 5.0F, 12.0F, 11.0F, 11.0F, 13.0F);
		rh.renderBlock(x, y, z, renderer);

		rh.setBounds(5.0F, 5.0F, 13.0F, 11.0F, 11.0F, 14.0F);
		rh.renderBlock(x, y, z, renderer);

	}

	public void setEnergyStored(int energy){

		this.RFStorage.setEnergyStored(energy);
	}
	@Override
	public boolean onActivate(EntityPlayer player, Vec3 position) {
		if(this.getHostTile().getWorldObj().isRemote == false){
			this.LinkedGui = (GuiEnergyInterface)this.getClientGuiElement(player);
			player.openGui(AppliedIntegrations.instance, 1, this.getHostTile().getWorldObj(), this.getHostTile().xCoord, this.getHostTile().yCoord, this.getHostTile().zCoord);

		}
		return true;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r)
	{
		if(this.getHostTile().getWorldObj()!=null && !this.getHostTile().getWorldObj().isRemote && !EUloaded) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			EUloaded = true;
		}
	}


	@Override
	public Object getClientGuiElement(EntityPlayer player)
	{
		return new GuiEnergyInterface((ContainerEnergyInterface) getServerGuiElement(player),
				this, this.getX(),this.getY(),this.getZ(),this.getSide(), player );
	}
	@Override
	public Object getServerGuiElement(EntityPlayer player)
	{
		return new ContainerEnergyInterface(player, this);
	}


	@Override
	public void onInventoryChanged() {
		for (int i = 0; i < this.upgradeInventory.getSizeInventory(); i++) {
			ItemStack currentStack = this.upgradeInventory.getStackInSlot(i);
			if (currentStack != null) {
				if (AEApi.instance().definitions().materials().cardRedstone().isSameAs(currentStack))
					this.redstoneControlled = true;
			}
		}
	}
	// Duality Inject

	/**
	 *
	 * @param node
	 * GridNode for manipulation with storage grid
	 * @param resource
	 * Resource to be injected
	 * @param doFill
	 * Simulate or modulate?
	 * @return
	 * returns amount injected
	 */
	public int InjectEnergy(IGridNode node,FluidStack resource, boolean doFill) {
		IGrid grid = node.getGrid(); // check grid node
		if (grid == null) {
			AILog.info("Grid cannot be initialized, WTF?");
			return 0;
		}
		IStorageGrid storage = grid.getCache(IStorageGrid.class); // check storage gridnode
		if (storage == null && this.node.getGrid().getCache(IStorageGrid.class) == null) {
			AILog.info("StorageGrid cannot be initialized, WTF?");
			return 0;
		}
		IAEFluidStack returnAmount;
		if (doFill) { // Modulation
			returnAmount = storage.getFluidInventory().injectItems(
					AEApi.instance().storage().createFluidStack(resource),
					Actionable.MODULATE, new MachineSource(this));
		} else {//Simulation
			returnAmount = storage.getFluidInventory().injectItems(
					AEApi.instance().storage().createFluidStack(resource),
					Actionable.SIMULATE, new MachineSource(this));

		}

		if (returnAmount == null)
			return resource.amount;
		return (int) (resource.amount - returnAmount.getStackSize());
	}
	// Duality Extract
	public int ExtractEnergy(IGridNode node,FluidStack resource, boolean doFill) {
		if(node == null)
			return 0;
		IGrid grid = node.getGrid();
		if (grid == null) {
			AILog.info("Grid cannot be initialized, WTF?");
			return 0;
		}
		IStorageGrid storage = (IStorageGrid)grid.getCache(IStorageGrid.class);
		if (storage == null) {
			AILog.info("StorageGrid cannot be initialized, WTF?");
			return 0;
		}
		IAEFluidStack notRemoved;
		if (doFill) {
			notRemoved = (IAEFluidStack)storage.getFluidInventory().extractItems(AEApi.instance().storage().createFluidStack(resource), Actionable.MODULATE, new MachineSource(this));
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
	public int getPriority() {
		return this.priority;
	}

	@Override
	public void setPriority(int newValue) {
		this.priority = newValue;
	}

	/**
	 * Cooperate:
	 */
	public EnergyStorage getEnergyStorage(LiquidAIEnergy energy) {
		if(energy == RF) {
			return RFStorage;
		}
		if(energy == EU)
			return EUStorage;
		if(energy == J)
			return JStorage;
		if (energy == WA){
			return null;
		}
		return null;
	}

	public int multiExtract(ForgeDirection from, int maxTransfer, LiquidAIEnergy mode){
		TileEntity tile = this.getFacingTile();
		if(tile instanceof IEnergyReceiver && mode == RF) {
			IEnergyReceiver FacingRFStorage = (IEnergyReceiver) tile;
			FacingRFStorage.receiveEnergy(from,maxTransfer,false);
			return this.getEnergyStorage(mode).extractEnergy(maxTransfer, false);
		}else if(Loader.isModLoaded("IC2") && tile instanceof IEnergySink && mode == J) {
			IEnergySink FacingJStorage = (IEnergySink) tile;
			FacingJStorage.injectEnergy(from,maxTransfer,4);
			return this.getEnergyStorage(mode).extractEnergy(maxTransfer, false);
		} else if(tile instanceof IStrictEnergyAcceptor && mode == EU) {
			IStrictEnergyAcceptor FacingEUStorage = (IStrictEnergyAcceptor) tile;
			FacingEUStorage.transferEnergyToAcceptor(from,maxTransfer);
			return this.getEnergyStorage(mode).extractEnergy(maxTransfer, false);
		}
		return 0;
	}
	/**
	 * Redstone Flux Api:
	 */
	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		boolean shouldUpdate = false;
			if(this.FilteredEnergy == null || FilteredEnergy != RF || FilteredEnergy != J){
				if(getFacingTile() instanceof IStrictEnergyAcceptor && FilteredEnergy != J){
					int r = this.getEnergyStorage(J).receiveEnergy(maxReceive,simulate);
					return r;
				}else if(getFacingTile() instanceof IEnergyReceiver && FilteredEnergy != RF){
					int r = this.getEnergyStorage(RF).receiveEnergy(maxReceive,simulate);
					return r;
				}
			}
			return 0;
	}
	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
	{
		if(FilteredEnergy == RF) {
			return this.getEnergyStorage(RF).extractEnergy(maxExtract, simulate);
		}
		return 0;
	}
	@Override
	public int getEnergyStored(ForgeDirection from)
	{
		return getEnergyStorage(RF).getEnergyStored();
	}
	@Override
	public int getMaxEnergyStored(ForgeDirection from)
	{
		return getEnergyStorage(RF).getMaxEnergyStored();
	}
	/**
	 * Rotary Craft:
	 */
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
    public boolean addPower(int torque, int omega, long l, ForgeDirection forgeDirection) {
		if(this.bar == null || this.bar == WA ){
			this.WattPower = l;
			this.torque = torque;
			this.omega = omega;
			this.currentPower += l;
			return true;
		}
    	return false;
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
		return this.currentPower;
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
	 * AE2 power system
	 */
	@Override
	public double injectAEPower(double amt, Actionable mode) {
		if( mode == Actionable.SIMULATE )
		{
			final double SimulateCopy = this.fluixStorage + amt;
			if( SimulateCopy > this.capacity/2 )
			{
				return SimulateCopy - this.capacity/2;
			}

			return 0;
		}

		if( this.fluixStorage < 0.01 && amt > 0.01 )
		{
			this.getGridNode().getGrid().postEvent( new MENetworkPowerStorage( this, MENetworkPowerStorage.PowerEventType.PROVIDE_POWER ) );
		}

		this.fluixStorage += amt;
		if( this.fluixStorage > this.capacity/2 )
		{
			amt = this.fluixStorage - this.capacity/2;
			this.fluixStorage = this.capacity/2;

			return amt;
		}

		return 0;
	}

	@Override
	public double getAEMaxPower() {
		return this.fluixStorage;
	}

	@Override
	public double getAECurrentPower() {
		return this.capacity/2;
	}

	@Override
	public boolean isAEPublicPowerStorage() {
		return true;
	}

	@Override
	public AccessRestriction getPowerFlow() {
		return AccessRestriction.READ_WRITE;
	}
	@Override
	public double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
		return 0;//return usePowerMultiplier.divide(this.extractAEPower( usePowerMultiplier.multiply( amt ), mode ));
	}

	/* private double extractAEPower( double amt, final Actionable mode )
	{
		if( mode == Actionable.SIMULATE )
		{
			if( this.fluixStorage > amt )
			{
				return amt;
			}
			return this.fluixStorage;
		}

		final boolean wasFull = this.fluixStorage >= this.capacity/2 - 0.001;

		if( wasFull && amt > 0.001 )
		{
			try
			{
				this.getGridNode().getGrid().postEvent( new MENetworkPowerStorage( this, MENetworkPowerStorage.PowerEventType.REQUEST_POWER ) );
			}
			catch( final Exception ignored )
			{

			}
		}

		if( this.fluixStorage > amt )
		{
			this.fluixStorage -= amt;

			return amt;
		}

		amt = this.fluixStorage;
		this.fluixStorage = 0;

		return amt;
	}
	*/
	/**
	 * Mekanism:
	 */
	@Override
	public double transferEnergyToAcceptor(ForgeDirection side, double amount) {
		return 0;
	}


	@Override
	public boolean canReceiveEnergy(ForgeDirection side) {
		return side==getSide();
	}

	@Override
	public double getEnergy() {
		return this.getEnergyStorage(J).getEnergyStored();
	}

	@Override
	public void setEnergy(double energy) {
		this.getEnergyStorage(J).setEnergyStored((int)energy);
	}

	@Override
	public double getMaxEnergy() {
		return this.capacity*2.5;
	}
	/**
	 * Industrial Craft 2:
	 */
	@Override
	public double getDemandedEnergy() {
		return this.capacity*4-this.getEnergyStorage(EU).getEnergyStored();
	}

	@Override
	public int getSinkTier() {
		return 4;
	}

	@Override
	public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {
		if(this.bar == null || this.bar == EU) {
			if(this.FilteredEnergy != EU) {
				getEnergyStorage(EU).receiveEnergy((int) amount, false);
				return 0;
			}
		}
		return getEnergyStorage(EU).getEnergyStored();
	}

	@Override
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
		return true;
	}
	@Override
	public boolean emitsEnergyTo(TileEntity receiver, ForgeDirection direction) {
		return false;
	}

	@Override
	public double getOfferedEnergy() {
		return 0;
	}

	@Override
	public void drawEnergy(double amount) {
	//	if(EUStorage.getEnergyStored()>amount);
    //    EUStorage.modifyEnergyStored((int)-amount);
	}

	@Override
	public int getSourceTier() {
		return 4;
	}

	@Override
	public void saveChanges() {
		// Ensure there is a host
		if( this.host != null )
		{
			// Mark
			this.host.markForSave();
		}
	}

	@Override
	public void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removedStack, ItemStack newStack) {

	}
	@Override
	public void readFromNBT( final NBTTagCompound data )
	{
		for(LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			if(getEnergyStorage(energy) != null)
				this.getEnergyStorage(energy).setEnergyStored(data.getInteger("#EnergyTag" + energy.getEnergyName()));
		}
	}
	@Override
	public void writeToNBT( final NBTTagCompound data, final PartItemStack saveType )
	{
		for(LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			if (getEnergyStorage(energy) != null)
				data.setInteger("#EnergyTag" + energy.getEnergyName(), this.getEnergyStorage(energy).getEnergyStored());
		}
	}

	private void notifyListenersOfFilterEnergyChange()
	{
		for( ContainerEnergyInterface listener : this.LinkedListeners)
		{
			if(listener!=null) {
				NetworkHandler.sendToServer(new PacketServerFilter(this.FilteredEnergy,0,this.getX(),this.getY(),this.getZ()
						,this.getSide(),this.getHostTile().getWorldObj()));
			}
		}
	}
	// Synchronize data with all listeners
	private void notifyListenersOfEnergyBarChange(LiquidAIEnergy Energy){
		for(ContainerEnergyInterface listener : this.LinkedListeners){
			if(listener!=null) {
				if(this.getHostTile() != null) {
					NetworkHandler.sendTo(new PacketProgressBar(this,getX(),getY(),getZ(),getSide(),this.getHostTile().getWorldObj()), (EntityPlayerMP) listener.player);
				}
			}
		}
	}
	private void notifyListenersOfBarFilterChange(LiquidAIEnergy bar){
		for(ContainerEnergyInterface listener : this.LinkedListeners){
			if(listener!=null) {
				NetworkHandler.sendTo(new PacketBarChange(bar,getX(),getY(),getZ(),getSide(),this.getHostTile().getWorldObj()),(EntityPlayerMP)listener.player);
			}
		}
	}
	public void removeListener( final ContainerEnergyInterface container )
	{
		this.LinkedListeners.remove( container );
	}
	public void addListener( final ContainerEnergyInterface container )
	{
		if(!this.LinkedListeners.contains(container)){
			this.LinkedListeners.add(container);
		}
	}
	@Override
	public final void updateFilter( final LiquidAIEnergy energy,final int index)
	{
		// Set the filter
		this.FilteredEnergy = energy;
	}

	/**
	 * Ticking Request:
	 */
	@Override
	public TickingRequest getTickingRequest( final IGridNode node )
	{
		return new TickingRequest( 1, 1, false, false );
	}

	@Override
	public TickRateModulation tickingRequest( final IGridNode node, final int TicksSinceLastCall )
	{
			try {
				if (this.isActive()) {
						DoInjectDualityWork(Actionable.MODULATE);
						DoExtractDualityWork(Actionable.MODULATE);
						if(FilteredEnergy!=null)
						AILog.info(this.FilteredEnergy.getEnergyName());
				}
			}catch (NullNodeConnectionException e) {
				AILog.error(e,"Node of PartEnergy Interface, when it's active could not be null");
			}
			//Syncing:
			notifyListenersOfFilterEnergyChange();
			// Energy Stored with GUi
			int i = 0;
			for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
				if (this.getEnergyStorage(energy) != null && this.getEnergyStorage(energy).getEnergyStored() > 0) {
					this.bar = energy;
					notifyListenersOfBarFilterChange(this.bar);
					i += 1;
				}
			}
			if (i == 0)
				this.bar = null;
			// Notify container and gui
				if (bar != null)
				// Bar Filter With Gui
			this.notifyListenersOfEnergyBarChange(this.bar);

			this.saveChanges();
		return IDLE;
	}
	public void setFlowMode(FlowMode mode){
		this.flowMode = mode;
	}
    @Override
    public List<String> getWailaBodey(NBTTagCompound tag, List<String> list) {
    	return null;
    }

    @Override
	public NBTTagCompound getWailaTag(NBTTagCompound tag) {
        tag.setInteger("amount",    this.getEnergyStored(ForgeDirection.UNKNOWN));
        return tag;
    }

    /**
     * Inject energy from facing tile, with mode #Link Action
     * @param action
     * @return
     */
    @Override
    public void DoInjectDualityWork(Actionable action) throws NullNodeConnectionException {


		IGridNode node = this.getGridNode();
		if (node == null) {
			throw new NullNodeConnectionException();
		}
		// Is it modulate, or matrix?
		if (action == Actionable.MODULATE) {
			// RF Api
			if (this.getEnergyStorage(RF).getEnergyStored() > 0 && this.getEnergyStorage(J).getEnergyStored() == 0 && this.FilteredEnergy != RF) {

				int ValuedReceive = Math.min(this.getEnergyStorage(RF).getEnergyStored(), this.maxTransfer);
				int Diff = InjectEnergy(node, new FluidStack(RF, ValuedReceive), false) - ValuedReceive;
				if (Diff == 0) {
					int amountToReflect = InjectEnergy(node, new FluidStack(RF, ValuedReceive + Diff), true);
					// Insert only that amount, which network can inject
					this.getEnergyStorage(RF).modifyEnergyStored(-amountToReflect);
				}
			}
			// IC2
			if (this.getEnergyStorage(EU).getEnergyStored() > 0 && this.FilteredEnergy != EU) {

				int ValuedReceive = Math.min(this.getEnergyStorage(EU).getEnergyStored(), this.maxTransfer);
				int Diff = InjectEnergy(node, new FluidStack(EU, ValuedReceive), false) - ValuedReceive;
				if (Diff == 0) {
					// Insert only that amount, which network can inject
					ValuedReceive = Math.min(ValuedReceive,InjectEnergy(node, new FluidStack(EU, ValuedReceive + Diff), false));
					this.getEnergyStorage(EU).modifyEnergyStored(-InjectEnergy(node, new FluidStack(EU, ValuedReceive + Diff), true));
				}
			}
			// Mekanism
			if (this.getEnergyStorage(J).getEnergyStored() > 0 && this.FilteredEnergy != J) {

				int ValuedReceive = Math.min(this.getEnergyStorage(J).getEnergyStored(), this.maxTransfer);
				int Diff = InjectEnergy(node, new FluidStack(J, ValuedReceive), false) - ValuedReceive;
				if (Diff == 0) {
					// Insert only that amount, which network can inject
					ValuedReceive = Math.min(ValuedReceive,InjectEnergy(node, new FluidStack(J, ValuedReceive + Diff), false));
					this.getEnergyStorage(J).modifyEnergyStored(-InjectEnergy(node, new FluidStack(J, ValuedReceive + Diff), true));
				}
			}
			// Rotary Craft
			if (this.WattPower > 0 && this.FilteredEnergy != WA) {
				Long ValuedReceive = Math.min(WattPower, this.maxTransfer);
				// Energy Inject not supports Long
				if (this.WattPower < Integer.MAX_VALUE) {
					// But Storage in ae is still can be long
					int Diff = InjectEnergy(node, new FluidStack(WA, ValuedReceive.intValue()), false) - ValuedReceive.intValue();
					if (Diff == 0) {
						this.WattPower -= ValuedReceive;
						InjectEnergy(node, new FluidStack(WA, ValuedReceive.intValue()), true);
					}
				} else {
					// Then inject energy by fractions of WattPower / Integer.MaxValue
					for (float i = 0; i < WattPower / Integer.MAX_VALUE; i++) {
							if (WattPower / Integer.MAX_VALUE > 1){
								ValuedReceive = ValuedReceive;
							}else if ((WattPower / Integer.MAX_VALUE > 0 && WattPower / Integer.MAX_VALUE < 1)){
								ValuedReceive *= (WattPower / Integer.MAX_VALUE);
							}
							int Diff = InjectEnergy(node, new FluidStack(WA, ValuedReceive.intValue()), false) - ValuedReceive.intValue();
							if (Diff == 0) {
								this.WattPower -= ValuedReceive;
								InjectEnergy(node, new FluidStack(WA, ValuedReceive.intValue()), true);

							}

					}
				}

			}
		}
	}

	@Override
    public void DoExtractDualityWork(Actionable action) throws NullNodeConnectionException {
		IGridNode node = this.getGridNode();
		if (node == null) {
			throw new NullNodeConnectionException();
		}
		if(action == Actionable.MODULATE){
			if(FilteredEnergy != null) {
				int valuedReceive = Math.min(this.getEnergyStorage(FilteredEnergy).getEnergyStored(), this.maxTransfer);
				int diff =  valuedReceive - this.ExtractEnergy(node,new FluidStack(FilteredEnergy,valuedReceive),false) ;
				if(diff == 0) {
					this.getEnergyStorage(this.FilteredEnergy).modifyEnergyStored(this.ExtractEnergy(node, new FluidStack(FilteredEnergy, valuedReceive), true));
					transferEnergy(this.FilteredEnergy, valuedReceive);
				}
			}
		}
    }

	private void transferEnergy(LiquidAIEnergy filteredEnergy, int Amount) {
		if(filteredEnergy == RF){
			if(this.getFacingTile() instanceof IEnergyReceiver){
				IEnergyReceiver receiver = (IEnergyReceiver)this.getFacingTile();
				receiver.receiveEnergy(this.getSide(),Amount,false);
			}
		}else if(FilteredEnergy == EU){
			if(this.getFacingTile() instanceof IEnergySink){
				IEnergySink receiver = (IEnergySink)this.getFacingTile();
				receiver.injectEnergy(this.getSide(),(double)Amount,4);
			}
		}else if(FilteredEnergy == J){
			if(this.getFacingTile() instanceof IStrictEnergyAcceptor){
				IStrictEnergyAcceptor receiver = (IStrictEnergyAcceptor)this.getFacingTile();
				receiver.transferEnergyToAcceptor(this.getSide(),Amount);
			}
		}
    }


	public int getMaxEnergyStored(ForgeDirection unknown,@Nullable LiquidAIEnergy linkedMetric) {
    	if(linkedMetric == RF){
    		return this.capacity;
		}
		if(linkedMetric == EU){
			return this.capacity*4;
		}
		if(linkedMetric == J){
			return (int)(this.capacity*2.5);
		}
		if(linkedMetric == WA){
			return this.capacity*10;
		}
		if(linkedMetric == HU){
			return this.capacity*4;
		}
		if(linkedMetric == KU){
			return this.capacity*4;
		}
		if(linkedMetric == FZ){
			return this.capacity;
		}
		if(linkedMetric == AE){
    		return this.capacity/2;
		}
    	return 0;
	}

	public boolean isRedstoneControlled() {
		return this.redstoneControlled;
	}


	@Override
	public IStorageMonitorable getMonitorable(ForgeDirection side, BaseActionSource src) {
		return this;
	}

	@Override
	public void provideCrafting(ICraftingProviderHelper craftingTracker) {

	}

	@Override
	public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
		return false;
	}

	@Override
	public boolean isBusy() {
		return false;
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

	private AIGridNodeInventory slotInventory = new AIGridNodeInventory("slotInventory",9,1,this);

	/**
	 *
	 * @return
	 */
	@Override
	public int getSizeInventory() {
		return 9;
	}

	@Override
	public ItemStack getStackInSlot(int id) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {

	}

	@Override
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return false;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

	public LiquidAIEnergy getFilter(ForgeDirection unknown) {
		return this.FilteredEnergy;
	}

	public void setRealContainer(String realContainer) {
		this.realContainer=realContainer;
	}

	@Override
	public LiquidAIEnergy getFilter(int index) {
		return null;
	}
}



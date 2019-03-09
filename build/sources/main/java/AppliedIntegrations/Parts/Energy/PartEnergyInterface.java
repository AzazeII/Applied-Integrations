package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.*;
import AppliedIntegrations.API.Storage.EnumCapabilityType;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Gui.PartGui;
import AppliedIntegrations.Helpers.IntegrationsHelper;
import AppliedIntegrations.Helpers.InterfaceDuality;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketBarChange;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Network.Packets.PacketProgressBar;
import AppliedIntegrations.Network.Packets.PacketServerFilter;
import AppliedIntegrations.Parts.*;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartModel;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.core.sync.GuiBridge;
import appeng.helpers.IPriorityHost;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import teamroots.embers.block.BlockEmberEmitter;
import teamroots.embers.power.IEmberCapability;
import teamroots.embers.tileentity.TileEntityEmitter;
import teamroots.embers.tileentity.TileEntityReceiver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.*;
import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;
import static appeng.api.networking.ticking.TickRateModulation.IDLE;
import static appeng.api.util.AEPartLocation.INTERNAL;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

/**
 * @Author Azazell
 */
@net.minecraftforge.fml.common.Optional.InterfaceList(value = {
		@Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent",modid = "ic2",striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink",modid = "ic2",striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage",modid = "Mekanism",striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true),
		@Optional.Interface(iface = "mcp.mobius.waila.api.*", modid = "Waila",striprefs = true),
		@Optional.Interface(iface = "teamroots.embers.power.IEmberCapability", modid = "embers", striprefs = true),
		@Optional.Interface(iface = "teamroots.embers.power.DefaultEmberCapability", modid = "embers", striprefs = true)
}

)
public class PartEnergyInterface
		extends AIPart
		implements IInventory,
		IEnergyInterface, IInventoryHost,
		IEnergyMachine,IAEAppEngInventory, IPriorityHost,IGridTickable,
		IStorageMonitorable,ICraftingProvider,IPowerChannelState, IEnergySink {

	private static boolean EUloaded = false;

	// Watts
	private long WattPower = 0;

	private int priority;

	protected EmberInterfaceStorageDuality EmberStorage;
	protected EnergyInterfaceStorage RFStorage = new EnergyInterfaceStorage(this, capacity,maxTransfer);
	protected InterfaceSinkSource EUStorage;

    protected TeslaInterfaceStorageDuality TESLAStorage;
	protected JouleInterfaceStorage JStorage;

	// Interface duality, or interface host
	private InterfaceDuality duality = new InterfaceDuality(this);

    // AE storage
	private double fluixStorage;

	// Gui Units
	private boolean redstoneControlled;
	public LiquidAIEnergy bar;
	private boolean updateRequested;

    public boolean canConnectEnergy(AEPartLocation from) {
		return from==this.getSide();
	}

	public double IDLE_POWER_DRAIN = 0.5D;

	public GuiEnergyInterface LinkedGui;

	//Linked array of containers, that syncing this Machine with server
	private List<ContainerEnergyInterface> LinkedListeners = new ArrayList<ContainerEnergyInterface>();
	public LiquidAIEnergy FilteredEnergy = null;

	@Optional.Method(modid = "mekanism")
	private void initJStorage(){
		JStorage = new JouleInterfaceStorage(this, (int)(capacity*2.5));
	}

	@Optional.Method(modid = "embers")
	private void initEmberStorage(){
		EmberStorage = new EmberInterfaceStorageDuality();
		EmberStorage.setEmberCapacity(capacity*0.05); // Ember is really rich energy
		EmberStorage.setEmber(0.0D);
	}

	@Optional.Method(modid = "tesla")
	private void initTESLAStorage(){
		TESLAStorage = new TeslaInterfaceStorageDuality(this, (long)capacity, (long)maxTransfer);
	}

	// Make part available as "extendant(class to extend)S"
	// ** IMPORTANT FOR MANA INTERFACE **
	public PartEnergyInterface(PartEnum corespondingEnumPart, SecurityPermissions... permissions){
		super(corespondingEnumPart, permissions);
	}

	public PartEnergyInterface() {
		super(PartEnum.EnergyInterface, SecurityPermissions.INJECT, SecurityPermissions.EXTRACT);
		if(IntegrationsHelper.instance.isLoaded(J))
			initJStorage();
		if(IntegrationsHelper.instance.isLoaded(Ember))
			initEmberStorage();
		if(IntegrationsHelper.instance.isLoaded(TESLA))
			initTESLAStorage();
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
	public float getCableConnectionLength(AECableType aeCableType) {
		return 2;
	}

	@Override
	public AIGridNodeInventory getUpgradeInventory() {
		return upgradeInventory;
	}

	// hit boxes
	@Override
	public void getBoxes(IPartCollisionHelper bch) {
		bch.addBox(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
		bch.addBox(5.0D, 5.0D, 12.0D, 11.0D, 11.0D, 14.0D);
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered())
			if (this.isActive())
				return PartModelEnum.STORAGE_INTERFACE_HAS_CHANNEL;
			else
				return PartModelEnum.STORAGE_INTERFACE_ON;
		return PartModelEnum.STORAGE_INTERFACE_OFF;
	}

	@Override
	public double getIdlePowerUsage() {
		return IDLE_POWER_DRAIN;
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public void onEntityCollision(Entity entity) {

	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Call only on server
		if(getLogicalSide() == SERVER) {
			if(!player.isSneaking()) {
				// Link gui
				this.LinkedGui = (GuiEnergyInterface) this.getClientGuiElement(player);

				// Open GUI
				player.openGui(AppliedIntegrations.instance, 1, this.getHostTile().getWorld(), this.getHostTile().
						getPos().getX(), this.getHostTile().getPos().getY(), this.getHostTile().getPos().getZ());
				// Request gui update
				updateRequested = true;

			}else{
				duality.debug = true;

				AILog.chatLog("Stored: " + getEnergyStorage(RF, INTERNAL).getStored());
			}
		}
		return true;
	}


	@Override
	public void removeFromWorld()
	{
		super.removeFromWorld();
		if(IntegrationsHelper.instance.isLoaded(EU))
			this.invalidateSinkSource();
	}

	@Override
	public void addToWorld()
	{
		super.addToWorld();
		if(IntegrationsHelper.instance.isLoaded(EU))
			this.updateSinkSource();
	}

	private void updateSinkSource()
	{
		if( getEnergyStorage(EU, INTERNAL) == null )
		{
			EUStorage = new InterfaceSinkSource( this.getHost().getTile().getWorld(), this.getHost().getLocation().getPos(), getMaxEnergyStored(
					null, EU
			), 4, 4 );
		}

		((InterfaceSinkSource)getEnergyStorage(EU, INTERNAL)).update();
	}

	private void invalidateSinkSource()
	{
		if( getEnergyStorage(EU, INTERNAL) != null )
		{
			((InterfaceSinkSource)getEnergyStorage(EU, INTERNAL)).invalidate();
		}
	}

	@Override
	public Object getClientGuiElement(EntityPlayer player)
	{
		return new GuiEnergyInterface((ContainerEnergyInterface) getServerGuiElement(player), this, player);
	}
	@Override
	public Object getServerGuiElement(EntityPlayer player) {
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
		return null;
	}

	@Override
	public GuiBridge getGuiBridge() {
		return null;
	}

	/**
	 * Cooperate:
	 */
	public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
		if(energy == RF)
			return RFStorage;
		if(energy == EU)
			return EUStorage;
		if(energy == J)
			return JStorage;
		if (energy == Ember)
			return EmberStorage;
		if (energy == TESLA)
		    return TESLAStorage;
		return null;
	}

	@Override
	public boolean hasCapability( @Nonnull Capability<?> capability ) {
		return duality.hasCapability(capability);
	}

	@Nullable
	@Override
	public <T> T getCapability( @Nonnull Capability<T> capability )
	{
		return duality.getCapability(capability, INTERNAL);
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

	/**
	 *  marks GUI, as gui of THIS machine
	 */
	private void initGuiCoordinates(){
		for( ContainerEnergyInterface listener : this.LinkedListeners){
			if(listener!=null) {
				NetworkHandler.sendTo(new PacketCoordinateInit(getX(),getY(),getZ(),getHostTile().getWorld(),getSide().getFacing()),
						(EntityPlayerMP)listener.player);
				updateRequested = false;
			}
		}
	}
	private void notifyListenersOfFilterEnergyChange()
	{
		for( ContainerEnergyInterface listener : this.LinkedListeners)
		{
			if(listener!=null) {
				NetworkHandler.sendTo(new PacketServerFilter(this.FilteredEnergy,0,this.getX(),this.getY(),this.getZ()
						,this.getSide().getFacing(),this.getHostTile().getWorld()), (EntityPlayerMP)listener.player);
			}
		}
	}
	// Synchronize data with all listeners
	private void notifyListenersOfEnergyBarChange(LiquidAIEnergy Energy){
		for(ContainerEnergyInterface listener : this.LinkedListeners){
			if(listener!=null) {
				if(this.getHostTile() != null) {
					NetworkHandler.sendTo(new PacketProgressBar(this,getX(),getY(),getZ(),getSide().getFacing(),this.getHostTile().getWorld()), (EntityPlayerMP) listener.player);
				}
			}
		}
	}
	private void notifyListenersOfBarFilterChange(LiquidAIEnergy bar){
		for(ContainerEnergyInterface listener : this.LinkedListeners){
			if(listener!=null) {
				NetworkHandler.sendTo(new PacketBarChange(bar,getX(),getY(),getZ(),getSide().getFacing(),this.getHostTile().getWorld()),(EntityPlayerMP)listener.player);
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

		if(updateRequested){
			// Check if we have gui to update
			if(Minecraft.getMinecraft().currentScreen instanceof PartGui)
				this.initGuiCoordinates();
		}

		try {
			if (this.isActive()) {
					DoInjectDualityWork(Actionable.MODULATE);
					DoExtractDualityWork(Actionable.MODULATE);
			}
		}catch (NullNodeConnectionException e) {
			AILog.error(e,"Node of PartEnergy Interface, when it's active could not be null.. But it is");
		}

		/** Manually take ember energy, as receptor code is broken:
         * attachedTile.hasCapability(EmbersCapabilities.EMBER_CAPABILITY, **null**))
         * IEmberCapability cap = attachedTile.getCapability(EmbersCapabilities.EMBER_CAPABILITY, **null**);
         */
		if(IntegrationsHelper.instance.isLoaded(Ember)) {
			if (getFacingTile() instanceof TileEntityReceiver) {
				TileEntityReceiver emberReceptor = (TileEntityReceiver) getFacingTile();
				IBlockState state = getHostTile().getWorld().getBlockState(emberReceptor.getPos());

				// Check if facing is correct
				if (state.getValue(BlockEmberEmitter.facing) == this.getSide().getFacing()) {
					IEmberCapability emberStorage = emberReceptor.capability;

					if (emberStorage.getEmber() > 0) {
						emberStorage.removeAmount((Double)getEnergyStorage(Ember, INTERNAL).receive((int) emberStorage.getEmber(), false), true);
					}
				}
			} else if (getFacingTile() instanceof TileEntityEmitter) {
				TileEntityEmitter emberEmitter = (TileEntityEmitter) getFacingTile();
				IBlockState state = getHostTile().getWorld().getBlockState(emberEmitter.getPos());

				// Check if facing is correct
				if (state.getValue(BlockEmberEmitter.facing) == this.getSide().getFacing()) {
					IEmberCapability emberStorage = emberEmitter.capability;

					if (((EmberInterfaceStorageDuality) getEnergyStorage(Ember, INTERNAL)).getEmber() > 0) {
						getEnergyStorage(Ember, INTERNAL).extract((int) emberStorage.addAmount((Double)getEnergyStorage(Ember, INTERNAL).getStored(), true), false);
					}
				}
			}
		}

		//Syncing:
		notifyListenersOfFilterEnergyChange();
		// Energy Stored with GUi
		int i = 0;
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			if(getEnergyStorage(energy, INTERNAL) != null) {
				Class type = getEnergyStorage(energy, INTERNAL).getTypeClass();
				if (this.getEnergyStorage(energy, INTERNAL) != null && ((Number) getEnergyStorage(energy, INTERNAL).getStored()).doubleValue() > 0) {
					this.bar = energy;
					notifyListenersOfBarFilterChange(this.bar);
					i += 1;
				}
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

	@Override
	public double getMaxTransfer(AEPartLocation side) {
		return maxTransfer;
	}

	@Override
	public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {
		return FilteredEnergy;
	}

	/**
     * Inject energy from facing tile, with mode #Link Action
     * @param action
     * @return
     */
    @Override
    public void DoInjectDualityWork(Actionable action) throws NullNodeConnectionException {
		getDuality().DoInjectDualityWork(action);
	}

	@Override
    public void DoExtractDualityWork(Actionable action) throws NullNodeConnectionException {
    	getDuality().DoExtractDualityWork(action);
    }

	public IInterfaceDuality getDuality() {
		return this.duality;
	}


	public int getMaxEnergyStored(EnumFacing unknown,@Nullable LiquidAIEnergy linkedMetric) {
    	if(linkedMetric == RF){
    		return this.capacity;
		}
		if(linkedMetric == EU){
			return (int)(this.capacity*0.25);
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
	public void readFromNBT(NBTTagCompound tag) {
		if(IntegrationsHelper.instance.isLoaded(Ember))
			((EmberInterfaceStorageDuality)this.getEnergyStorage(Ember, INTERNAL)).writeToNBT(tag);
	}
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		if(IntegrationsHelper.instance.isLoaded(Ember))
			((EmberInterfaceStorageDuality)this.getEnergyStorage(Ember, INTERNAL)).readFromNBT(tag);
	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
		// Getting Node
		if (getGridNode(INTERNAL) == null)
			return null;
		// Getting net of node
		IGrid grid = getGridNode(INTERNAL).getGrid();
		if (grid == null)
			return null;
		// Cache of net
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		if (storage == null)
			return null;
		// fluidInventory of cache
		return storage.getInventory(channel);
	}

	/**
	 * @return;
	 */
	@Override
	public int getSizeInventory() {
		return 9;
	}

	@Override
	public boolean isEmpty() {
		return false;
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
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {

	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public void markDirty() {

	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	public LiquidAIEnergy getFilter(EnumFacing unknown) {
		return this.FilteredEnergy;
	}

	public void setRealContainer(String realContainer) { }

	@Override
	public LiquidAIEnergy getFilter(int index) {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Optional.Method(modid = "ic2")
	@Override
	public double getDemandedEnergy() {
		return (Double)getEnergyStorage(EU, INTERNAL).getMaxStored() - (Double)getEnergyStorage(EU, INTERNAL).getStored();
	}

	@Optional.Method(modid = "ic2")
	@Override
	public int getSinkTier() {
		return 4;
	}

	@Optional.Method(modid = "ic2")
	@Override
	public double injectEnergy(EnumFacing enumFacing, double v, double v1) {
		return (Double)getEnergyStorage(EU, INTERNAL).receive(v, false);
	}

	@Optional.Method(modid = "ic2")
	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		return true;
	}
}



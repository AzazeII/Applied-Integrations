package AppliedIntegrations.Parts.Energy;

import AppliedIntegrations.API.*;
import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.API.EmberInterfaceStorageDuality;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Gui.PartGui;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketBarChange;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Network.Packets.PacketProgressBar;
import AppliedIntegrations.Network.Packets.PacketServerFilter;
import AppliedIntegrations.Parts.*;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.SecurityPermissions;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkPowerStorage;
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
import appeng.capabilities.Capabilities;
import appeng.core.sync.GuiBridge;
import appeng.helpers.IPriorityHost;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import mekanism.api.energy.IStrictEnergyAcceptor;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import teamroots.embers.block.BlockEmberEmitter;
import teamroots.embers.power.EmberCapabilityProvider;
import teamroots.embers.power.IEmberCapability;
import teamroots.embers.tileentity.TileEntityEmitter;
import teamroots.embers.tileentity.TileEntityReceiver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static AppliedIntegrations.API.LiquidAIEnergy.*;
import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;
import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;
import static appeng.api.networking.ticking.TickRateModulation.IDLE;
import static net.minecraftforge.fml.relauncher.Side.SERVER;

/**
 * @Author Azazell
 */
@net.minecraftforge.fml.common.Optional.InterfaceList(value = { //0_lol
		@Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent",modid = "IC2",striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink",modid = "IC2",striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage",modid = "Mekanism",striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true),
		@Optional.Interface(iface = "cofh.api.energy.EnergyStorage",modid = "CoFHAPI",striprefs = true),
		@Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver",modid = "CoFHAPI",striprefs = true),
		@Optional.Interface(iface = "mcp.mobius.waila.api.*", modid = "Waila",striprefs = true),
		@Optional.Interface(iface = " com.cout970.magneticraft.api.*",modid = "MagneticCraft"),
		@Optional.Interface(iface = "com.cout970.magneticraft.api.heat.IHeatTile", modid = "Magneticraft",striprefs = true),
		@Optional.Interface(iface = "com.cout970.magneticraft.api.heat.prefab.*",modid = "Magneticraft",striprefs = true),
		@Optional.Interface(iface = "teamroots.embers.power.IEmberCapability", modid = "embers", striprefs = true),
		@Optional.Interface(iface = "teamroots.embers.power.DefaultEmberCapability", modid = "embers", striprefs = true)
}

)
public class PartEnergyInterface
		extends AIPart
		implements IEnergyDuality,IInventory,
		IEnergyInterface, IInventoryHost,
		IEnergyMachine,IAEAppEngInventory, IPriorityHost,IGridTickable,
		IStorageMonitorable,ICraftingProvider,IPowerChannelState {

	private static boolean EUloaded = false;

	// Watts
	private long WattPower = 0;

	private int priority;

	protected EmberInterfaceStorageDuality EmberStorage = new EmberInterfaceStorageDuality();
	protected EnergyInterfaceStorage RFStorage = new EnergyInterfaceStorage(this, capacity,maxTransfer);
	protected EnergyInterfaceStorage EUStorage = new EnergyInterfaceStorage(this,capacity*4,maxTransfer);
	protected EnergyInterfaceStorage JStorage = new EnergyInterfaceStorage(this, (int)(capacity*2.5),maxTransfer);
    protected EnergyInterfaceStorage TESLAStorage = new EnergyInterfaceStorage(this, capacity, maxTransfer);

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

	public PartEnergyInterface() {
		super(PartEnum.EnergyInterface, SecurityPermissions.INJECT, SecurityPermissions.EXTRACT);
		EmberStorage.setEmberCapacity(capacity*0.05); // Ember is really rich energy
		EmberStorage.setEmber(0.0D);

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

				// TODO: 2019-02-19 FIX GUI open crash
				// Open GUI
				//player.openGui(AppliedIntegrations.instance, 1, this.getHostTile().getWorld(), this.getHostTile().
				//		getPos().getX(), this.getHostTile().getPos().getY(), this.getHostTile().getPos().getZ());

				// Request gui update
				updateRequested = true;
				AILog.chatLog(getFacingTile().toString());
			}else{
				AILog.chatLog("Stored: " + getEnergyStorage(RF).getStored() + " RF / " + getEnergyStorage(RF).getMaxStored() + " RF", player);
				AILog.chatLog("Stored: " + getEnergyStorage(EU).getStored() + " EU / " + getEnergyStorage(EU).getMaxStored() + " EU", player);
				AILog.chatLog("Stored: " + getEnergyStorage(J).getStored() + " J / " + getEnergyStorage(J).getMaxStored() + " J", player);
				AILog.chatLog("Stored: " + getEnergyStorage(Ember).getStored() + " Ember / " + getEnergyStorage(Ember).getMaxStored() + " Ember", player);
			}
		}
		return true;
	}

	@Override
	public void randomDisplayTick(World world, BlockPos blockPos, Random random) {
		if(this.getHostTile().getWorld()!=null && !this.getHostTile().getWorld().isRemote && !EUloaded) {
			MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
			EUloaded = true;
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
	public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy) {
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
	public boolean hasCapability( @Nonnull Capability<?> capability )
	{
		// Register FE capability
		if( capability == Capabilities.FORGE_ENERGY ) {
			return true;
		}else if(capability == EmberCapabilityProvider.emberCapability){
			return true;
		}
		return super.hasCapability( capability );
	}

	@Nullable
	@Override
	public <T> T getCapability( @Nonnull Capability<T> capability )
	{
		// Register FE capability
		if( capability == Capabilities.FORGE_ENERGY ) {
			return (T) this.getEnergyStorage(RF);
		}else if(capability == EmberCapabilityProvider.emberCapability){
			return (T) this.getEnergyStorage(Ember);
		}
		return super.getCapability( capability );
	}



	/**
	 * AE2 power system
	 */
	@Override
	public double injectAEPower(double amt, Actionable mode) {
		if( mode == SIMULATE )
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

	@Override
	public boolean canReceiveEnergy(EnumFacing side) {
		return side==getSide().getFacing();
	}
	/**
	 * Industrial Craft 2:
	 */
	@Override
	public double getDemandedEnergy() {
		return this.capacity*4-this.getEnergyStorage(EU).getStored();
	}

	@Override
	public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
		if(this.bar == null || this.bar == EU) {
			if(this.FilteredEnergy != EU) {
				getEnergyStorage(EU).receive((int) amount, false);
				return 0;
			}
		}
		return getEnergyStorage(EU).getStored();
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
		// TODO: 2019-02-17 Integrations with Embers
		if(updateRequested){
			// Check if we have gui to update
			if(Minecraft.getMinecraft().currentScreen instanceof PartGui)
				this.initGuiCoordinates();
		}
		/*try {
			if (this.isActive()) {
					DoInjectDualityWork(Actionable.MODULATE);
					DoExtractDualityWork(Actionable.MODULATE);
			}
		}catch (NullNodeConnectionException e) {
			AILog.error(e,"Node of PartEnergy Interface, when it's active could not be null.. But it is");
		}*/

		/** Manually take ember energy, as receptor code is broken:
         * attachedTile.hasCapability(EmbersCapabilities.EMBER_CAPABILITY, **null**))
         * IEmberCapability cap = attachedTile.getCapability(EmbersCapabilities.EMBER_CAPABILITY, **null**);
         */

		if(getFacingTile() instanceof TileEntityReceiver){
		    TileEntityReceiver emberReceptor = (TileEntityReceiver) getFacingTile();
            IBlockState state = getHostTile().getWorld().getBlockState(emberReceptor.getPos());

            // Check if facing is correct
		    if(state.getValue(BlockEmberEmitter.facing) == this.getSide().getFacing()) {
                IEmberCapability emberStorage = emberReceptor.capability;

                if (emberStorage.getEmber() > 0) {
                    emberStorage.removeAmount(getEnergyStorage(Ember).receive((int) emberStorage.getEmber(), false), true);
                }
            }
        }else if(getFacingTile() instanceof TileEntityEmitter){
		    TileEntityEmitter emberEmitter = (TileEntityEmitter) getFacingTile();
		    IBlockState state = getHostTile().getWorld().getBlockState(emberEmitter.getPos());

		    // Check if facing is correct
		    if(state.getValue(BlockEmberEmitter.facing) == this.getSide().getFacing()){
                IEmberCapability emberStorage = emberEmitter.capability;

                if (((EmberInterfaceStorageDuality)getEnergyStorage(Ember)).getEmber() > 0) {
                    getEnergyStorage(Ember).extract((int)emberStorage.addAmount(getEnergyStorage(Ember).getStored(), true), false);
                }
            }
        }

		//Syncing:
		notifyListenersOfFilterEnergyChange();
		// Energy Stored with GUi
		int i = 0;
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			if (this.getEnergyStorage(energy) != null && getEnergyStorage(energy).getStored() > 0) {
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
			if (this.getEnergyStorage(RF).getStored() > 0 && this.getEnergyStorage(J).getStored() == 0 && this.FilteredEnergy != RF) {
                // We can cast double to int, as RFAPI only operates int-energy
				int ValuedReceive = (int)Math.min(this.getEnergyStorage(RF).getStored(), this.maxTransfer);
				int Diff = InjectEnergy(new FluidStack(RF, ValuedReceive), SIMULATE) - ValuedReceive;
				if (Diff == 0) {
					int amountToReflect = InjectEnergy(new FluidStack(RF, ValuedReceive + Diff), MODULATE);
					// Insert only that amount, which network can inject
					this.getEnergyStorage(RF).modifyEnergyStored(-amountToReflect);
				}
			}
			// IC2
			if (this.getEnergyStorage(EU).getStored() > 0 && this.FilteredEnergy != EU) {

				int ValuedReceive = (int)Math.min(this.getEnergyStorage(EU).getStored(), this.maxTransfer);
				int Diff = InjectEnergy(new FluidStack(EU, ValuedReceive), SIMULATE) - ValuedReceive;
				if (Diff == 0) {
					// Insert only that amount, which network can inject
					ValuedReceive = Math.min(ValuedReceive,InjectEnergy(new FluidStack(EU, ValuedReceive + Diff), SIMULATE));
					this.getEnergyStorage(EU).modifyEnergyStored(-InjectEnergy(new FluidStack(EU, ValuedReceive + Diff), MODULATE));
				}
			}
			// Mekanism
			if (this.getEnergyStorage(J).getStored() > 0 && this.FilteredEnergy != J) {

				int ValuedReceive = (int)Math.min(this.getEnergyStorage(J).getStored(), this.maxTransfer);
				int Diff = InjectEnergy(new FluidStack(J, ValuedReceive), SIMULATE) - ValuedReceive;
				if (Diff == 0) {
					// Insert only that amount, which network can inject
					ValuedReceive = Math.min(ValuedReceive,InjectEnergy(new FluidStack(J, ValuedReceive + Diff), SIMULATE));
					this.getEnergyStorage(J).modifyEnergyStored(-InjectEnergy(new FluidStack(J, ValuedReceive + Diff), MODULATE));
				}
			}
			// Rotary Craft Commented, until RC 1.12.2 will be released
			/*if (this.WattPower > 0 && this.FilteredEnergy != WA) {
				Long ValuedReceive = Math.min(WattPower, this.maxTransfer);
				// Energy Inject not supports Long
				if (this.WattPower < Integer.MAX_VALUE) {
					// But Storage in ae is still can be long
					int Diff = InjectEnergy(new FluidStack(WA, ValuedReceive.intValue()), SIMULATE) - ValuedReceive.intValue();
					if (Diff == 0) {
						this.WattPower -= ValuedReceive;
						InjectEnergy(new FluidStack(WA, ValuedReceive.intValue()), MODULATE);
					}
				} else {
					// Then inject energy by fractions of WattPower / Integer.MaxValue
					for (float i = 0; i < WattPower / Integer.MAX_VALUE; i++) {
							if (WattPower / Integer.MAX_VALUE > 1){
								ValuedReceive = ValuedReceive;
							}else if ((WattPower / Integer.MAX_VALUE > 0 && WattPower / Integer.MAX_VALUE < 1)){
								ValuedReceive *= (WattPower / Integer.MAX_VALUE);
							}
							int Diff = InjectEnergy(new FluidStack(WA, ValuedReceive.intValue()), SIMULATE) - ValuedReceive.intValue();
							if (Diff == 0) {
								this.WattPower -= ValuedReceive;
								InjectEnergy(new FluidStack(WA, ValuedReceive.intValue()), MODULATE);

							}

					}
				}

			}*/
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
				int valuedReceive = (int)Math.min(this.getEnergyStorage(FilteredEnergy).getStored(), this.maxTransfer);
				int diff =  valuedReceive - this.ExtractEnergy(new FluidStack(FilteredEnergy,valuedReceive),SIMULATE) ;
				if(diff == 0) {
					this.getEnergyStorage(this.FilteredEnergy).modifyEnergyStored(this.ExtractEnergy(new FluidStack(FilteredEnergy, valuedReceive), MODULATE));
					transferEnergy(this.FilteredEnergy, valuedReceive);
				}
			}
		}
    }

	private void transferEnergy(LiquidAIEnergy filteredEnergy, int Amount) {
		if(filteredEnergy == RF){
			if(this.getFacingTile().hasCapability(Capabilities.FORGE_ENERGY, getSide().getOpposite().getFacing())){
				IEnergyStorage capability = getFacingTile().getCapability(Capabilities.FORGE_ENERGY, getSide().getOpposite().getFacing());
				capability.receiveEnergy(Amount,false);
			}
		}else if(FilteredEnergy == EU){
			if(this.getFacingTile() instanceof IEnergySink){
				IEnergySink receiver = (IEnergySink)this.getFacingTile();
				receiver.injectEnergy(this.getSide().getFacing(),(double)Amount,4);
			}
		}else if(FilteredEnergy == J){
			if(this.getFacingTile() instanceof IStrictEnergyAcceptor){
				IStrictEnergyAcceptor receiver = (IStrictEnergyAcceptor)this.getFacingTile();
				receiver.acceptEnergy(this.getSide().getFacing(),Amount, false);
			}
		}
    }


	public int getMaxEnergyStored(EnumFacing unknown,@Nullable LiquidAIEnergy linkedMetric) {
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
		this.getEnergyStorage(Ember).writeToNBT(tag);
	}
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		this.getEnergyStorage(Ember).readFromNBT(tag);
	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
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
		return storage.getInventory(channel);
	}

	private AIGridNodeInventory slotInventory = new AIGridNodeInventory("slotInventory",9,1,this);

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
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		return false;
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
}



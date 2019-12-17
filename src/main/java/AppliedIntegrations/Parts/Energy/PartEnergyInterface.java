package AppliedIntegrations.Parts.Energy;
import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import AppliedIntegrations.Helpers.EnergyInterfaceDuality;
import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Parts.PartModelEnum;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.*;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
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
import appeng.me.GridAccessException;
import appeng.util.Platform;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static AppliedIntegrations.grid.Implementation.AIEnergy.*;
import static appeng.api.networking.ticking.TickRateModulation.IDLE;
import static appeng.api.parts.PartItemStack.BREAK;
import static appeng.api.util.AEPartLocation.INTERNAL;

/**
 * @Author Azazell
 */

@net.minecraftforge.fml.common.Optional.InterfaceList(value = {@Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent", modid = "ic2", striprefs = true),
		@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2", striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage", modid = "Mekanism", striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "Mekanism", striprefs = true),
		@Optional.Interface(iface = "mcp.mobius.waila.api.*", modid = "Waila", striprefs = true),
		@Optional.Interface(iface = "teamroots.embers.power.IEmberCapability", modid = "embers", striprefs = true),
		@Optional.Interface(iface = "teamroots.embers.power.DefaultEmberCapability", modid = "embers", striprefs = true)})
public class PartEnergyInterface extends AIPart implements IInventory, IEnergyInterface,
		IInventoryHost, IEnergyMachine, IPriorityHostExtended, IGridTickable, IStorageMonitorable,
		ICraftingProvider, IPowerChannelState, IEnergySink {
	public LiquidAIEnergy bar;
	public LiquidAIEnergy filteredEnergy = null;

	private int priority;
	private int capacity = AIConfig.interfaceMaxStorage;
	private int maxTransfer = 500000;

	private EnergyInterfaceStorage RFStorage;
	private InterfaceSinkSource EUStorage;
	private JouleInterfaceStorage JStorage;
	private TeslaInterfaceStorageDuality TESLAStorage;

	// Interface duality, or interface host
	private EnergyInterfaceDuality duality = new EnergyInterfaceDuality(this);
	// Linked array of containers, that syncing this Machine with server
	private List<ContainerEnergyInterface> linkedListeners = new ArrayList<ContainerEnergyInterface>();

	// Registring Inventory for slots of upgrades
	private AIGridNodeInventory upgradeInventory = new AIGridNodeInventory("", 1, 1, this) {
		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return AEApi.instance().definitions().materials().cardRedstone().isSameAs(itemstack);
		}
	};

	// Make host available as "extendant(class to extend)"
	// ** IMPORTANT FOR MANA INTERFACE **
	public PartEnergyInterface(PartEnum corespondingEnumPart) {
		super(corespondingEnumPart);
	}

	public PartEnergyInterface() {
		super(PartEnum.EnergyInterface);
		// Pass init to duality
		duality.initStorage(INTERNAL);
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

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		if (IntegrationsHelper.instance.isLoaded(EU, false)) {
			tag.setDouble("#EUEnergy", EUStorage.getStored());
		}
		if (IntegrationsHelper.instance.isLoaded(J, false)) {
			JStorage.writeToNBT(tag);
		}
		if (IntegrationsHelper.instance.isLoaded(TESLA, false)) {
			tag.setTag("#TeslaTag", TESLAStorage.serializeNBT());
		}
		RFStorage.writeToNBT(tag);
		if (filteredEnergy != null) {
			filteredEnergy.writeToNBT(tag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if (IntegrationsHelper.instance.isLoaded(J, false)) {
			JStorage.readFromNBT(tag);
		}
		if (IntegrationsHelper.instance.isLoaded(TESLA, false)) {
			TESLAStorage.deserializeNBT(tag.getCompoundTag("#TeslaTag"));
		}
		RFStorage.readFromNBT(tag);
		filteredEnergy = LiquidAIEnergy.readFromNBT(tag);
	}

	@Override
	public int getLightLevel() {
		return 0;
	}

	@Override
	public void removeFromWorld() {
		super.removeFromWorld();
		if (IntegrationsHelper.instance.isLoaded(EU, false)) {
			this.invalidateSinkSource();
		}
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		if (IntegrationsHelper.instance.isLoaded(EU, false)) {
			this.updateSinkSource();
		}
	}

	@Override
	public boolean onActivate(EntityPlayer player, EnumHand enumHand, Vec3d vec3d) {
		// Activation logic is server sided
		if (Platform.isServer()) {
			if (!player.isSneaking()) {
				// Open GUI
				AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiInterface, player, getHostSide(), getHostTile().getPos());
			}
		}
		return true;
	}

	// drops, when wrenched
	@Override
	public void getDrops(List<ItemStack> drops, boolean wrenched) {
		for (ItemStack stack : upgradeInventory.slots) {
			if (stack != null) {
				drops.add(stack);
			}
		}
	}

	private void updateSinkSource() {
		if (getEnergyStorage(EU, INTERNAL) == null) {
			EUStorage = new InterfaceSinkSource(this, INTERNAL, getMaxEnergyStored(null, EU), 4, 4);
		}

		// Update handler
		((InterfaceSinkSource) getEnergyStorage(EU, INTERNAL)).update();
	}

	private void invalidateSinkSource() {
		if (getEnergyStorage(EU, INTERNAL) != null) {
			((InterfaceSinkSource) getEnergyStorage(EU, INTERNAL)).invalidate();
		}
	}

	@Override
	public void onEntityCollision(Entity entity) {
	}

	@Override
	public float getCableConnectionLength(AECableType aeCableType) {
		return 2;
	}

	@Nonnull
	@Override
	public IPartModel getStaticModels() {
		if (this.isPowered()) {
			if (this.isActive()) {
				return PartModelEnum.STORAGE_INTERFACE_HAS_CHANNEL;
			} else {
				return PartModelEnum.STORAGE_INTERFACE_ON;
			}
		}
		return PartModelEnum.STORAGE_INTERFACE_OFF;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability) {
		return duality.hasCapability(capability);
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability) {
		return duality.getCapability(capability, INTERNAL);
	}

	@Override
	public void onInventoryChanged() {
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
		return getItemStack(BREAK);
	}

	@Override
	public GuiBridge getGuiBridge() {
		return null;
	}

	public void removeListener(final ContainerEnergyInterface container) {
		this.linkedListeners.remove(container);
	}

	public void addListener(final ContainerEnergyInterface container) {
		if (!this.linkedListeners.contains(container)) {
			this.linkedListeners.add(container);
		}
	}

	@Override
	public final void updateFilter(final LiquidAIEnergy energy, final int index) {
		// Set the filter
		this.filteredEnergy = energy;
	}

	@Override
	public TickingRequest getTickingRequest(@Nonnull final IGridNode node) {
		return new TickingRequest(1, 1, false, false);
	}

	@Override
	public TickRateModulation tickingRequest(@Nonnull final IGridNode node, final int TicksSinceLastCall) {
		if (!getHostTile().getWorld().isRemote) {
			// Sync stored energy data with GUI
			if (bar != null) {
				duality.notifyListenersOfBarFilterChange(bar);
				duality.notifyListenersOfEnergyBarChange(bar, INTERNAL);
			}

			try {
				if (this.isActive()) {
					// Try injecting energy from buffer
					doInjectDualityWork(Actionable.MODULATE);
					// Try extracting energy into buffer
					doExtractDualityWork(Actionable.MODULATE);
				}
			} catch(NullNodeConnectionException | GridAccessException e) {
				AILog.error(e, "Part energy interface can't access it's grid");
			}

			this.saveChanges();
		}
		return IDLE;
	}

	private void saveChanges() {
		// Ensure there is a host
		if (this.host != null) {
			// Mark
			this.host.markForSave();
		}
	}

	public IEnergyInterfaceDuality getDuality() {
		return this.duality;
	}

	@Override
	public double getMaxTransfer(AEPartLocation side) {
		return maxTransfer;
	}

	@Override
	public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {
		return filteredEnergy;
	}

	public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
		if (energy == RF) {
			return RFStorage;
		}
		if (energy == EU) {
			return EUStorage;
		}
		if (energy == J) {
			return JStorage;
		}
		if (energy == TESLA) {
			return TESLAStorage;
		}
		return null;
	}

	@Override
	public void doInjectDualityWork(Actionable action) throws NullNodeConnectionException, GridAccessException {
		getDuality().doInjectDualityWork(action);
	}

	@Override
	public void doExtractDualityWork(Actionable action) throws NullNodeConnectionException, GridAccessException {
		getDuality().doExtractDualityWork(action);
	}

	@Override
	public void provideCrafting(ICraftingProviderHelper craftingTracker) {}

	@Override
	public boolean pushPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table) {
		return false;
	}

	@Override
	public boolean isBusy() {
		return false;
	}

	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
		// Getting Node
		if (getGridNode(INTERNAL) == null) {
			return null;
		}
		// Getting net of node
		IGrid grid = getGridNode(INTERNAL).getGrid();
		// Cache of net
		IStorageGrid storage = grid.getCache(IStorageGrid.class);
		// fluidInventory of cache
		return storage.getInventory(channel);
	}

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

	@Override
	public void initEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
		if (energy == RF) {
			initRFStorage();
		}
		if (energy == EU) {
			initEUStorage();
		}
		if (energy == J) {
			initJStorage();
		}
		if (energy == TESLA) {
			initTESLAStorage();
		}
	}

	private void initRFStorage() {
		RFStorage = new EnergyInterfaceStorage(this, INTERNAL, RF, capacity, maxTransfer);
	}

	@Optional.Method(modid = "ic2")
	private void initEUStorage() {
		//EUStorage = new InterfaceSinkSource(getHostWorld(), );
	}

	@Optional.Method(modid = "mekanism")
	private void initJStorage() {
		JStorage = new JouleInterfaceStorage(this, INTERNAL, (int) (capacity * 2.5));
	}

	@Optional.Method(modid = "tesla")
	private void initTESLAStorage() {
		TESLAStorage = new TeslaInterfaceStorageDuality(this, INTERNAL, (long) capacity, (long) maxTransfer);
	}

	@Override
	public int getMaxEnergyStored(AEPartLocation unknown, @Nullable LiquidAIEnergy linkedMetric) {
		if (linkedMetric == RF) {
			return this.capacity;
		}
		if (linkedMetric == EU) {
			return (int) (this.capacity * 0.25);
		}
		if (linkedMetric == J) {
			return (int) (this.capacity * 2.5);
		}
		if (linkedMetric == WA) {
			return this.capacity * 10;
		}
		if (linkedMetric == HU) {
			return this.capacity * 4;
		}
		if (linkedMetric == KU) {
			return this.capacity * 4;
		}
		if (linkedMetric == FZ) {
			return this.capacity;
		}
		if (linkedMetric == AE) {
			return this.capacity / 2;
		}
		return 0;
	}

	@Override
	public TileEntity getFacingTile(EnumFacing side) {
		return getFacingTile();
	}

	@Override
	public List<ContainerEnergyInterface> getListeners() {
		return this.linkedListeners;
	}

	@Override
	public void setLastInjectedEnergy(AEPartLocation side, LiquidAIEnergy energy) {
		bar = energy;
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
		return (Double) getEnergyStorage(EU, INTERNAL).getMaxStored() - (Double) getEnergyStorage(EU,
				INTERNAL).getStored();
	}

	@Optional.Method(modid = "ic2")
	@Override
	public int getSinkTier() {
		return 4;
	}

	@Optional.Method(modid = "ic2")
	@Override
	public double injectEnergy(EnumFacing enumFacing, double v, double v1) {
		return (Double) getEnergyStorage(EU, INTERNAL).receive(v, Actionable.MODULATE);
	}

	@Optional.Method(modid = "ic2")
	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		return true;
	}

	@Nonnull
	@Override
	public AIGuiHandler.GuiEnum getGui() {
		return AIGuiHandler.GuiEnum.GuiInterface;
	}
}



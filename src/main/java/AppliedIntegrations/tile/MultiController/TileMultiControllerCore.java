package AppliedIntegrations.tile.MultiController;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerCore;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerTerminal;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Inventory.AIGridNodeInventoryWithView;
import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.MultiController.PacketScrollClientToServer;
import AppliedIntegrations.Utils.MultiBlockUtils;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.IInventoryHost;
import AppliedIntegrations.api.Multiblocks.IAIPatternExtendable;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import AppliedIntegrations.tile.MultiController.Networking.MEMultiControllerMonitorHandlerReceiver;
import AppliedIntegrations.tile.MultiController.helpers.Crafting.MultiControllerCPUHandler;
import AppliedIntegrations.tile.MultiController.helpers.Crafting.MultiControllerCraftingHandler;
import AppliedIntegrations.tile.MultiController.helpers.Matter.FilteredMultiControllerPortHandler;
import AppliedIntegrations.tile.MultiController.helpers.MultiControllerCoreInventory;
import AppliedIntegrations.tile.Patterns.AIPatterns;
import appeng.api.config.*;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkCraftingCpuChange;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.*;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.api.util.IConfigManager;
import appeng.api.util.INetworkToolAgent;
import appeng.me.GridAccessException;
import appeng.me.helpers.MEMonitorHandler;
import appeng.me.helpers.MachineSource;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Azazell
 */
public class TileMultiControllerCore extends AITile implements IAIMultiBlock, IMaster,
		INetworkToolAgent, ITickable, ITerminalHost, IConfigManagerHost {
	public class CardInventoryManager implements IInventoryHost {
		public void onCardRemove(ItemStack card) {
			NBTTagCompound tag = Platform.openNbtData(card);
			AEPartLocation side = AEPartLocation.values()[tag.getInteger(NetworkCard.NBT_KEY_PORT_SIDE)];
			int id = tag.getInteger(NetworkCard.NBT_KEY_PORT_ID);
			TileMultiControllerPort port = getPortAtSide(id, side);

			if (port == null) {
				return;
			}

			portHandlers.get(side).put(id, null);
			portCraftingHandlers.get(side).put(id, null);
			portCPUHandlers.get(side).put(id, null);

			cpuUpdate();
			port.postCellInventoryEvent();
			port.postGridEvent(new MENetworkCraftingPatternChange(portCraftingHandlers.get(side).get(id), getGridNode()));
		}

		@Override
		public void onInventoryChanged() {
			for (ItemStack stack : cardInv.slots) {
				if (stack.getItem() instanceof NetworkCard) {
					NBTTagCompound tag = Platform.openNbtData(stack);
					Pair<LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>>,
							LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>>> data =
							NetworkCard.decodeDataFromTag(tag);

					AEPartLocation side = AEPartLocation.values()[tag.getInteger(NetworkCard.NBT_KEY_PORT_SIDE)];
					int id = tag.getInteger(NetworkCard.NBT_KEY_PORT_ID);
					TileMultiControllerPort port = getPortAtSide(id, side);

					if (port == null) {
						continue;
					}

					LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler> handlers = new LinkedHashMap<>();
					ContainerMultiControllerTerminal.channelList.forEach(channel -> {
						try {
							// Get new handler from API
							FilteredMultiControllerPortHandler handler = Objects.requireNonNull(AIApi.instance()).getHandlerFromChannel(channel).newInstance(data.getLeft(), data.getRight(), TileMultiControllerCore.this);

							// Map handler with channel
							handlers.put(channel, handler);
						} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
							throw new IllegalStateException("Unexpected Error");
						}
					});

					portHandlers.get(side).put(id, handlers);
					portCraftingHandlers.get(side).put(
							id, new MultiControllerCraftingHandler(data.getLeft(), data.getRight(), TileMultiControllerCore.this));
					portCPUHandlers.get(side).put(id, new MultiControllerCPUHandler(TileMultiControllerCore.this));
					cpuUpdate();

					port.postCellInventoryEvent();
					port.postGridEvent(new MENetworkCraftingPatternChange(portCraftingHandlers.get(side).get(id), getGridNode()));
					port.postGridEvent(new MENetworkCraftingCpuChange(getGridNode()));
				}
			}
		}
	}

	public static final int INV_SLOTS = 153;
	private static final String KEY_FORMED = "#FORMED";

	private boolean constructionRequested;

	private List<AIMultiControllerTile> slaves = new ArrayList<>();
	private LinkedHashMap<Class<? extends AIMultiControllerTile>, List<AIMultiControllerTile>> slaveMap = new LinkedHashMap<>();
	private MultiControllerCoreInventory cardInventory = new MultiControllerCoreInventory(this);
	private IMEMonitor<IAEItemStack> monitor = new MEMonitorHandler<>(cardInventory);
	private CardInventoryManager cardManager = new CardInventoryManager();
	private IConfigManager configManager = new ConfigManager(this);

	// list of blocks in multi-block
	private List<Class<? extends AIMultiControllerTile>> multi_controllerClasses =
			Arrays.asList(TileMultiControllerHousing.class, TileMultiControllerPort.class, TileMultiControllerRib.class);

	// Port-side map
	private LinkedHashMap<AEPartLocation,Map<Integer, TileMultiControllerPort>> portMap = new LinkedHashMap<AEPartLocation, Map<Integer, TileMultiControllerPort>>() {{
		for (AEPartLocation side : AEPartLocation.values()){
			put(side, new HashMap<>());
		}
	}};

	// List of all "mediums" for providing cell inventory from main network into adjacent networks
	private LinkedHashMap<AEPartLocation, Map<Integer, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler>>> portHandlers = new LinkedHashMap<AEPartLocation, Map<Integer, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler>>>() {{
		for (AEPartLocation side : AEPartLocation.values()){
			put(side, new HashMap<>());
		}
	}};

	// List of all crafting "mediums" for providing craft grid from main network into adjacent networks
	private LinkedHashMap<AEPartLocation, Map<Integer, ICraftingProvider>> portCraftingHandlers = new LinkedHashMap<AEPartLocation, Map<Integer, ICraftingProvider>>() {{
		for (AEPartLocation side : AEPartLocation.values()) {
			put(side, new HashMap<>());
		}
	}};

	// List of all crafting CPU simulators
	private LinkedHashMap<AEPartLocation, Map<Integer, MultiControllerCPUHandler>> portCPUHandlers = new LinkedHashMap<AEPartLocation, Map<Integer, MultiControllerCPUHandler>>(){{
		for (AEPartLocation side : AEPartLocation.values()){
			put(side, new HashMap<>());
		}
	}};

	public AIGridNodeInventoryWithView cardInv = new AIGridNodeInventoryWithView("Network Card Slots", INV_SLOTS, 45, 1, this.cardManager) {
		@Override
		public ItemStack decrStackSize(int slotId, int amount) {
			if (slots[slotId].getItem() instanceof NetworkCard) {
				cardManager.onCardRemove(slots[slotId]);
			}

			return super.decrStackSize(slotId, amount);
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemstack) {
			return itemstack.getItem() instanceof NetworkCard;
		}
	};

	private List<MEMultiControllerMonitorHandlerReceiver> receiverList = new ArrayList<>();

	private boolean isFormed;
	private int slotDifference;

	public List<ContainerMultiControllerCore> listeners = new ArrayList<>();

	{
		// Fill maps with empty data
		nullifyMap();

		configManager.registerSetting( Settings.SORT_BY, SortOrder.NAME );
		configManager.registerSetting( Settings.VIEW_MODE, ViewItems.ALL );
		configManager.registerSetting( Settings.SORT_DIRECTION, SortDir.ASCENDING );
	}

	public int getSlotDiff() {
		return this.slotDifference;
	}

	public void setSlotDiff(int scroll) {
		this.slotDifference = scroll;
		this.cardInv.updateView(scroll);

		if (world.isRemote) {
			NetworkHandler.sendToServer(new PacketScrollClientToServer(getSlotDiff(), this));
		}
	}

	public CardInventoryManager getCardManager() {
		return cardManager;
	}

	private void cpuUpdate() {
		// TODO: 2019-05-25 Later
	}

	public ICraftingGrid getMainNetworkCraftingGrid() {
		if (getMainNetwork() == null) {
			return null;
		}

		return getMainNetwork().getCache(ICraftingGrid.class);
	}

	private IGrid getMainNetwork() {
		if (!isFormed) {
			return null;
		}

		if (slaveMap.get(TileMultiControllerRib.class).isEmpty()) {
			return null;
		}

		TileMultiControllerRib rib = (TileMultiControllerRib) slaveMap.get(TileMultiControllerRib.class).get(0);
		return rib.getMainNetwork();
	}

	public void activate(EntityPlayer p) {
		AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiServerStorage, p, AEPartLocation.INTERNAL, pos);
	}

	public void postNetworkCellEvents() throws GridAccessException {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			for (int portID = 0; portID < portMap.get(side).size(); portID++) {
				if (portMap.get(side).get(portID) == null || portMap.get(side).get(portID).requestNetwork() == null) {
					continue;
				}

				postCellInventoryEvent(portMap.get(side).get(portID).requestNetwork());
				postGridEvent(new MENetworkCraftingPatternChange(portCraftingHandlers.get(side).get(portID), getGridNode()));
			}
		}

		postCellInventoryEvent();
	}

	@SuppressWarnings("unchecked")
	public void postNetworkAlterationsEvents(IStorageChannel<? extends IAEStack<?>> channel, Iterable change, MachineSource machineSource) throws GridAccessException {
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			for (int portID = 0; portID < portMap.get(side).size(); portID++) {
				if (portMap.get(side).get(portID) == null || portMap.get(side).get(portID).requestNetwork() == null) {
					continue;
				}

				IStorageGrid grid = portMap.get(side).get(portID).requestNetwork().getCache(IStorageGrid.class);
				grid.postAlterationOfStoredItems(channel, change, machineSource);
			}
		}
	}

	// -----------------------------Crafting Methods-----------------------------//
	public void providePortCrafting(ICraftingProviderHelper craftingTracker, AEPartLocation side, int portID) {
 		if (portCraftingHandlers.get(side).get(portID) == null) {
			return;
		}

		portCraftingHandlers.get(side).get(portID).provideCrafting(craftingTracker);
	}

	public boolean pushPortPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table, AEPartLocation side,
	                               int portID) {
		if (portCraftingHandlers.get(side).get(portID) == null) {
			return false;
		}

		return portCraftingHandlers.get(side).get(portID).pushPattern(patternDetails, table);
	}

	public boolean isPortBusy(AEPartLocation side, int portID) {
		if (portCraftingHandlers.get(side).get(portID) == null) {
			return false;
		}

		return portCraftingHandlers.get(side).get(portID).isBusy();
	}
	// -----------------------------Crafting Methods-----------------------------//

	// -----------------------------Drive Methods-----------------------------//
	public List<IMEInventoryHandler> getPortCellArray(AEPartLocation side, int portID, IStorageChannel<?> channel) {
		if (portHandlers.get(side) == null || portHandlers.get(side).get(portID) == null) {
			return new ArrayList<>();
		}

		return Collections.singletonList(portHandlers.get(side).get(portID).get(channel));
	}

	public void savePortChanges(ICellInventory<?> iCellInventory, AEPartLocation side, int id) {
		if (iCellInventory != null) {
			iCellInventory.persist();
		}

		TileMultiControllerPort port = getPortAtSide(id, side);
		if (port == null) {
			return;
		}

		getWorld().markChunkDirty(port.getPos(), port);
	}
	// -----------------------------Drive Methods-----------------------------//

	private TileMultiControllerPort getPortAtSide(int id, AEPartLocation side) {
		for (IAIMultiBlock slave : slaveMap.get(TileMultiControllerPort.class)) {
			TileMultiControllerPort port = (TileMultiControllerPort) slave;
			if (port.getSideVector() == side && port.getPortID() == id) {
				return port;
			}
		}

		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		// Read inventories
		cardInv.readFromNBT(tag.getTagList("#cardInv", 10)); // Card inventory

		// Check if tile is formed
		if (tag.getBoolean(KEY_FORMED)) {
			// When world is loaded this chain fires forI tile -> readFromNBT -> ... -> ..........
			// And then forI: tile.update.
			// So, at moment when tile.update is called all tiles are already loaded. So, construction
			// Should be performed from update method
			// Request construction
			constructionRequested = true;
		}

		super.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("#cardInv", cardInv.writeToNBT());
		tag.setBoolean(KEY_FORMED, isFormed);
		return super.writeToNBT(tag);
	}

	@Override
	public boolean showNetworkInfo(RayTraceResult rayTraceResult) {

		return false;
	}

	@Override
	public Iterator<IGridNode> getMultiblockNodes() {
		return null;
	}

	@Override
	public void securityBreak() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void invalidate() {
		super.invalidate();
		if (isFormed) {
			this.destroyMultiBlock();
		}

		Platform.spawnDrops(world, pos, Arrays.asList(cardInv.slots)); // Card inv
	}

	@SuppressWarnings("unchecked")
	public void destroyMultiBlock() {
		for (AIMultiControllerTile tile : slaves) {
			tile.setMaster(null);
			tile.destroyProxyNode();
		}

		nullifyMap();
		slaves = new ArrayList<>();

		// Nullify maps
		portMap = new LinkedHashMap<AEPartLocation, Map<Integer, TileMultiControllerPort>>() {{
			for (AEPartLocation side : AEPartLocation.values()){
				put(side, new HashMap<>());
			}
		}}; // (1)

		portHandlers = new LinkedHashMap<AEPartLocation, Map<Integer, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler>>>() {{
			for (AEPartLocation side : AEPartLocation.values()){
				put(side, new HashMap<>());
			}
		}}; // (2)

		portCraftingHandlers = new LinkedHashMap<AEPartLocation, Map<Integer, ICraftingProvider>>() {{
			for (AEPartLocation side : AEPartLocation.values()){
				put(side, new HashMap<>());
			}
		}}; // (3)

		portCPUHandlers = new LinkedHashMap<AEPartLocation, Map<Integer, MultiControllerCPUHandler>>() {{
			for (AEPartLocation side : AEPartLocation.values()){
				put(side, new HashMap<>());
			}
		}}; // (4)
		isFormed = false;

		// Remove receivers from listeners of each channel from main server grid
		// Iterate for each channel
		ContainerMultiControllerTerminal.channelList.forEach(channel -> {
			receiverList.forEach((meMultiControllerMonitorHandlerReceiver -> {
				if (getMainNetworkInventory(channel) != null) {
					getMainNetworkInventory(channel).removeListener(meMultiControllerMonitorHandlerReceiver);
				}
			}));
		});

		receiverList = new ArrayList<>();
	}

	private void nullifyMap() {
		slaveMap = new LinkedHashMap<>();

		for (Class<? extends AIMultiControllerTile> type : multi_controllerClasses) {
			slaveMap.put(type, new ArrayList<>());
		}
	}

	@Override
	public void update() {
		super.update();

		if (world.isRemote) {
			return;
		}

		if (constructionRequested) {
			tryConstruct(null);
			constructionRequested = true;
		}
	}

	@Override
	public void notifyBlock() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void tryConstruct(EntityPlayer p) {
		if (!isFormed) {
			AtomicInteger count = new AtomicInteger();

			Map<EnumFacing.Axis, Integer> axisLengthMap = new LinkedHashMap<>();

			// Iterate 3 times
			for (int rangeToBlock = 0; rangeToBlock < 4; rangeToBlock++) {
				final int finalI = rangeToBlock;

				Arrays.stream(EnumFacing.values()).filter((facing) -> facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE).forEach((side) -> {
					TileEntity maybePort = world.getTileEntity(new BlockPos(
							getPos().getX() + side.getFrontOffsetX() * finalI,
							getPos().getY() + side.getFrontOffsetY() * finalI,
							getPos().getZ() + side.getFrontOffsetZ() * finalI));

					if (maybePort instanceof TileMultiControllerPort) {
						axisLengthMap.put(side.getAxis(), finalI - 1);
					}
				});
			}

			IAIPatternExtendable pattern = AIPatterns.ME_MULTI_CONTROLLER;
			if (axisLengthMap.size() != 3)
				return;

			// Extend pattern by length from map
			pattern = MultiBlockUtils.getExtendedPattern(pattern, axisLengthMap);

			try {
				// Get list of blocks matched the pattern
				formServer((List<AIMultiControllerTile>) MultiBlockUtils.fillListWithPattern(pattern.getPatternData(),
						this, (block) -> count.getAndIncrement()), pattern, count, p);
			} catch (GridAccessException ignored) { }
		}
	}

	@SuppressWarnings("unchecked")
	private void formServer(List<AIMultiControllerTile> toUpdate, IAIPatternExtendable pattern, AtomicInteger count, EntityPlayer p) throws GridAccessException {
		if (pattern.getPatternData().size() == count.get()) {
			for (AIMultiControllerTile slave : toUpdate) {
				if (slave == null)
					continue;

				slave.setMaster(this);
				slave.createProxyNode();
				slaveMap.get(slave.getClass()).add(slave);
				slaves.add(slave);
			}

			for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
				List<BlockPos> edge = pattern.getPosEdgeMap().get(side);
				int edgePosId = 0;
				for (BlockPos pos : edge) {
					TileEntity maybePort = getWorld().getTileEntity(getPos().add(pos));

					if (maybePort instanceof TileMultiControllerPort) {
						TileMultiControllerPort port = (TileMultiControllerPort) maybePort;

						port.setSideVector(side);
						port.setPortID(edgePosId);

						portMap.get(side).put(edgePosId, port);
						edgePosId ++;
					}
				}
			}

			cardManager.onInventoryChanged();
			postNetworkCellEvents();
			isFormed = true;

			// Add receivers to listeners of each channel of main server grid
			ContainerMultiControllerTerminal.channelList.forEach(channel -> {
				IMEMonitor<? extends IAEStack<?>> inventory = getMainNetworkInventory(channel);
				MEMultiControllerMonitorHandlerReceiver receiver = new MEMultiControllerMonitorHandlerReceiver<>(this, channel);
				receiverList.add(receiver);
				inventory.addListener(receiver, null);
			});

			if (p != null) {
				p.sendMessage(new TextComponentTranslation("ME multi-controller formed!"));
			}
		}
	}

	public <T extends IAEStack<T>> IMEMonitor<T> getMainNetworkInventory(IStorageChannel<T> channel) {
		if (getMainNetwork() == null) {
			return null;
		}

		return ((IStorageMonitorable) getMainNetwork().getCache(IStorageGrid.class)).getInventory(channel);
	}

	@Override
	public boolean hasMaster() {

		return true;
	}

	@Override
	public IMaster getMaster() {

		return this;
	}

	@Override
	public void setMaster(IMaster tileServerCore) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
		if (channel == cardInventory.getChannel()) {
			return (IMEMonitor<T>) monitor;
		}

		return null;
	}

	@Override
	public IConfigManager getConfigManager() {
		return configManager;
	}

	@Override
	public void updateSetting(IConfigManager manager, Enum settingName, Enum newValue) {
		// Ignored ( not, since this comment here :) )
	}
}

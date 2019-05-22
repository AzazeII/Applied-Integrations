package AppliedIntegrations.tile.MultiController;


import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Items.NetworkCard;
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
import AppliedIntegrations.tile.Patterns.AIPatterns;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
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
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.api.util.INetworkToolAgent;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
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
public class TileMultiControllerCore extends AITile implements IAIMultiBlock, IMaster, INetworkToolAgent, ITickable {
	private class CardInventoryManager implements IInventoryHost {

		private void onCardRemove(ItemStack card) {
			// Get tag
			NBTTagCompound tag = Platform.openNbtData(card);

			// Get side
			AEPartLocation side = AEPartLocation.values()[tag.getInteger(NetworkCard.NBT_KEY_NET_SIDE)];

			// Get port
			TileMultiControllerPort port = getPortAtSide(side);

			// Check not null
			if (port == null) {
				// Skip
				return;
			}

			// Nullify port handlers for this port
			portHandlers.put(side, null);

			// Nullify port crafting handlers for this port
			portCraftingHandlers.put(side, null);

			// Nullify cpu handlers for this port
			portCPUHandlers.put(side, null);

			// Update CPU handlers in each grid;
			cpuUpdate();

			// Notify grid of current port
			port.postCellInventoryEvent();

			// Notify grid of current port about crafting update
			port.postCellEvent(new MENetworkCraftingPatternChange(portCraftingHandlers.get(side), getGridNode()));
		}

		@Override
		public void onInventoryChanged() {
			// Iterate for each stack in cards inventory
			for (ItemStack stack : cardInv.slots) {
				// Check if item in stack is network card
				if (stack.getItem() instanceof NetworkCard) {
					// Get tag
					NBTTagCompound tag = Platform.openNbtData(stack);

					// Get decoded pair from card
					Pair<LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>>, LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>>> data = NetworkCard.decodeDataFromTag(tag);

					// Get side
					AEPartLocation side = AEPartLocation.values()[tag.getInteger(NetworkCard.NBT_KEY_NET_SIDE)];

					// Get port
					TileMultiControllerPort port = getPortAtSide(side);

					// Check not null
					if (port == null) {
						// Skip
						continue;
					}

					// Create list
					LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler> handlers = new LinkedHashMap<>();

					// Iterate for each channel
					GuiStorageChannelButton.getChannelList().forEach(channel -> {
						try {
							// Get new handler from API
							FilteredMultiControllerPortHandler handler = Objects.requireNonNull(AIApi.instance()).getHandlerFromChannel(channel).newInstance(data.getLeft(), data.getRight(), TileMultiControllerCore.this);

							// Map handler with channel
							handlers.put(channel, handler);
						} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
							throw new IllegalStateException("Unexpected Error");
						}
					});

					// Encode new handler for side from card
					TileMultiControllerCore.this.portHandlers.put(side, handlers);

					// Encode new crafting handler for side from card
					TileMultiControllerCore.this.portCraftingHandlers.put(side, new MultiControllerCraftingHandler(data.getLeft(), data.getRight(), TileMultiControllerCore.this));

					// Encode new CPU handler for side from card
					TileMultiControllerCore.this.portCPUHandlers.put(side, new MultiControllerCPUHandler(TileMultiControllerCore.this));

					// Update CPU handler in each grid
					TileMultiControllerCore.this.cpuUpdate();

					// Notify grid of current port about inventory update
					port.postCellInventoryEvent();

					// Notify grid of current port about crafting update
					port.postCellEvent(new MENetworkCraftingPatternChange(portCraftingHandlers.get(side), getGridNode()));

					// Notify grid of current port about cpu update
					port.postCellEvent(new MENetworkCraftingCpuChange(getGridNode()));
				}
			}
		}
	}

	private static final String KEY_FORMED = "#FORMED";

	public List<AIMultiControllerTile> slaves = new ArrayList<>();

	private boolean constructionRequested;

	private LinkedHashMap<Class<? extends AIMultiControllerTile>, List<AIMultiControllerTile>> slaveMap = new LinkedHashMap<>();

	// Port-side map
	private LinkedHashMap<AEPartLocation, TileMultiControllerPort> portMap = new LinkedHashMap<>();

	private CardInventoryManager cardManager = new CardInventoryManager();

	// list of blocks in multiblock
	private List<Class<? extends AIMultiControllerTile>> serverClasses = Arrays.asList(TileMultiControllerHousing.class, TileMultiControllerPort.class, TileMultiControllerRib.class);

	// List of all "mediums" for providing cell inventory from main network into adjacent networks
	private LinkedHashMap<AEPartLocation, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler>> portHandlers = new LinkedHashMap<>();

	// List of all crafting "mediums" for providing craft grid from main network into adjacent networks
	private LinkedHashMap<AEPartLocation, ICraftingProvider> portCraftingHandlers = new LinkedHashMap<>();

	// List of all crafting CPU simulators
	private LinkedHashMap<AEPartLocation, MultiControllerCPUHandler> portCPUHandlers = new LinkedHashMap<>();

	public AIGridNodeInventory cardInv = new AIGridNodeInventory("Network Card Slots", 30, 1, this.cardManager) {
		@Override
		public ItemStack decrStackSize(int slotId, int amount) {
			// Check if slot decreasing is network card
			if (slots[slotId].getItem() instanceof NetworkCard) {
				// Pass call to outer function
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

	{
		nullifyMap();
	}

	private void cpuUpdate() {
		// TODO Later
	}

	public ICraftingGrid getMainNetworkCraftingGrid() {
		// Check not null
		if (getMainNetwork() == null) {
			return null;
		}

		return getMainNetwork().getCache(ICraftingGrid.class);
	}

	private IGrid getMainNetwork() {
		// Check if mutli-block isn't formed
		if (!isFormed) {
			return null;
		}

		// Check if list is empty
		if (slaveMap.get(TileMultiControllerRib.class).isEmpty()) {
			return null;
		}

		// Get first rib in list in map
		TileMultiControllerRib rib = (TileMultiControllerRib) slaveMap.get(TileMultiControllerRib.class).get(0);

		// Pass call to rib
		return rib.getMainNetwork();
	}

	public void activate(EntityPlayer p) {
		// Open GUI
		AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiServerStorage, p, AEPartLocation.INTERNAL, pos);
	}

	public void postNetworkCellEvents() throws GridAccessException {
		// Iterate for each side
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			// Check not null
			if (portMap.get(side) == null || portMap.get(side).requestNetwork() == null) {
				continue;
			}

			// Post cell event for network at this side
			postCellInventoryEvent(portMap.get(side).requestNetwork());

			// Notify grid of current port about crafting update
			postCellEvent(new MENetworkCraftingPatternChange(portCraftingHandlers.get(side), getGridNode()));
		}

		// Notify main server network
		postCellInventoryEvent();
	}

	public void postNetworkAlterationsEvents(IStorageChannel<? extends IAEStack<?>> channel, Iterable change, MachineSource machineSource) throws GridAccessException {
		// Iterate for each side
		for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
			// Check not null
			if (portMap.get(side) == null || portMap.get(side).requestNetwork() == null) {
				continue;
			}

			// Get storage grid of network
			IStorageGrid grid = portMap.get(side).requestNetwork().getCache(IStorageGrid.class);

			// Post alteration
			grid.postAlterationOfStoredItems(channel, change, machineSource);
		}
	}

	// -----------------------------Crafting Methods-----------------------------//
	public void providePortCrafting(ICraftingProviderHelper craftingTracker, AEPartLocation side) {
		// Check if handler not null
		if (portCraftingHandlers.get(side) == null) {
			return;
		}

		// Pass call to handler
		portCraftingHandlers.get(side).provideCrafting(craftingTracker);
	}

	public boolean pushPortPattern(ICraftingPatternDetails patternDetails, InventoryCrafting table, AEPartLocation side) {
		// Check if handler not null
		if (portCraftingHandlers.get(side) == null) {
			return false;
		}

		// Pass call to handler
		return portCraftingHandlers.get(side).pushPattern(patternDetails, table);
	}

	public boolean isPortBusy(AEPartLocation side) {
		// Check if handler not null
		if (portCraftingHandlers.get(side) == null) {
			return false;
		}

		// Pass call to handler
		return portCraftingHandlers.get(side).isBusy();
	}
	// -----------------------------Crafting Methods-----------------------------//

	// -----------------------------Drive Methods-----------------------------//
	public List<IMEInventoryHandler> getPortCellArray(AEPartLocation side, IStorageChannel<?> channel) {
		// Check if handler not null
		if (portHandlers.get(side) == null) {
			return new ArrayList<>();
		}

		// Return only one handler for tile
		return Collections.singletonList(portHandlers.get(side).get(channel));
	}

	public void savePortChanges(ICellInventory<?> iCellInventory, AEPartLocation side) {
		// Check if inventory not null
		if (iCellInventory != null) {
			// Persist inventory
			iCellInventory.persist();
		}

		// Get port
		TileMultiControllerPort port = getPortAtSide(side);

		// Check not null
		if (port == null) {
			return;
		}

		// Mark dirty
		getWorld().markChunkDirty(port.getPos(), port);
	}
	// -----------------------------Drive Methods-----------------------------//
	private TileMultiControllerPort getPortAtSide(AEPartLocation side) {
		// Iterate for each slave
		for (IAIMultiBlock slave : slaveMap.get(TileMultiControllerPort.class)) {
			// Get port
			TileMultiControllerPort port = (TileMultiControllerPort) slave;

			// Check if port side is given side
			if (port.getSideVector() == side) {
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
		// Write inventories
		tag.setTag("#cardInv", cardInv.writeToNBT()); // Card inventory

		// Write is formed
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

		// Drop items from drive and card inventory
		Platform.spawnDrops(world, pos, Arrays.asList(cardInv.slots)); // Card inv
	}

	@SuppressWarnings("unchecked")
	public void destroyMultiBlock() {
		// Iterate for each slave
		for (AIMultiControllerTile tile : slaves) {
			// Nullify master
			tile.setMaster(null);

			// Destroy proxy noe
			tile.destroyProxyNode();
		}

		// Nullify slave map
		nullifyMap();

		// Nullify slave list
		slaves = new ArrayList<>();

		// Nullify maps
		portMap = new LinkedHashMap<>(); // (1)
		portHandlers = new LinkedHashMap<>(); // (2)
		portCraftingHandlers = new LinkedHashMap<>(); // (3)
		portCPUHandlers = new LinkedHashMap<>(); // (4)

		// Make server not formed
		isFormed = false;

		// Remove receivers from listeners of each channel from main server grid
		// Iterate for each channel
		GuiStorageChannelButton.getChannelList().forEach(channel -> {
			// Iterate for each ME server listeners in list
			receiverList.forEach((meMultiControllerMonitorHandlerReceiver -> {
				// Check not null
				if (getMainNetworkInventory(channel) != null) {
					// Remove from listeners
					getMainNetworkInventory(channel).removeListener(meMultiControllerMonitorHandlerReceiver);
				}
			}));
		});

		// Nullify receivers list
		receiverList = new ArrayList<>(); // (2)
	}

	private void nullifyMap() {
		// Nullify map
		slaveMap = new LinkedHashMap<>();

		// Iterate for each tile type
		for (Class<? extends AIMultiControllerTile> type : serverClasses) {
			// Add list to map
			slaveMap.put(type, new ArrayList<>());
		}
	}

	@Override
	public void update() {
		super.update();

		// Check if construction was requested from read nbt method
		if (constructionRequested) {
			// Don't call on client
			if (world.isRemote) {
				// Skip client call
				return;
			}

			// Check if grid node is already initialized
			if (getGridNode() != null) {

			}

			// Try construct server
			tryConstruct(null);

			// Check if server was constructed successfully
			if (isFormed) {
				// Update inventory
				cardManager.onInventoryChanged();
			}

			// Toggle
			constructionRequested = true;
		}
	}

	@Override
	public void notifyBlock() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void tryConstruct(EntityPlayer p) {
		// Check if multi block isn't formed yet
		if (!isFormed) {
			// Count of blocks matched the pattern. Atomic, because it accessed by lambda function
			AtomicInteger count = new AtomicInteger();

			// Map for half of length from core to port at axis
			Map<EnumFacing.Axis, Integer> axisLengthMap = new LinkedHashMap<>();

			// Iterate until i = 4
			for (int rangeToBlock = 0; rangeToBlock < 4; rangeToBlock++) {
				// Convert to final
				final int finalI = rangeToBlock;

				// Iterate for each positive enum side
				Arrays.stream(EnumFacing.values()).filter((facing) -> facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE).forEach((side) -> {
					// Get tile with rangeToBlock blocks offset to side
					TileEntity maybePort = world.getTileEntity(new BlockPos(
							getPos().getX() + side.getFrontOffsetX() * finalI,
							getPos().getY() + side.getFrontOffsetY() * finalI,
							getPos().getZ() + side.getFrontOffsetZ() * finalI));

					// Check if tile is port
					if (maybePort instanceof TileMultiControllerPort) {
						// Put value in map
						axisLengthMap.put(side.getAxis(), finalI);
					}
				});
			}

			// Get initial pattern
			IAIPatternExtendable pattern = AIPatterns.ME_MULTI_CONTROLLER;

			// Iterate for each axis
			for (EnumFacing.Axis axis : EnumFacing.Axis.values()) {
				// Check if map contains this axis
				if (!axisLengthMap.containsKey(axis))
					// Skip
					continue;

				// Extend pattern by length from map
				pattern = MultiBlockUtils.getExtendedPattern(pattern, axis, axisLengthMap.get(axis) - 1);
			}

			try {
				// Get list of blocks matched the pattern
				formServer((List<AIMultiControllerTile>) MultiBlockUtils.fillListWithPattern(AIPatterns.ME_MULTI_CONTROLLER.getPatternData(),
						this, (block) -> count.getAndIncrement()), count, p);
			} catch (GridAccessException ignored) { }
		}
	}

	@SuppressWarnings("unchecked")
	private void formServer(List<AIMultiControllerTile> toUpdate, AtomicInteger count, EntityPlayer p) throws GridAccessException {
		// Check if length equal to count, so all block has matched the pattern
		if (AIPatterns.ME_MULTI_CONTROLLER.getPatternData().size() == count.get()) {
			// Iterate for each block to update
			for (AIMultiControllerTile slave : toUpdate) {
				// Check if slave is null
				if (slave == null)
					continue;

				// Set slave master
				slave.setMaster(this);

				// Create slave proxy
				slave.createProxyNode();

				// Put in category map
				slaveMap.get(slave.getClass()).add(slave);

				// Add to slave list
				slaves.add(slave);
			}

			// Iterate for each side
			for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
				// get tile with double offset from this side
				TileEntity tile = world.getTileEntity(new BlockPos(getPos().getX() + side.xOffset * 2, getPos().getY() + side.yOffset * 2, getPos().getZ() + side.zOffset * 2));

				// Check for instanceof port
				if (tile instanceof TileMultiControllerPort) {
					// Get port
					TileMultiControllerPort port = (TileMultiControllerPort) tile;

					// Set proper direction
					port.setDir(side.getFacing());

					// Add port
					portMap.put(side, port);

					// Update grid of port
					port.onNeighborChange();
				}
			}

			// Toggle formed
			isFormed = true;

			// Add receivers to listeners of each channel of main server grid
			// Iterate for each channel
			GuiStorageChannelButton.getChannelList().forEach(channel -> {
				// Get inventory
				IMEMonitor<? extends IAEStack<?>> inventory = getMainNetworkInventory(channel);

				// Create receiver
				MEMultiControllerMonitorHandlerReceiver receiver = new MEMultiControllerMonitorHandlerReceiver<>(this, channel);

				// Add to receiver list
				receiverList.add(receiver);

				// Add to listeners of main network inventory
				inventory.addListener(receiver, null);
			});

			// Check not null
			if (p != null) {
				// Send message
				p.sendMessage(new TextComponentTranslation("ME Server Formed!"));
			}
		}
	}

	public <T extends IAEStack<T>> IMEMonitor<T> getMainNetworkInventory(IStorageChannel<T> channel) {
		// Check not null
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
}

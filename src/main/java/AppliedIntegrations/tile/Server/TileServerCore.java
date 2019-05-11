package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Container.tile.Server.ContainerMEServer;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Gui.ServerGUI.GuiMEServer;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.MultiBlockUtils;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.IInventoryHost;
import AppliedIntegrations.tile.*;
import appeng.api.AEApi;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.*;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.api.util.INetworkToolAgent;
import appeng.api.util.IReadOnlyCollection;
import appeng.util.Platform;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Azazell
 */
public class TileServerCore extends AITile implements IAIMultiBlock, IMaster, ICellContainer, INetworkToolAgent, IInventoryHost, ITickable {

    private static final int BLOCKS_IN_STRUCTURE = AIPatterns.ME_SERVER.length+1;

    private static final int RESERVED_MASTER_ID = 1;
    private int AVAILABLE_ID = RESERVED_MASTER_ID+1;

    // list of blocks in multiblock
    public Vector<IAIMultiBlock> slaves = new Vector<>();

    public AIGridNodeInventory cardInv = new AIGridNodeInventory("Network Card Slots", 6, 1, this){
        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return itemstack.getItem() instanceof NetworkCard;
        }

        @Override
        public ItemStack decrStackSize(int slotId, int amount) {
            // Check if slot decreasing is network card
            if (slots[slotId].getItem() instanceof NetworkCard) {
                // Pass call to outer function
                onCardRemove(slots[slotId]);
            }

            return super.decrStackSize(slotId, amount);
        }
    };

    public AIGridNodeInventory inv = new AIGridNodeInventory("ME Server",30,1, this){
        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return AEApi.instance().registries().cell().isCellHandled(itemstack);
        }
    };


    // ME Inventories
    private List<IMEInventoryHandler> items = new LinkedList<>();
    private List<IMEInventoryHandler> fluids = new LinkedList<>();

    /**
     * Server network map
     * Map of ids of IGrids, which contains Server
     */
    public LinkedHashMap<IGrid, Integer> networkIDMap = new LinkedHashMap<>();

    // Networks in ports
    public LinkedHashMap<AEPartLocation,IGrid> portNetworks = new LinkedHashMap<>();

    private LinkedHashMap<AEPartLocation, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler>> handlers = new LinkedHashMap<>();

    // Network of owner
    public IGrid mainNetwork;

    public boolean isFormed;

    private byte[] cellStatuses = new byte[6];

    // Wait for updating
    private int blocksToPlace = 0;
    private boolean updateRequested;

    public boolean isServerNetwork(IGrid grid){
        if(grid != null){
            IReadOnlyCollection<Class<? extends IGridHost>> collection = grid.getMachinesClasses();

            return collection.contains(TileServerCore.class);
        }
        return false;
    }

    public void addSlave(AIMultiBlockTile slave) {
        slaves.add(slave);
    }

    public void activate(EntityPlayer p) {
        // Open GUI
        AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiServerStorage, p, AEPartLocation.INTERNAL, pos);
    }

    public void requestUpdate(){

        updateRequested = true;
    }

    int getNextNetID(){
        return this.AVAILABLE_ID++;
    }

    public void destroyMultiBlock(){
        // Iterate for each slave
        for(IAIMultiBlock tile : slaves){
            // Nullify master
            tile.setMaster(null);

            // Destroy node of slave
            ((AIMultiBlockTile)tile).destroyAENode();
        }

        // Iterate fore each side
        for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS){
            // Remove each side networks
            portNetworks.remove(side);
        }

        // Nullify network
        mainNetwork = null;

        // Nullify map
        networkIDMap = new LinkedHashMap<>();

        // Make server not formed
        isFormed = false;
    }

    private TileServerPort getPortAtSide(AEPartLocation side) {
        // Iterate for each slave
        for (IAIMultiBlock slave : slaves){
            // Check if slave is port
            if (slave instanceof TileServerPort){
                TileServerPort port = (TileServerPort) slave;

                if (port.getSideVector() == side)
                    return port;
            }
        }

        return null;
    }

    public void postNetworkCellEvents() {
        // Iterate for each side
        for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
            // Check not null
            if (portNetworks.get(side) == null)
                continue;

            // Post cell event for network at this side
            postCellEvent(portNetworks.get(side));
        }

        // Notify server-networks
        postCellEvent();
    }

    @Override
    public void update() {
        super.update();
        if (isFormed) {
            if(!networkIDMap.containsValue(RESERVED_MASTER_ID) && mainNetwork != null){
                networkIDMap.put(mainNetwork,RESERVED_MASTER_ID);
            }

            if(updateRequested){
                Gui g = Minecraft.getMinecraft().currentScreen;
                if(g instanceof GuiServerTerminal){
                    GuiServerTerminal SPT = (GuiServerTerminal)g;
                    SPT.mInstance = this;

                    updateRequested = false;
                }
            }

        }
    }

    @Override
    public void notifyBlock(){

    }

    @Override
    public void tryConstruct(EntityPlayer p) {
        // Check if multi block isn't formed yet
        if(!isFormed) {
            // For 4d humans =)
            blocksToPlace = BLOCKS_IN_STRUCTURE - 1;

            // Count of blocks matched the pattern. Atomic, because it accessed by lambda function
            AtomicInteger count = new AtomicInteger();

            // Create list of blocks to update later
            List<IAIMultiBlock> toUpdate;

            // Call method of utils
            toUpdate = MultiBlockUtils.fillListWithPattern(AIPatterns.ME_SERVER, this, (block) -> count.getAndIncrement());

            // Count blocks in pattern
            int counter = 0;

            // Iterate for length of pattern
            for (int i = 0; i < AIPatterns.ME_SERVER.length; i++)
                // Check not null
                if (AIPatterns.ME_SERVER[i] != null)
                    // Add to counter
                    counter++;


            int blocksToPlace = AIPatterns.ME_SERVER_FILL.length - 1;
            if (counter == count.get()) {
                for (int i = 0; i < AIPatterns.ME_SERVER_FILL.length; i++) {
                    int x, y, z;
                    Block block = AIPatterns.ME_SERVER_FILL[blocksToPlace].b;

                    x = this.pos.getX() + AIPatterns.ME_SERVER_FILL[blocksToPlace].x;
                    y = this.pos.getY() + AIPatterns.ME_SERVER_FILL[blocksToPlace].y;
                    z = this.pos.getZ() + AIPatterns.ME_SERVER_FILL[blocksToPlace].z;

                    world.setBlockToAir(new BlockPos(x, y, z));
                    world.setBlockState(new BlockPos(x, y, z), block.getDefaultState());
                    toUpdate.add((TileServerRib) world.getTileEntity(new BlockPos(x,y,z)));
                    blocksToPlace--;
                }

                // Iterate for each block to update
                for (IAIMultiBlock slave : toUpdate) {
                    // Set slave master
                    slave.setMaster(this);

                    // Check for instance of port
                    if (slave instanceof TileServerPort) {
                        // Get port
                        TileServerPort port = (TileServerPort) slave;
                        // Create node
                        port.createAENode();

                    // Check for instance of rib
                    } else if (slave instanceof TileServerRib) {
                        // Get rib
                        TileServerRib rib = (TileServerRib) slave;

                        // Create node
                        rib.createAENode();

                        //rib.getWorld().setBlockState(rib.getPos(), rib.getWorld().getBlockState().withProperty());
                    }

                    // Add to salve list
                    slaves.add(slave);
                }

                // Iterate for each side
                for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS){
                    // get tile with double offset from this side
                    TileEntity tile = world.getTileEntity(new BlockPos(getPos().getX()+side.xOffset*2,getPos().getY()+side.yOffset*2,getPos().getZ()+side.zOffset*2));

                    // Check for instanceof port
                    if(tile instanceof TileServerPort){
                        // Get port
                        TileServerPort port = (TileServerPort)tile;

                        // Set proper direction
                        port.setDir(side.getFacing());

                        // Update grid of port
                        port.onNeighborChange();
                    }
                }
            }

            isFormed = true;
            if(p!=null)
                p.sendMessage(new TextComponentTranslation("ME Server Formed!"));
        }
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
    public void setMaster(IMaster tileServerCore) { }

    @Override
    public Object getServerGuiElement( final EntityPlayer player ) {
        return new ContainerMEServer(player,this);
    }

    @Override
    public Object getClientGuiElement( final EntityPlayer player ) {
        return new GuiMEServer((ContainerMEServer)this.getServerGuiElement(player), player);
    }

    @Override
    public void blinkCell(int slot) {
    }


    @Override
    public void securityBreak() {

    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (world != null && !world.isRemote) {
            destroyAENode();
        } if(isFormed)
            this.destroyMultiBlock();
    }

    @Nonnull
    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    // -----------------------------Drive Methods-----------------------------//
    @Override
    public int getPriority() {
        return 0;
    }

    private void onCardRemove(ItemStack card) {
        // Get tag
        NBTTagCompound tag = Platform.openNbtData(card);

        // Get side
        AEPartLocation side = AEPartLocation.values()[tag.getInteger(NetworkCard.NBT_KEY_NET_SIDE)];

        // Get port
        TileServerPort port = getPortAtSide(side);

        // Check not null
        if (port == null)
            // Skip
            return;

        // Nullify handlers for this port
        handlers.put(side, null);

        // Notify grid of current port
        port.postCellEvent();

        // Notify our grid also
        postCellEvent();
    }

    @Override
    public void onInventoryChanged() {
        // Iterate for each stack in cards inventory
        for (ItemStack stack : cardInv.slots){
            // Check if item in stack is network card
            if (stack.getItem() instanceof NetworkCard) {
                // Get tag
                NBTTagCompound tag = Platform.openNbtData(stack);

                // Get decoded pair from card
                Pair<LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, List<IAEStack<? extends IAEStack>>>>,
                        LinkedHashMap<SecurityPermissions, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IncludeExclude>>> data = NetworkCard.decodeDataFromTag(tag);

                // Get side
                AEPartLocation side = AEPartLocation.values()[tag.getInteger(NetworkCard.NBT_KEY_NET_SIDE)];

                // Get port
                TileServerPort port = getPortAtSide(side);

                // Check not null
                if (port == null)
                    // Skip
                    continue;

                // Create list
                LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler> handlers = new LinkedHashMap<>();

                // Iterate for each channel
                GuiStorageChannelButton.getChannelList().forEach(channel -> {
                    // Get inventory
                    IMEInventory<?> inventory = ((IStorageGrid)mainNetwork.getCache(IStorageGrid.class)).getInventory(channel);

                    try {
                        // Encode new handler from channel into map
                        handlers.put(channel, Objects.requireNonNull(AIApi.instance()).getHandlerFromChannel(channel).newInstance(
                                              data.getLeft(),
                                              data.getRight(),
                                              inventory));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalStateException("Unexpected Error");
                    }
                });

                // Encode new handler for side from card
                this.handlers.put(side, handlers);

                // Notify grid of current port
                port.postCellEvent();
            }
        }

        // Notify our grid also
        postCellEvent();
    }

    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel channel) {
        if (!gridNode.isActive())
            return new ArrayList<>();
        return channel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class) ? this.items : this.fluids;
    }

    @Override
    public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {
        // Check if inventory not null
        if (iCellInventory != null)
            // Persist inventory
            iCellInventory.persist();

        // Mark dirty
        getWorld().markChunkDirty(this.getPos(), this);
    }

    public List<IMEInventoryHandler> getSidedCellArray(AEPartLocation side, IStorageChannel<?> channel) {
        // Check if handler not null
        if (handlers.get(side) == null)
            return new LinkedList<>();

        // Return only one handler for tile
        return Collections.singletonList(handlers.get(side).get(channel));
    }


    public void saveSidedChanges(ICellInventory<?> iCellInventory, AEPartLocation side) {
        // Check if inventory not null
        if (iCellInventory != null)
            // Persist inventory
            iCellInventory.persist();

        // Get port
        TileServerPort port = getPortAtSide(side);

        // Check not null
        if (port == null)
            return;

        // Mark dirty
        getWorld().markChunkDirty(port.getPos(), port);
    }
    // -----------------------------Drive Methods-----------------------------//

    @Override
    public boolean showNetworkInfo(RayTraceResult rayTraceResult) {
        return false;
    }

    @Override
    public Iterator<IGridNode> getMultiblockNodes() {
        return null;
    }
}

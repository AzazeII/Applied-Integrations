package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Container.tile.Server.ContainerMEServer;
import AppliedIntegrations.Gui.ServerGUI.GuiMEServer;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import AppliedIntegrations.Gui.ServerGUI.SubGui.NetworkData;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketMEServer;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.MultiBlockUtils;
import AppliedIntegrations.api.IInventoryHost;
import AppliedIntegrations.tile.*;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.storage.*;
import appeng.api.storage.channels.IFluidStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.util.AEPartLocation;
import appeng.api.util.INetworkToolAgent;
import appeng.api.util.IReadOnlyCollection;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public AIGridNodeInventory inv = new AIGridNodeInventory("ME Server",30,1,this){
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
    public LinkedHashMap<EnumFacing,IGrid> portNetworks = new LinkedHashMap<>();


    // Network of owner
    public IGrid mainNetwork;

    public boolean isFormed;

    private byte[] cellStatuses = new byte[6];

    // Wait for updating
    private int blocksToPlace = 0;
    private boolean updateRequested;

    public void requestUpdate(){

        updateRequested = true;
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
                        port.updateGrid();
                    }
                }
            }

            isFormed = true;
            if(p!=null)
                p.sendMessage(new TextComponentTranslation("ME Server Formed!"));
        }
    }

    int getNextNetID(){
        return this.AVAILABLE_ID++;
    }

    public void destoryMultiBlock(){
        for(IAIMultiBlock tile : slaves){
            if(tile instanceof TileServerRib){
                TileServerRib rib = (TileServerRib)tile;
                rib.changeAlt(false);
            }

            if(tile instanceof TileServerPort){
                TileServerPort port = (TileServerPort)tile;
            }
            tile.setMaster(null);
            ((AIMultiBlockTile)tile).destroyAENode();
        }
        for(EnumFacing dir : EnumFacing.values()){
            portNetworks.remove(dir);
        }

        mainNetwork = null;

        networkIDMap = new LinkedHashMap<>();

        isFormed = false;

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

    @Nonnull
    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }


    // Drive
    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onInventoryChanged() {
        this.items = updateHandlers(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
        this.fluids = updateHandlers(AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));
        for (int i = 0; i < this.cellStatuses.length; i++) {
            ItemStack stackInSlot = this.inv.getStackInSlot(i);
            IMEInventoryHandler inventoryHandler = AEApi.instance()
                    .registries().cell()
                    .getCellInventory(stackInSlot, null, AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));
            if (inventoryHandler == null)
                inventoryHandler = AEApi.instance().registries().cell().getCellInventory(stackInSlot, null,
                               AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class));

            ICellHandler cellHandler = AEApi.instance().registries().cell()
                    .getHandler(stackInSlot);
            if (cellHandler == null || inventoryHandler == null) {
                this.cellStatuses[i] = 0;
            } else {
                //this.cellStatuses[i] = (byte) cellHandler.getStatusForCell(
                 ///       stackInSlot, new BasicCellInventoryHandler<T>(inventoryHandler, AEApi.instance().storage().getStorageChannel(IFluidStorageChannel.class)));
            }
        }
        IGridNode node = getGridNode(AEPartLocation.INTERNAL);
        if (node != null) {
            IGrid grid = node.getGrid();
            grid.postEvent(new MENetworkCellArrayUpdate());
        }
    }

    private List<IMEInventoryHandler> updateHandlers(IStorageChannel channel) {
        ICellRegistry cellRegistry = AEApi.instance().registries().cell();
        List<IMEInventoryHandler> handlers = new ArrayList<>();
        for (int i = 0; i < this.inv.getSizeInventory(); i++) {
            ItemStack cell = this.inv.getStackInSlot(i);
            if (cellRegistry.isCellHandled(cell)) {
                IMEInventoryHandler cellInventory = cellRegistry
                        .getCellInventory(cell, null, channel);
                if (cellInventory != null)
                    handlers.add(cellInventory);
            }
        }
        return handlers;
    }
    @Override
    public List<IMEInventoryHandler> getCellArray(IStorageChannel channel) {
        if (!gridNode.isActive())
            return new ArrayList<>();
        return channel == AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class) ? this.items : this.fluids;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (world != null && !world.isRemote) {
            destroyAENode();
        }
        if(isFormed)
            this.destoryMultiBlock();

    }

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

    public void updateGUI() {
        // Check if multiblock is formed
        if(isFormed) {
            // Check if server network map has reserved master id
            if (networkIDMap.containsValue(RESERVED_MASTER_ID)) {

                // TODO Notify only listeners
                // Notify everyone about GUI change
                NetworkHandler.sendToAll(new PacketMEServer(new NetworkData(true, AEPartLocation.INTERNAL, RESERVED_MASTER_ID),
                        getPos().getX(), getPos().getY(), getPos().getZ(),world));

                // Iterate for each side
                for (AEPartLocation dir : AEPartLocation.SIDE_LOCATIONS) {
                    // Get grid from this direction
                    IGrid grid = portNetworks.get(dir.getFacing());

                    // Check if this grid is contained in network -> id map
                    if(networkIDMap.get(grid) != null) {
                        // Check if id of this grid isn't reserved
                        if (networkIDMap.get(grid) != RESERVED_MASTER_ID) {
                            // Notify client about this network
                            NetworkHandler.sendToAll(new PacketMEServer(new NetworkData( isServerNetwork(grid), dir, networkIDMap.get(grid)),
                                    getPos().getX(), getPos().getY(), getPos().getZ(),world));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void saveChanges(@Nullable ICellInventory<?> iCellInventory) {

    }

    @Override
    public boolean showNetworkInfo(RayTraceResult rayTraceResult) {
        return false;
    }

    @Override
    public Iterator<IGridNode> getMultiblockNodes() {
        return null;
    }
}

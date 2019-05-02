package AppliedIntegrations.tile.Server;

import AppliedIntegrations.api.IInventoryHost;
import AppliedIntegrations.api.Multiblocks.BlockType;
import AppliedIntegrations.tile.AIPatterns;
import AppliedIntegrations.Container.tile.Server.ContainerMEServer;
import AppliedIntegrations.tile.AIMultiBlockTile;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.Gui.ServerGUI.GuiMEServer;
import AppliedIntegrations.Gui.ServerGUI.NetworkData;
import AppliedIntegrations.Gui.ServerGUI.NetworkPermissions;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketMEServer;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.tile.IMaster;
import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
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

import javax.annotation.Nullable;
import java.util.*;

/**
 * @Author Azazell
 */
public class TileServerCore extends AITile implements IAIMultiBlock, IMaster, ICellContainer, INetworkToolAgent, IInventoryHost, ITickable {

    public static final int BLOCKS_IN_STRUCTURE = AIPatterns.ME_SERVER.length+1;

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
    public LinkedHashMap<IGrid, Integer> ServerNetworkMap = new LinkedHashMap<>();

    // Networks in ports
    public LinkedHashMap<EnumFacing,IGrid> portNetworks = new LinkedHashMap<>();

    /**
     * Metadata - data of data
     */
    public Vector<NetworkData> dataMap = new Vector<>();

    // Network of owner
    public IGrid MainNetwork;

    public boolean isFormed;

    public boolean isConflicting;

    private short[] blinkTimers;
    private byte[] cellStatuses = new byte[6];

    // Wait for updating
    public int blocksToPlace = 0;
    private boolean updateRequested;
    private LinkedHashMap<IAIMultiBlock, BlockType> archMap = new LinkedHashMap<>();

    public void requestUpdate(){

        updateRequested = true;
    }

    @Override
    public void update() {
        super.update();
        if (isFormed) {
            if(!ServerNetworkMap.containsValue(RESERVED_MASTER_ID) && MainNetwork != null){
                ServerNetworkMap.put(MainNetwork,RESERVED_MASTER_ID);
            }

            for (IAIMultiBlock slave : slaves) {

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

    /*@Override
    public void validate(){
        Timer t = new Timer();

        TimerTask formTile = new TimerTask() {
            @Override
            public void run() {
                if(worldObj != null) {
                    tryToFindCore(null);
                }
            }
        };

        t.schedule(formTile,50);
    }*/

    @Override
    public void notifyBlock(){

    }
    @Override
    public void tryConstruct(EntityPlayer p) {
        // Check if multi block isn't formed yet
        if(!isFormed) {
            // For 4d humans =)
            blocksToPlace = BLOCKS_IN_STRUCTURE - 1;

            // Count of blocks matched the pattern
            int count = 0;

            // Create list of blocks to update later
            List<IAIMultiBlock> toUpdate = new ArrayList<>();

            // Iterate for i >= pattern.len
            for (int i = 0; i < AIPatterns.ME_SERVER.length; i++) {
                // Call on server
                if (!this.world.isRemote) {
                    // Create block and it's coordinates
                    int x, y, z; // (1)
                    Block block; // (2)

                    // Add x, y and z of block in patter to our location
                    x = this.pos.getX() + AIPatterns.ME_SERVER[blocksToPlace - 1].x; // (1)
                    y = this.pos.getY() + AIPatterns.ME_SERVER[blocksToPlace - 1].y; // (2)
                    z = this.pos.getZ() + AIPatterns.ME_SERVER[blocksToPlace - 1].z; // (3)

                    // Get block
                    block = AIPatterns.ME_SERVER[blocksToPlace - 1].b;

                    // Get block in world, and check if it's equal to block in pattern
                    if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == block) {
                        // Add to count
                        count++;

                        // Get tile
                        IAIMultiBlock multiBlock = (IAIMultiBlock) world.getTileEntity(new BlockPos(x, y, z));

                        // Add to list
                        toUpdate.add(multiBlock);

                        // Add to architecture map
                        archMap.put(multiBlock, AIPatterns.ME_SERVER[blocksToPlace - 1].type);

                        // Remove one block to place
                        blocksToPlace--;
                    }
                }
            }

            // Count blocks in pattern
            int counter = 0;

            // Iterate for length of pattern
            for (int i = 0; i < AIPatterns.ME_SERVER.length; i++)
                // Check not null
                if (AIPatterns.ME_SERVER[i] != null)
                    // Add to counter
                    counter++;


            int blocksToPlace = AIPatterns.ME_SERVER_FILL.length - 1;
            if (counter == count) {
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

                        // Check if type of rib in map is corner
                        if (archMap.get(rib) == BlockType.Corner)
                            // Notify rib
                            rib.isCorner = true;

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
                TileServerRib Rib = (TileServerRib)tile;
                Rib.changeAlt(false);
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

        MainNetwork = null;

        ServerNetworkMap = new LinkedHashMap<>();

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
        if (slot > 0 && slot < this.blinkTimers.length)
            this.blinkTimers[slot] = 15;
    }


    @Override
    public void securityBreak() {

    }

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
                inventoryHandler = AEApi
                        .instance()
                        .registries()
                        .cell()
                        .getCellInventory(stackInSlot, null,
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
            if (grid != null) {
                grid.postEvent(new MENetworkCellArrayUpdate());
            }
        }
    }

    private List<IMEInventoryHandler> updateHandlers(IStorageChannel channel) {
        ICellRegistry cellRegistry = AEApi.instance().registries().cell();
        List<IMEInventoryHandler> handlers = new ArrayList<IMEInventoryHandler>();
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
            return new ArrayList<IMEInventoryHandler>();
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

    public void updateGUI(TileServerSecurity sender) {
        if(isFormed) {
            if (ServerNetworkMap.containsValue(RESERVED_MASTER_ID)) {
                NetworkHandler.sendToAll(new PacketMEServer(new NetworkData(true, AEPartLocation.INTERNAL, RESERVED_MASTER_ID),
                        getPos().getX(), getPos().getY(), getPos().getZ(),world));
                for (AEPartLocation dir : AEPartLocation.SIDE_LOCATIONS) {
                    IGrid grid = portNetworks.get(dir);
                    if(ServerNetworkMap.get(grid) != null) {
                        if (ServerNetworkMap.get(grid) != RESERVED_MASTER_ID) {
                            NetworkHandler.sendToAll(new PacketMEServer(new NetworkData( isServerNetwork(grid), dir, ServerNetworkMap.get(grid)),
                                    getPos().getX(), getPos().getY(), getPos().getZ(),world));
                        }
                    }
                }
            }
        }
    }

    public void onFeedback(boolean Connected, int serverID, AEPartLocation port, LinkedHashMap<SecurityPermissions,NetworkPermissions> networkPermissions) { }

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

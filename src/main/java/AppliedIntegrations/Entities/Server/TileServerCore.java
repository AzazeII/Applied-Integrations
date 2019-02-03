package AppliedIntegrations.Entities.Server;

import AppliedIntegrations.API.IInventoryHost;
import AppliedIntegrations.API.Multiblocks.Patterns;
import AppliedIntegrations.Container.Server.ContainerMEServer;
import AppliedIntegrations.Entities.AIMultiBlockTile;
import AppliedIntegrations.Entities.AITile;
import AppliedIntegrations.Entities.IAIMultiBlock;
import AppliedIntegrations.Gui.ServerGUI.GuiMEServer;
import AppliedIntegrations.Gui.ServerGUI.NetworkData;
import AppliedIntegrations.Gui.ServerGUI.NetworkPermissions;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketMEServer;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.*;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.storage.*;
import appeng.api.util.AEPartLocation;
import appeng.api.util.INetworkToolAgent;
import appeng.api.util.IReadOnlyCollection;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class TileServerCore extends AITile implements IAIMultiBlock, ICellContainer, INetworkToolAgent, IInventoryHost {

    public static final int BLOCKS_IN_STRUCTURE = Patterns.ME_SERVER.length+1;

    private static final int RESERVED_MASTER_ID = 1;
    private int AVAILABLE_ID = RESERVED_MASTER_ID+1;

    // List of blocks in multiblock
    public Vector<IAIMultiBlock> Slaves = new Vector<>();

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

    public void requestUpdate(){

        updateRequested = true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (isFormed) {
            if(!ServerNetworkMap.containsValue(RESERVED_MASTER_ID) && MainNetwork != null){
                ServerNetworkMap.put(MainNetwork,RESERVED_MASTER_ID);
            }

            for (IAIMultiBlock slave : Slaves) {

            }
            if(updateRequested){
                Gui g = Minecraft.getMinecraft().currentScreen;
                if(g instanceof ServerPacketTracer){
                    ServerPacketTracer SPT = (ServerPacketTracer)g;
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
                    tryConstruct(null);
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
        if(!isFormed) {
            // For 4d humans =)
            blocksToPlace = BLOCKS_IN_STRUCTURE - 1;
            int count = 0;
            List<IAIMultiBlock> toUpdate = new ArrayList<>();
            for (int i = 0; i < Patterns.ME_SERVER.length; i++) {
                if (!this.world.isRemote) {
                    int x, y, z;
                    Block block;

                    x = this.pos.getX() + Patterns.ME_SERVER[blocksToPlace - 1].x;
                    y = this.pos.getY() + Patterns.ME_SERVER[blocksToPlace - 1].y;
                    z = this.pos.getZ() + Patterns.ME_SERVER[blocksToPlace - 1].z;
                    block = Patterns.ME_SERVER[blocksToPlace - 1].b;
                    if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == block) {
                        count++;
                        toUpdate.add((IAIMultiBlock) world.getTileEntity(new BlockPos(x, y, z)));
                        blocksToPlace--;
                    }
                }
            }
            int counter = 0;
            for (int i = 0; i < Patterns.ME_SERVER.length; i++)
                if (Patterns.ME_SERVER[i] != null)
                    counter++;
            int BlocksToPlace = Patterns.ME_SERVER_FILL.length - 1;
            if (counter == count) {
                for (int i = 0; i < Patterns.ME_SERVER_FILL.length; i++) {
                    int x, y, z, meta;
                    Block block = Patterns.ME_SERVER_FILL[BlocksToPlace].b;

                    x = this.pos.getX() + Patterns.ME_SERVER_FILL[BlocksToPlace].x;
                    y = this.pos.getY() + Patterns.ME_SERVER_FILL[BlocksToPlace].y;
                    z = this.pos.getZ() + Patterns.ME_SERVER_FILL[BlocksToPlace].z;
                    meta = Patterns.ME_SERVER_FILL[BlocksToPlace].meta;

                    world.setBlockState(new BlockPos(x, y, z), Blocks.AIR);
                    world.setBlockState(new BlockPos(x, y, z), block, meta, meta);
                    toUpdate.add((TileServerRib) world.getTileEntity(x, y, z));
                    ((TileServerRib) world.getTileEntity(new BlockPos(x, y, z))).changeAlt(true);
                    BlocksToPlace--;
                }
                for (int i = 0; i < toUpdate.size(); i++) {
                    toUpdate.get(i).setMaster(this);
                    if (toUpdate.get(i) instanceof TileServerPort) {
                        TileServerPort port = (TileServerPort) toUpdate.get(i);
                        port.createAELink();
                    } else if (toUpdate.get(i) instanceof TileServerRib) {
                        TileServerRib rib = (TileServerRib) toUpdate.get(i);
                        rib.createAELink();
                    }
                    Slaves.add(toUpdate.get(i));
                }
                for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS){
                    TileEntity tile = world.getTileEntity(new BlockPos(getPos().getX()+side.xOffset*2,getPos().getY()+side.yOffset*2,getPos().getZ()+side.zOffset*2));
                    if(tile instanceof TileServerPort){
                        TileServerPort port = (TileServerPort)tile;
                        port.setDir(side);
                        port.updateGrid();
                    }
                }
            }

            isFormed = true;
            if(p!=null)
                p.addChatComponentMessage(new ChatComponentText("ME Server Formed!"));
        }
    }

    public int getNextNetID(){
        return this.AVAILABLE_ID++;
    }

    public void DestroyMultiBlock(){
        for(IAIMultiBlock tile : Slaves){
            if(tile instanceof TileServerRib){
                TileServerRib Rib = (TileServerRib)tile;
                Rib.changeAlt(false);
            }
            if(tile instanceof TileServerPort){
                TileServerPort port = (TileServerPort)tile;
            }
            tile.setMaster(null);
            ((AIMultiBlockTile)tile).destroyAELink();
        }
        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS){
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
    public TileServerCore getMaster() {
        return this;
    }

    @Override
    public void setMaster(TileServerCore tileServerCore) { }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }
    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
    }
    @Override
    public Object getServerGuiElement( final EntityPlayer player ) {
        return new ContainerMEServer(player,this);
    }
    @Override
    public Object getClientGuiElement( final EntityPlayer player )
    {
        return new GuiMEServer((ContainerMEServer)this.getServerGuiElement(player));
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
        this.items = updateHandlers(StorageChannel.ITEMS);
        this.fluids = updateHandlers(StorageChannel.FLUIDS);
        for (int i = 0; i < this.cellStatuses.length; i++) {
            ItemStack stackInSlot = this.inv.getStackInSlot(i);
            IMEInventoryHandler inventoryHandler = AEApi.instance()
                    .registries().cell()
                    .getCellInventory(stackInSlot, null, StorageChannel.ITEMS);
            if (inventoryHandler == null)
                inventoryHandler = AEApi
                        .instance()
                        .registries()
                        .cell()
                        .getCellInventory(stackInSlot, null,
                                StorageChannel.FLUIDS);

            ICellHandler cellHandler = AEApi.instance().registries().cell()
                    .getHandler(stackInSlot);
            if (cellHandler == null || inventoryHandler == null) {
                this.cellStatuses[i] = 0;
            } else {
                this.cellStatuses[i] = (byte) cellHandler.getStatusForCell(
                        stackInSlot, inventoryHandler);
            }
        }
        IGridNode node = getGridNode(ForgeDirection.UNKNOWN);
        if (node != null) {
            IGrid grid = node.getGrid();
            if (grid != null) {
                grid.postEvent(new MENetworkCellArrayUpdate());
            }
            getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    private List<IMEInventoryHandler> updateHandlers(StorageChannel channel) {
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
    public List<IMEInventoryHandler> getCellArray(StorageChannel channel) {
        if (!theGridNode.isActive())
            return new ArrayList<IMEInventoryHandler>();
        return channel == StorageChannel.ITEMS ? this.items : this.fluids;
    }

    @Override
    public void saveChanges(IMEInventory cellInventory) {

    }
    @Override
    public void invalidate() {
        super.invalidate();
        if (world != null && !world.isRemote) {
            destroyAELink();
        }
        if(isFormed)
            this.DestroyMultiBlock();

    }

    @Override
    public boolean showNetworkInfo(MovingObjectPosition where) {
        return true;
    }

    public boolean isServerNetwork(IGrid grid){
        if(grid != null){
            IReadOnlyCollection<Class<? extends IGridHost>> collection = grid.getMachinesClasses();

            return collection.contains(TileServerCore.class);
        }
        return false;
    }

    public void addSlave(AIMultiBlockTile slave) {
        Slaves.add(slave);
    }

    public void updateGUI(TileServerSecurity sender) {
        if(isFormed) {
            if (ServerNetworkMap.containsValue(RESERVED_MASTER_ID)) {
                NetworkHandler.sendToAll(new PacketMEServer(new NetworkData(true, AEPartLocation.INTERNAL, RESERVED_MASTER_ID),xCoord,yCoord,zCoord,worldObj));
                for (AEPartLocation dir : AEPartLocation.SIDE_LOCATIONS) {
                    IGrid grid = portNetworks.get(dir);
                    if(ServerNetworkMap.get(grid) != null) {
                        if (ServerNetworkMap.get(grid) != RESERVED_MASTER_ID) {
                            NetworkHandler.sendToAll(new PacketMEServer(new NetworkData( isServerNetwork(grid), dir, ServerNetworkMap.get(grid)),xCoord,yCoord,zCoord,worldObj));
                        }
                    }
                }
            }
        }
    }


    public void onFeedback(boolean Connected, int serverID, AEPartLocation port, LinkedHashMap<SecurityPermissions,NetworkPermissions> networkPermissions) {
        if(Connected){

        }else{

        }
    }
}

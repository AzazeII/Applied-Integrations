package AppliedIntegrations.Entities.Server;

import AppliedIntegrations.API.IInventoryHost;
import AppliedIntegrations.API.Multiblocks.Patterns;
import AppliedIntegrations.Container.ContainerMEServer;
import AppliedIntegrations.Entities.AIMultiBlockTile;
import AppliedIntegrations.Entities.AITile;
import AppliedIntegrations.Entities.IAIMultiBlock;
import AppliedIntegrations.Gui.ServerGUI.GuiMEServer;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.api.AEApi;
import appeng.api.networking.*;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.storage.*;
import appeng.api.util.AECableType;
import appeng.api.util.INetworkToolAgent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class TileServerCore extends AITile implements IAIMultiBlock, ICellContainer, INetworkToolAgent, IInventoryHost {

    public static final int BLOCKS_IN_STRUCTURE = Patterns.ME_SERVER.length+1;
    // List of blocks in multiblock
    public Vector<IAIMultiBlock> Slaves = new Vector<>();

    public AIGridNodeInventory inv = new AIGridNodeInventory("ME Server",30,1,this);

    // ME Inventories
    private List<IMEInventoryHandler> items = new LinkedList<>();
    private List<IMEInventoryHandler> fluids = new LinkedList<>();

    // Networks in ports
    public Vector<IGrid> portNetworks = new Vector<>();
    // Network of owner
    public IGrid MainNetwork;

    private boolean tryForm;
    public boolean isFormed;

    public boolean isConflicting;

    private short[] blinkTimers;
    private byte[] cellStatuses = new byte[6];

    // Wait for updating
    public int blocksToPlace = 0;


    @Override
    public void updateEntity() {
        super.updateEntity();
        if (isFormed) {
            for (IAIMultiBlock slave : Slaves) {
                slave.notifyBlock();
            }
            /*if (MainNetwork != null) {
                for (IGrid grid : portNetworks) {
                    if (grid == MainNetwork) {
                        for (IAIMultiBlock slave : Slaves) {
                            if (slave instanceof TileServerRib) {
                                if (!isConflicting) {
                                    TileServerRib rib = (TileServerRib) slave;
                                    rib.notifyConflict();
                                    isConflicting = true;
                                    return;
                                }
                            }
                        }
                    }
                }

            }*/

        /*if(tryForm){
            isFormed = false;
            if(worldObj != null) {
                this.tryConstruct(null);
                tryForm = false;
            }
        }*/
        }
    }
    @Override
    public void validate(){
        tryForm = true;
    }
    @Override
    public void notifyBlock(){
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
    }
    @Override
    public void tryConstruct(EntityPlayer p) {
        if(!isFormed) {
            // For 4d humans =)
            blocksToPlace = BLOCKS_IN_STRUCTURE - 1;
            int count = 0;
            List<IAIMultiBlock> toUpdate = new ArrayList<>();
            for (int i = 0; i < Patterns.ME_SERVER.length; i++) {
                if (!this.worldObj.isRemote) {
                    int x, y, z;
                    Block block;

                    x = this.xCoord + Patterns.ME_SERVER[blocksToPlace - 1].x;
                    y = this.yCoord + Patterns.ME_SERVER[blocksToPlace - 1].y;
                    z = this.zCoord + Patterns.ME_SERVER[blocksToPlace - 1].z;
                    block = Patterns.ME_SERVER[blocksToPlace - 1].b;
                    if (worldObj.getBlock(x, y, z) == block) {
                        count++;
                        toUpdate.add((IAIMultiBlock) worldObj.getTileEntity(x, y, z));
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

                    x = this.xCoord + Patterns.ME_SERVER_FILL[BlocksToPlace].x;
                    y = this.yCoord + Patterns.ME_SERVER_FILL[BlocksToPlace].y;
                    z = this.zCoord + Patterns.ME_SERVER_FILL[BlocksToPlace].z;
                    meta = Patterns.ME_SERVER_FILL[BlocksToPlace].meta;

                    worldObj.setBlock(x, y, z, Blocks.air);
                    worldObj.setBlock(x, y, z, block, meta, meta);
                    worldObj.markBlockForUpdate(x, y, z);
                    toUpdate.add((TileServerRib) worldObj.getTileEntity(x, y, z));
                    ((TileServerRib) worldObj.getTileEntity(x, y, z)).changeAlt(true);
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

            }

            isFormed = true;
            if(p!=null)
                p.addChatComponentMessage(new ChatComponentText("ME Server Formed!"));
        }

    }


    public void DestroyMultiBlock(){
        for(IAIMultiBlock tile : Slaves){
            if(tile instanceof TileServerRib){
                TileServerRib Rib = (TileServerRib)tile;
                Rib.changeAlt(false);
                Rib.destroyAELink();
            }
            if(tile instanceof TileServerPort){
                TileServerPort port = (TileServerPort)tile;
                port.destroyAELink();
            }
            tile.setMaster(null);
            ((AIMultiBlockTile)tile).getGridNode(ForgeDirection.UNKNOWN).updateState();

        }


        isFormed = false;

    }


    public boolean acceptsStack(ItemStack stack){
        return AEApi.instance().registries().cell().isCellHandled(stack);
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
        if(hasMaster())
            return new ContainerMEServer(player,this);
        return null;
    }
    @Override
    public Object getClientGuiElement( final EntityPlayer player )
    {
        if(hasMaster())
            return new GuiMEServer((ContainerMEServer)this.getServerGuiElement(player));
        return null;
    }

    @Override
    public void blinkCell(int slot) {
        if (slot > 0 && slot < this.blinkTimers.length)
            this.blinkTimers[slot] = 15;
    }

    @Override
    public IGridNode getActionableNode() {
        return super.getGridNode(ForgeDirection.UNKNOWN);
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        return super.getGridNode(dir);
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return super.getCableConnectionType(dir);
    }

    @Override
    public void securityBreak() {

    }

    @Override
    public EnumSet<ForgeDirection> getConnectableSides() {
        if(isFormed)
            return EnumSet.allOf(ForgeDirection.class);
        return EnumSet.noneOf(ForgeDirection.class);
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
    @MENetworkEventSubscribe
    public void updateChannels(MENetworkChannelsChanged channel) {
        IGridNode node = getGridNode(ForgeDirection.UNKNOWN);
        if (node != null) {
            this.markDirty();
            node.updateState();
        }
        node.getGrid().postEvent(new MENetworkCellArrayUpdate());
    }
    @MENetworkEventSubscribe
    public void powerChange(MENetworkPowerStatusChange event) {
        IGridNode node = getGridNode(ForgeDirection.UNKNOWN);
        if (node != null) {
            this.markDirty();
            node.updateState();
        }
        node.getGrid().postEvent(new MENetworkCellArrayUpdate());
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
        if (worldObj != null && !worldObj.isRemote) {
            destroyAELink();
        }
        if(isFormed)
            this.DestroyMultiBlock();

    }

    @Override
    public boolean showNetworkInfo(MovingObjectPosition where) {
        return true;
    }

    public void addSlave(AIMultiBlockTile slave) {
        Slaves.add(slave);
    }
}

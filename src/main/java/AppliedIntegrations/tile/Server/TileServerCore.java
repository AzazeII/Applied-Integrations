package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Container.tile.Server.ContainerServerCore;
import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Gui.ServerGUI.GuiServerCore;
import AppliedIntegrations.Gui.ServerGUI.SubGui.Buttons.GuiStorageChannelButton;
import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.Utils.MultiBlockUtils;
import AppliedIntegrations.api.AIApi;
import AppliedIntegrations.api.IInventoryHost;
import AppliedIntegrations.tile.AIPatterns;
import AppliedIntegrations.tile.AITile;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import appeng.api.config.IncludeExclude;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import appeng.api.util.INetworkToolAgent;
import appeng.util.Platform;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.tuple.Pair;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Azazell
 */
public class TileServerCore extends AITile implements IAIMultiBlock, IMaster, INetworkToolAgent, ITickable {
    private enum TileTypes {
        HOUSING,
        PORT,
        RIB,
        CORE,
        DRIVE,
        TERMINAL
    }

    private class CardInventoryManager implements IInventoryHost {

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

            // Nullify portHandlers for this port
            portHandlers.put(side, null);

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
                    TileServerCore.this.portHandlers.put(side, handlers);

                    // Notify grid of current port
                    port.postCellEvent();
                }
            }

            // Notify our grid also
            postCellEvent();
        }
    }

    private static final String KEY_FORMED = "#FORMED";
    private boolean constructionRequested;

    private LinkedHashMap<TileTypes, List<AIServerMultiBlockTile>> slaveMap = new LinkedHashMap<>();

    private CardInventoryManager cardManager = new CardInventoryManager();

    private static final int BLOCKS_IN_STRUCTURE = AIPatterns.ME_SERVER.length+1;

    private static final int RESERVED_MASTER_ID = 1;

    // list of blocks in multiblock
    public List<AIServerMultiBlockTile> slaves = new ArrayList<>();

    public AIGridNodeInventory cardInv = new AIGridNodeInventory("Network Card Slots", 30, 1, this.cardManager){
        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return itemstack.getItem() instanceof NetworkCard;
        }

        @Override
        public ItemStack decrStackSize(int slotId, int amount) {
            // Check if slot decreasing is network card
            if (slots[slotId].getItem() instanceof NetworkCard) {
                // Pass call to outer function
                cardManager.onCardRemove(slots[slotId]);
            }

            return super.decrStackSize(slotId, amount);
        }
    };
    // Networks in ports

    public LinkedHashMap<AEPartLocation,IGrid> portNetworks = new LinkedHashMap<>();
    private LinkedHashMap<AEPartLocation, LinkedHashMap<IStorageChannel<? extends IAEStack<?>>, IMEInventoryHandler>> portHandlers = new LinkedHashMap<>();
    // Network of owner

    public IGrid mainNetwork;
    public boolean isFormed;

    {
       nullifyMap();
    }

    void nullifyMap() {
        // Nullify map
        slaveMap = new LinkedHashMap<>();

        // Iterate for each tile type
        for (TileTypes type : TileTypes.values()) {
            // Add list to map
            slaveMap.put(type, new ArrayList<>());
        }
    }

    public void addSlave(AIServerMultiBlockTile slave) {
        slaves.add(slave);
    }

    public void activate(EntityPlayer p) {
        // Open GUI
        AIGuiHandler.open(AIGuiHandler.GuiEnum.GuiServerStorage, p, AEPartLocation.INTERNAL, pos);
    }

    public void destroyMultiBlock(){
        // Iterate for each slave
        for(IAIMultiBlock tile : slaves){
            // Nullify master
            tile.setMaster(null);

            // Destroy node of slave
            ((AIServerMultiBlockTile)tile).destroyAENode();
        }

        // Iterate fore each side
        for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS){
            // Remove each side networks
            portNetworks.remove(side);
        }

        // Nullify slave map
        nullifyMap();

        // Nullify slave list
        slaves = new ArrayList<>();

        // Nullify network
        mainNetwork = null;

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

        // Check if construction was requested from read nbt method
        if (constructionRequested){
            // Try construct server
            tryConstruct(null);

            // Toggle
            constructionRequested = true;
        }
    }

    @Override
    public void notifyBlock(){

    }

    @SuppressWarnings("unchecked")
    @Override
    public void tryConstruct(EntityPlayer p) {
        // Check if multi block isn't formed yet
        if(!isFormed) {
            // Count of blocks matched the pattern. Atomic, because it accessed by lambda function
            AtomicInteger count = new AtomicInteger();

            // Get list of blocks matched the pattern
            formServer((List<AIServerMultiBlockTile>) MultiBlockUtils.fillListWithPattern(AIPatterns.ME_SERVER, this, (block) -> count.getAndIncrement()), count, p);
        }
    }

    private void formServer(List<AIServerMultiBlockTile> toUpdate, AtomicInteger count, EntityPlayer p) {
        // Create blocks to place counter
        int blocksToPlace = AIPatterns.ME_SERVER_FILL.length - 1;

        // Check if length equal to count
        if (AIPatterns.ME_SERVER.length == count.get()) {
            // Iterate until i = len
            for (int i = 0; i < AIPatterns.ME_SERVER_FILL.length; i++) {
                // Create x, y, z
                int x, y, z;

                // Create block
                Block block = AIPatterns.ME_SERVER_FILL[blocksToPlace].b;

                // Initialize x, y, z
                x = this.pos.getX() + AIPatterns.ME_SERVER_FILL[blocksToPlace].x;
                y = this.pos.getY() + AIPatterns.ME_SERVER_FILL[blocksToPlace].y;
                z = this.pos.getZ() + AIPatterns.ME_SERVER_FILL[blocksToPlace].z;

                // Set block state to our state
                world.setBlockState(new BlockPos(x, y, z), block.getDefaultState());

                // Add block to update
                toUpdate.add((TileServerRib) world.getTileEntity(new BlockPos(x,y,z)));

                // Decrease counter
                blocksToPlace--;
            }

            // Iterate for each block to update
            for (AIServerMultiBlockTile slave : toUpdate) {
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

                    // Put in category map
                    slaveMap.get(TileTypes.HOUSING).add(rib);

                    //rib.getWorld().setBlockState(rib.getPos(), rib.getWorld().getBlockState().withProperty());
                } else if (slave instanceof TileServerHousing) {
                    // Get housing
                    TileServerHousing housing = (TileServerHousing) slave;

                    // Put in category map
                    slaveMap.get(TileTypes.HOUSING).add(housing);

                }

                // Add to slave list
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

        // Toggle formed
        isFormed = true;

        // Check not null
        if(p != null)
            // Send message
            p.sendMessage(new TextComponentTranslation("ME Server Formed!"));
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
        return new ContainerServerCore(player,this);
    }

    @Override
    public Object getClientGuiElement( final EntityPlayer player ) {
        return new GuiServerCore((ContainerServerCore)this.getServerGuiElement(player), player);
    }

    @Override
    public void securityBreak() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void invalidate() {
        super.invalidate();
        if (world != null && !world.isRemote)
            destroyAENode();

        if(isFormed)
            this.destroyMultiBlock();

        // Drop items from drive and card inventory
        Platform.spawnDrops(world, pos, Arrays.asList(cardInv.slots)); // Card inv
    }

    @Nonnull
    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    // -----------------------------Drive Methods-----------------------------//
    public List<IMEInventoryHandler> getSidedCellArray(AEPartLocation side, IStorageChannel<?> channel) {
        // Check if handler not null
        if (portHandlers.get(side) == null)
            return new LinkedList<>();

        // Return only one handler for tile
        return Collections.singletonList(portHandlers.get(side).get(channel));
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
}

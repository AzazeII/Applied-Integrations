package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Container.tile.Server.ContainerServerTerminal;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Items.NetworkCard;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.api.Storage.IChannelContainerWidget;
import AppliedIntegrations.api.Storage.IChannelWidget;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.tile.AIMultiBlockTile;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.util.IOrientable;
import appeng.util.item.AEItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Azazell
 */
public class TileServerSecurity extends AIMultiBlockTile implements IOrientable {
    // Used by both container and gui
    public static final int SLOT_Y = 18; // (1)
    public static final int SLOT_X = 9; // (2)
    public static final int SLOT_ROWS = 3; // (3)
    public static final int SLOT_COLUMNS = 9; // (4)

    public List<ContainerServerTerminal> listeners = new LinkedList<>();

    public AIGridNodeInventory editorInv = new AIGridNodeInventory("Network Card Editor", 1, 1){
        @Override
        public boolean isItemValidForSlot(int i, ItemStack itemstack) {
            return itemstack.getItem() instanceof NetworkCard;
        }
    };

    public boolean updateRequested;

    private EnumFacing fw;
    private List<IChannelWidget<?>> filterSlots = new LinkedList<>();

    public void updateCardData(NBTTagCompound tag) {
        // Get inventory
        AIGridNodeInventory inv = this.editorInv;

        // Get stack
        ItemStack stack = inv.getStackInSlot(0);

        // Check if stack has network card item
        if (stack != null && stack.getItem() instanceof NetworkCard){
            // Change NBT tag
            stack.setTagCompound(tag);
        }
    }

    private void initGuiCoordinates() {
        // Iterate for each listener
        for( ContainerServerTerminal listener : listeners){
            // Send update packet
            NetworkHandler.sendTo(new PacketCoordinateInit(this),
                    (EntityPlayerMP)listener.player);

            // Trigger request
            updateRequested = false;
        }
    }

    @Override
    public void update() {
        super.update();

        // Check if tile has master
        if(!hasMaster()){
            // Check not null
            if(gridNode == null)
                return;

            // Get our grid
            IGrid grid = gridNode.getGrid();

            // Iterate for each node of this grid
            for(IGridNode node : grid.getNodes()){
                // Check if node is server core
                if(node.getMachine() instanceof TileServerCore ) {
                    // Cast this node to core
                    TileServerCore master = ((TileServerCore)node.getMachine());

                    // Check if multiblock is formed
                    if(master.isFormed) {
                        // Add this to slave list
                        master.addSlave(this);

                        // Set master
                        setMaster(master);
                    }

                    return;
                }
            }
        }

        // Check if update requested
        if (updateRequested){
            // Check if we have gui to update
            if (Minecraft.getMinecraft().currentScreen instanceof GuiServerTerminal) {
                // Init gui coordinate set
                initGuiCoordinates();
            }
        }
    }

    @Override
    public Object getServerGuiElement( final EntityPlayer player ) {
        return new ContainerServerTerminal((TileServerCore)getMaster(), this, player);
    }

    @Override
    public Object getClientGuiElement( final EntityPlayer player ) {
        return new GuiServerTerminal((ContainerServerTerminal)this.getServerGuiElement(player),player);
    }

    @Override
    public void createAENode() {
        if (!world.isRemote) {
            if (gridNode == null)
                gridNode = AEApi.instance().grid().createGridNode(this);
                gridNode.updateState();
        }
    }

    @Nonnull
    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    @Nonnull
    @Override
    public EnumSet<EnumFacing> getConnectableSides() {
        // TODO Auto-generated method stub
        EnumSet<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);
        for(EnumFacing side : EnumFacing.values()){
            if(side != fw){
                set.add(side);
            }
        }
        return set;
    }

    @Override
    public void invalidate() {
        if (world != null && !world.isRemote) {
            destroyAENode();
        }
        if(hasMaster()){
            ((TileServerCore)getMaster()).slaves.remove(this);
            master.mainNetwork = null;
        }
    }

    public void notifyBlock(){

    }

    @Override
    public boolean canBeRotated() {
        return true;
    }

    @Override
    public EnumFacing getForward() {
        return null;
    }

    @Override
    public EnumFacing getUp() {
        return null;
    }

    @Override
    public void setOrientation(EnumFacing Forward, EnumFacing Up) {

    }

    public void readFromNBT(NBTTagCompound compound) {
        // Read inventory
        editorInv.readFromNBT(compound.getTagList("#upgradeInventory", 10));

        super.readFromNBT(compound);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        // Write inventory
        tag.setTag("#upgradeInventory", editorInv.writeToNBT());

        return super.writeToNBT(tag);
    }

    // ------# Used by packets to sync slots by identifier #------ //
    public void addWidgetSlotLink(IChannelWidget<?> widget) {
        this.filterSlots.add(widget);
    }

    public void updateWidgetSlotLink(int x, int y, ItemStack stack) {
        // Iterate for each widget
        this.filterSlots.forEach((widget -> {
            // Check if widget is under mouse
            if (widget.isMouseOverWidget(x, y)) {
                // Update stack
                widget.setAEStack(AEItemStack.fromItemStack(stack));
            }
        }));
    }
    // ------# Used by packets to sync slots by identifier #------ //
}

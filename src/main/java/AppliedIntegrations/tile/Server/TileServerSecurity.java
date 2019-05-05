package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Container.tile.Server.ContainerServerTerminal;
import AppliedIntegrations.tile.AIMultiBlockTile;
import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.util.IOrientable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * @Author Azazell
 */
public class TileServerSecurity extends AIMultiBlockTile implements IOrientable {

    private EnumFacing fw;

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

                        // Query gui update
                        master.updateGUI();
                    }

                    return;
                }
            }
        }

    }

    @Override
    public Object getServerGuiElement( final EntityPlayer player ) {
        return new ContainerServerTerminal((TileServerCore)getMaster(),player);
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
}

package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Blocks.MEServer.BlockServerRib;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.tile.AIMultiBlockTile;
import AppliedIntegrations.tile.IAIMultiBlock;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import net.minecraft.block.Block;
import net.minecraft.util.ITickable;

import java.util.EnumSet;


/**
 * @Author Azazell
 */
public class TileServerRib extends AIMultiBlockTile implements IAIMultiBlock, ITickable {

    // Did activity of grid node changed?
    private ChangeHandler<Boolean> activityChangeHandler = new ChangeHandler<>();

    @Override
    public void update() {
        super.update();

        // Check if grid node is not null
        if (getGridNode() != null)
            // Call onchange of handler
            activityChangeHandler.onChange(getGridNode().isActive(), (activity -> {

            }));

        // Check if structure is formed
        if(hasMaster()){
            // Check if main network is null
            if(((TileServerCore)getMaster()).mainNetwork == null) {
                // Get grid of current node
                IGrid grid = getNetwork();

                // Iterate for each node
                for (IGridNode node : grid.getNodes()) {
                    // CHeck if node isn't equal to mutliblock tile?? WTF, TODO review code here
                    if (!(node.getMachine() instanceof AIMultiBlockTile)) {
                        // Initialize main network of core
                        ((TileServerCore)getMaster()).mainNetwork = grid;
                    }
                }
            }
        }
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.DENSE_CAPACITY);
    }

    public void changeAlt(Boolean alt){
        Block rib = world.getBlockState(pos).getBlock();
        if(rib != null && rib.getClass() == BlockServerRib.class) {
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 0);
        }
    }

}

package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Blocks.MEServer.BlockServerRib;
import AppliedIntegrations.Network.NetworkHandler;
import AppliedIntegrations.Network.Packets.Server.PacketRibSync;
import AppliedIntegrations.Utils.ChangeHandler;
import AppliedIntegrations.tile.AIMultiBlockTile;
import AppliedIntegrations.tile.IAIMultiBlock;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import net.minecraft.block.Block;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.EnumSet;


/**
 * @Author Azazell
 */
public class TileServerRib extends AIMultiBlockTile implements IAIMultiBlock, ITickable {

    // Used only client
    public boolean isActive;

    // Did activity of grid node changed?
    private ChangeHandler<Boolean> activityChangeHandler = new ChangeHandler<>();

    private void notifyListeners() {
        // Sync with client
        NetworkHandler.sendToAllInRange(new PacketRibSync(this, getGridNode().isActive()),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
    }

    @Override
    public void update() {
        super.update();

        // Check if grid node is not null
        if (getGridNode() != null) {
            // Call onchange of handler
            activityChangeHandler.onChange(getGridNode().isActive(), (activity -> {
                // Pass call to function
                notifyListeners();
            }));
        }

        // Check if structure is formed
        if(hasMaster()){
            // Check if main network is null
            if(((TileServerCore)getMaster()).mainNetwork == null) {
                // Get grid of current node
                IGrid grid = getNetwork();

                // Iterate for each node
                for (IGridNode node : grid.getNodes()) {
                    // Check if machine of node isn't equal to mutliblock tile?? WTF, TODO review code here later
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

}

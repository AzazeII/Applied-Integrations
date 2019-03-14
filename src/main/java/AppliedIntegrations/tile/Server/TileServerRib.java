package AppliedIntegrations.tile.Server;

import AppliedIntegrations.Blocks.MEServer.BlockServerRib;
import AppliedIntegrations.tile.AIMultiBlockTile;
import AppliedIntegrations.tile.IAIMultiBlock;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import net.minecraft.block.Block;
import net.minecraft.util.ITickable;

import java.util.EnumSet;


public class TileServerRib extends AIMultiBlockTile implements IAIMultiBlock, ITickable {

    public boolean isBlockNotified;

    @Override
    public void update() {
        super.update();
        if(hasMaster()){
            if(((TileServerCore)getMaster()).MainNetwork == null) {
                IGrid grid = getNetwork();
                for (IGridNode node : grid.getNodes()) {
                    if (!(node.getMachine() instanceof AIMultiBlockTile)) {
                        ((TileServerCore)getMaster()).MainNetwork = grid;
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

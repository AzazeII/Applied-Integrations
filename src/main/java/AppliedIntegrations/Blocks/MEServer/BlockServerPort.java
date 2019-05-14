package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.tile.Server.TileServerPort;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class BlockServerPort extends AIMultiBlock {
    public BlockServerPort(String reg, String unloc) {
        super(reg, unloc);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        // Get port
        TileServerPort port = (TileServerPort)worldIn.getTileEntity(pos);

        // Check not null
        if (port != null) {
            // Pass call to tile
            port.onNeighborChange();
        }
    }
}

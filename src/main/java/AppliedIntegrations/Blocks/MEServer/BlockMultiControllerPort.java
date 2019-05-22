package AppliedIntegrations.Blocks.MEServer;


import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.tile.MultiController.TileMultiControllerPort;
import appeng.me.GridAccessException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class BlockMultiControllerPort extends AIMultiBlock {
	public BlockMultiControllerPort(String reg, String unloc) {

		super(reg, unloc);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		// Get port
		TileMultiControllerPort port = (TileMultiControllerPort) worldIn.getTileEntity(pos);

		// Check not null
		if (port != null) {
			try {
				// Pass call to tile
				port.onNeighborChange();
			} catch (GridAccessException ignored) {}
		}
	}
}

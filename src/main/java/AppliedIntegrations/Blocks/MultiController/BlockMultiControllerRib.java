package AppliedIntegrations.Blocks.MultiController;


import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import AppliedIntegrations.tile.MultiController.TileMultiControllerRib;
import appeng.util.Platform;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class BlockMultiControllerRib extends AIMultiBlock {

	public BlockMultiControllerRib(String reg, String unloc) {
		super(reg, unloc);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		// Make block invisible, and give all render handling to TESR
		return EnumBlockRenderType.INVISIBLE;
	}


	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(world, pos, state, p, hand, facing, hitX, hitY, hitZ);
		if (Platform.isWrench(p, p.getHeldItem(hand), pos)) {
			return false;
		}

		// Delegate call to core when we get formed
		if (!p.isSneaking()) {
			TileMultiControllerRib rib = (TileMultiControllerRib) world.getTileEntity(pos);
			if (rib != null && rib.hasMaster() && Platform.isServer()) {
				TileMultiControllerCore core = (TileMultiControllerCore) rib.getMaster();
				core.activate(p);

				return true;
			}
		}

		return false;
	}
}

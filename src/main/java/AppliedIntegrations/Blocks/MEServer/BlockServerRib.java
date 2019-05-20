package AppliedIntegrations.Blocks.MEServer;


import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.tile.MultiController.TileServerCore;
import AppliedIntegrations.tile.MultiController.TileServerRib;
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
public class BlockServerRib extends AIMultiBlock {

	public BlockServerRib(String reg, String unloc) {
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

		// Check if item held is wrench
		if (Platform.isWrench(p, p.getHeldItem(hand), pos)) {
			return false;
		}

		// Check if player isn't sneaking
		if (!p.isSneaking()) {
			// Get rib
			TileServerRib rib = (TileServerRib) world.getTileEntity(pos);

			// Check not null, has master and call only on server
			if (rib != null && rib.hasMaster() && !world.isRemote) {
				// Get master
				TileServerCore core = (TileServerCore) rib.getMaster();

				// Activate
				core.activate(p);

				return true;
			}
		}

		return false;
	}
}

package AppliedIntegrations.Blocks.Additions;

import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class BlockWhiteHole extends BlockBlackHole {
	public BlockWhiteHole(String blockWhiteHole, String white_hole) {
		super(blockWhiteHole, white_hole);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileWhiteHole();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (!p.isSneaking()) {
			// Pass activated to tile entity ( nothing new :) )
			if (tile instanceof TileWhiteHole) {
				// Pass activate to tile
				return ((TileWhiteHole) tile).activate(world, pos, state, p, hand);
			}
		}
		return false;
	}
}

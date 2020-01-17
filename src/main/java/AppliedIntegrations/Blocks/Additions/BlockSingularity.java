package AppliedIntegrations.Blocks.Additions;


import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class BlockSingularity extends BlockAIRegistrable {
	public BlockSingularity(String registryName, String unlocalizedName) {

		super(registryName, unlocalizedName);
		super.setBlockUnbreakable();
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {

		return new TileBlackHole();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		// Make block invisible, and give all render handling to TESR
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isFullCube(IBlockState iBlockState) {

		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		final float shave = 4.0f / 16.0f;
		return new AxisAlignedBB(shave, shave, shave, 1.0f - shave, 1.0f - shave, 1.0f - shave);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return Block.NULL_AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState iBlockState) {

		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileBlackHole) {
			return ((TileBlackHole) tile).activate(world, pos, state, p, hand);
		}
		return false;
	}
}

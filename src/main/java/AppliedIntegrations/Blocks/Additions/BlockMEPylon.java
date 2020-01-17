package AppliedIntegrations.Blocks.Additions;


import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.*;

/**
 * @Author Azazell
 */
public class BlockMEPylon extends BlockAIRegistrable {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockMEPylon(String registryName, String unlocalizedName) {
		super(registryName, unlocalizedName);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {

		return new TileMEPylon();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		// Set index as meta
		return (state.getValue(FACING)).getIndex();
	}

	@Override
	public boolean isFullCube(IBlockState iBlockState) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState iBlockState) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileMEPylon) {
			return ((TileMEPylon) tile).activate(hand, p);
		}
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (facing == UP || facing == DOWN) {
			return getDefaultState().withProperty(FACING, SOUTH);
		}
		return getDefaultState().withProperty(FACING, facing);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
}

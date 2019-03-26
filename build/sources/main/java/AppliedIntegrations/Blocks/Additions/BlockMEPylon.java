package AppliedIntegrations.Blocks.Additions;

import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
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
    // Facing in world
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
    public boolean isOpaqueCube(IBlockState iBlockState) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        // Add block state
        return new BlockStateContainer(this, new IProperty[] { FACING});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {

        // Get facing from meta
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        // Check if axis present X, or Z axis
        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            // else set facing to north
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }
    @Override
    public int getMetaFromState(IBlockState state) {

        // Set index as meta
        int meta = (state.getValue(FACING)).getIndex();

        return meta;

    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        // Pre check for up or down facing
        if(facing == UP || facing == DOWN)
            // Return state with south as facing
            return getDefaultState().withProperty(FACING, SOUTH);
        // Return state with facing
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public boolean isFullCube(IBlockState iBlockState) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!p.isSneaking()) {
            // Pass activated to tile entity ( nothing new :) )
            if (tile instanceof TileMEPylon) {
                // Pass activate to tile
                return ((TileMEPylon) tile).activate(hand, p);
            }
        }
        return false;
    }
}

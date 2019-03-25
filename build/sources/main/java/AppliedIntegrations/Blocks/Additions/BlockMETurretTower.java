package AppliedIntegrations.Blocks.Additions;

import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.tile.Additions.TileMETurretTower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class BlockMETurretTower extends BlockAIRegistrable {
    public BlockMETurretTower(String registryName, String unloc) {
        super(registryName, unloc);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        // Make block invisible, and give all render handling to TESR
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isOpaqueCube(IBlockState iBlockState) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState iBlockState) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        // don't render bounding box at all
        return new AxisAlignedBB( 0, 0, 0, 0, 0, 0 );
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMETurretTower();
    }
}

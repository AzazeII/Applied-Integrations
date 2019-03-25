package AppliedIntegrations.Blocks.Additions;

import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.tile.Additions.TileMETurretFoundation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class BlockMETurret extends BlockAIRegistrable {

    // Does this block enabled in config?
    public static boolean METurret_Enabled = true;

    public BlockMETurret(String registryName, String unloc) {
        super(registryName, unloc);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMETurretFoundation();
    }

    @Override
    public boolean isOpaqueCube(IBlockState iBlockState) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState iBlockState) {
        return false;
    }
}

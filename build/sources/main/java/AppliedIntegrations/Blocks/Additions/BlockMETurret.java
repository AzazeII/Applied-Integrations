package AppliedIntegrations.Blocks.Additions;

import AppliedIntegrations.AIConfig;
import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.tile.Additions.TileMETurretFoundation;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
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
public class BlockMETurret extends BlockAIRegistrable {

    // Does this block enabled in config?
    public static boolean METurret_Enabled = AIConfig.enableBlackHoleStorage;

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

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        if (!p.isSneaking()) {
            // Pass activated to tile entity ( nothing new :) )
            if (tile instanceof TileMETurretFoundation) {
                // Pass activate to tile
                return ((TileMETurretFoundation) tile).activate(hand, p);
            }
        }
        return false;
    }
}
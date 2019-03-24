package AppliedIntegrations.Blocks.Additions;

import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import AppliedIntegrations.tile.Additions.storage.TileSingularity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockMEPylon extends BlockAIRegistrable {
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
    public boolean isFullCube(IBlockState iBlockState) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = world.getTileEntity(pos);
        // Pass activated to tile entity ( nothing new :) )
        if (tile instanceof TileMEPylon) {
            // Pass activate to tile
            return ((TileMEPylon) tile).activate(p, hand);
        }
        return false;
    }
}

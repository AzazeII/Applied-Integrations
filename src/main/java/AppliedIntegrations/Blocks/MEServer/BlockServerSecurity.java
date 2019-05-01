package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.BlockAIRegistrable;
import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class BlockServerSecurity extends BlockAIRegistrable implements ITileEntityProvider {

    public boolean isActive;

    public BlockServerSecurity(String reg, String unloc) {
        super(reg, unloc);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        // Make block invisible, and give all render handling to TESR
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileServerSecurity();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world,pos,state, p, hand, facing, hitX, hitY, hitZ);
        if(!world.isRemote) {
            if (!p.isSneaking()) {
                p.openGui(AppliedIntegrations.instance, 8, world, pos.getX(), pos.getY(),pos.getZ());

                if(world.getTileEntity(pos)!=null) {
                    TileServerSecurity tile = (TileServerSecurity) world.getTileEntity(pos);

                    if(tile.hasMaster())
                        ((TileServerCore)tile.getMaster()).requestUpdate();
                }
                return true;
            }
        }
        return false;
    }

}

package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.tile.Server.TileServerRib;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * @Author Azazell
 */
public class BlockServerRib extends AIMultiBlock {

    public BlockServerRib(String reg, String unloc) {
        super(reg, unloc);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!p.isSneaking()) {
            TileServerRib rib = (TileServerRib) world.getTileEntity(pos);
            if (rib.hasMaster() && !world.isRemote) {
                try {
                    ((TileServerCore)rib.getMaster()).getWorld().getBlockState(((TileServerCore)rib.getMaster()).getPos()).getBlock().onBlockActivated(world, pos,
                            state, p, EnumHand.MAIN_HAND, facing, hitX, hitY, hitZ);
                }catch(Exception e){
                }

                return true;
            }
        }else{
            final List<ItemStack> list = Lists.newArrayList( appeng.util.Platform.getBlockDrops(world,new BlockPos(pos)) );
            Platform.spawnDrops( world, pos,list);
            world.setBlockToAir(pos);
            return true;
        }
        return false;
    }

}

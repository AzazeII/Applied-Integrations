package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Entities.Server.TileServerRib;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockServerRib extends AIMultiBlock {

    public BlockServerRib() {
        this.setUnlocalizedName("ME Server Rib");
        this.setRegistryName("ServerFrame");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!p.isSneaking()) {
            TileServerRib rib = (TileServerRib) world.getTileEntity(pos);
            if (rib.hasMaster() && !world.isRemote) {
                try {
                    rib.getMaster().getWorld().getBlockState(rib.getMaster().getPos()).getBlock().onBlockActivated(world, pos,
                            state, p, EnumHand.MAIN_HAND, facing, hitX, hitY, hitZ);
                }catch(Exception e){
                }finally {
                    return true;
                }
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

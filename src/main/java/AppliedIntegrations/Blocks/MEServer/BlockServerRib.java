package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Entities.Server.TileServerRib;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float par7, float par8, float par9) {
        if(!p.isSneaking()) {
            TileServerRib rib = (TileServerRib) world.getTileEntity(new BlockPos(x,y,z));
            if (rib.hasMaster() && !world.isRemote) {
                try {
                    rib.getMaster().getWorld().getBlockState(rib.getMaster().getPos()).getBlock().onBlockActivated(world,new BlockPos(x,y,z), p, side, par7, par8, par9);
                }catch(Exception e){
                }finally {
                    return true;
                }
            }
        }else{
            final List<ItemStack> list = Lists.newArrayList( appeng.util.Platform.getBlockDrops(world,x,y,z) );
            Platform.spawnDrops( world, new BlockPos(x,y,z),list);
            world.setBlockToAir(new BlockPos(x,y,z));
            return true;
        }
        return false;
    }

}

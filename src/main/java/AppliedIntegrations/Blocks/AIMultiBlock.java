package AppliedIntegrations.Blocks;

import AppliedIntegrations.Entities.AITile;
import AppliedIntegrations.Entities.IAIMultiBlock;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockObserver;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class AIMultiBlock extends BlockContainer implements ITileEntityProvider {

    protected AIMultiBlock() {
        super(Material.IRON);
        this.setHardness(5F);

    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (Platform.isWrench(p,p.inventory.getCurrentItem(),x,y,z)) {
            if(!p.isSneaking()){
                ((IAIMultiBlock)world.getTileEntity(x,y,z)).tryConstruct(p);
                return true;
            }else{
                final List<ItemStack> list = Lists.newArrayList( Platform.getBlockDrops(world,x,y,z) );
                Platform.spawnDrops( world, x, y, z,list);
                world.setBlockToAir( x, y, z );
                return true;
            }
        }
        return false;
    }
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        for(BlocksEnum b : BlocksEnum.values()){
            if(b.b == this){
                try {
                    return (TileEntity) b.tileEnum.getTileClass().newInstance();
                }catch(Exception e){

                }
            }
        }
        return null;
    }
}

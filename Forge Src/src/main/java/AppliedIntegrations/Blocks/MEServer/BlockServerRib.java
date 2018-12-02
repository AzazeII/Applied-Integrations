package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Entities.Server.TileServerRib;
import appeng.util.Platform;
import com.google.common.collect.Lists;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class BlockServerRib extends AIMultiBlock {
    private static IIcon BasicIcon;
    public boolean isAlt = false;

    private static IIcon altIcon_a;
    private static IIcon altIcon_b;

    private static IIcon iconOff_a;
    private static IIcon iconOff_b;
    private IIcon iconOff;

    public BlockServerRib() {
        this.setBlockName("ME Server Rib");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if(isAlt){
            if(meta == 10){
                return iconOff_a;
            }else if(meta == 20){
                return iconOff_b;
            }else if(meta == 30){
                if(side == 0 || side == 1){
                    return iconOff_b;
                }else{
                    return iconOff_a;
                }
            }else if(meta == 40){
                return iconOff;
            }else if(meta == 1)
                return altIcon_a;
            else if(meta == 2)
                return altIcon_b;
            else if(meta == 3){
             if(side == 0 || side == 1){
                return altIcon_b;
             }else{
                 return altIcon_a;
             }
            }else
                return BasicIcon;
        }else{
            return BasicIcon;
        }
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister IconRegistry) {
        BasicIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":ServerFrame");
        altIcon_a = IconRegistry.registerIcon(AppliedIntegrations.modid+":ServerFrameALT_a");
        altIcon_b = IconRegistry.registerIcon(AppliedIntegrations.modid+":ServerFrameALT_b");

        iconOff = IconRegistry.registerIcon(AppliedIntegrations.modid+":ServerFrameOFF");
        iconOff_a = IconRegistry.registerIcon(AppliedIntegrations.modid+":ServerFrameOFF_a");
        iconOff_b = IconRegistry.registerIcon(AppliedIntegrations.modid+":ServerFrameOFF_b");

    }
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float par7, float par8, float par9) {
        if(!p.isSneaking()) {
            TileServerRib rib = (TileServerRib) world.getTileEntity(x, y, z);
            if (rib.hasMaster() && !world.isRemote) {
                try {
                    rib.getMaster().getWorldObj().getBlock(rib.getMaster().xCoord, rib.getMaster().yCoord, rib.getMaster().zCoord).onBlockActivated(world, x, y, z, p, side, par7, par8, par9);
                }catch(Exception e){
                }finally {
                    return true;
                }
            }
        }else{
            final List<ItemStack> list = Lists.newArrayList( appeng.util.Platform.getBlockDrops(world,x,y,z) );
            Platform.spawnDrops( world, x, y, z,list);
            world.setBlockToAir( x, y, z );
            return true;
        }
        return false;
    }

}

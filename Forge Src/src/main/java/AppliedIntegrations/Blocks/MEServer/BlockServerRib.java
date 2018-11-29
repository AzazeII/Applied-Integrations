package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Entities.Server.TileServerCore;
import AppliedIntegrations.Entities.Server.TileServerRib;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import javafx.application.Platform;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import scala.App;

public class BlockServerRib extends AIMultiBlock {
    private static IIcon BasicIcon;
    public boolean isAlt = false;

    private static IIcon altIcon_a;
    private static IIcon altIcon_b;

    private static IIcon altIconRed_a;
    private static IIcon altIconRed_b;
    private IIcon RedIcon;

    public BlockServerRib() {
        this.setBlockName("ME Server Rib");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if(isAlt){
            if(meta == 1)
                return altIcon_a;
            else if(meta == 2)
                return altIcon_b;
            else if(meta == 3){
             if(side == 0 || side == 1){
                return altIcon_b;
             }else{
                 return altIcon_a;
             }
            }else if(meta == 10){
                return altIconRed_a;
            }else if(meta == 20){
                return altIconRed_a;
            }else if(meta == 30){
                if(side == 0 || side == 1){
                    return altIconRed_b;
                }else{
                    return altIconRed_a;
                }
            }else if(meta == 40){
                if(side == 0 || side == 1){
                    return altIconRed_b;
                }else{
                    return altIconRed_a;
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

        RedIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":ServerFrameR");
        altIconRed_a = IconRegistry.registerIcon(AppliedIntegrations.modid+":ServerFrameALTR_a");
        altIconRed_b = IconRegistry.registerIcon(AppliedIntegrations.modid+":ServerFrameALTR_b");

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
        }
        return false;
    }

}

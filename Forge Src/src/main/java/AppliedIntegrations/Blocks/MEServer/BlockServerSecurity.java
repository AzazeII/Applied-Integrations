package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Entities.Server.TileServerRib;
import AppliedIntegrations.Entities.Server.TileServerSecurity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockServerSecurity extends Block implements ITileEntityProvider {
    private static IIcon SideIcon;
    private static IIcon SideIcon_d;

    private static IIcon SideIcon_l;
    private static IIcon SideIcon_r;

    private static IIcon BottomIcon;

    private static IIcon Monitor_N;
    private static IIcon Monitor_E;
    private static IIcon Monitor_W;
    private static IIcon Monitor_S;

    private static IIcon TopIcon;

    public boolean isActive;
    public ForgeDirection monitorFw = SOUTH;

    public BlockServerSecurity()
    {
        super(Material.iron);
        this.setHardness(5F);
        this.setBlockName("ME Server Security Terminal");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        ForgeDirection Fw;
        if(meta == 1)
            Fw = UP;
        else if(meta == 0)
            Fw = DOWN;
        else
            Fw = ForgeDirection.getOrientation(meta);

        if(dir == Fw){
            if(isActive){
                switch (monitorFw){
                    case NORTH:
                        return Monitor_N;
                    case SOUTH:
                        return Monitor_S;
                    case EAST:
                        return Monitor_E;
                    case WEST:
                        return Monitor_W;

                        default:
                            return Monitor_W;
                }
            }else{
                return TopIcon;
            }
        }else if(dir == Fw.getOpposite()){
            return BottomIcon;
        }else{
            if(dir == UP){
                switch (Fw){
                    case SOUTH:
                        return SideIcon_d;
                    case WEST:
                        return SideIcon_l;
                    case EAST:
                        return SideIcon_r;
                    case NORTH:
                        return SideIcon;
                }
            }
            if(dir == DOWN){
                switch (Fw){
                    case NORTH:
                        return SideIcon;
                    case EAST:
                        return SideIcon_r;
                    case WEST:
                        return SideIcon_l;
                    case SOUTH:
                        return SideIcon_d;
                }
            }
            if(dir == SOUTH){
                switch (Fw){
                    case UP:
                        return SideIcon;
                    case WEST:
                        return SideIcon_l;
                    case EAST:
                        return SideIcon_r;
                    case DOWN:
                        return SideIcon_d;
                }
            }
            if(dir == NORTH){
                switch (Fw){
                    case DOWN:
                        return SideIcon_d;
                    case EAST:
                        return SideIcon_l;
                    case WEST:
                        return SideIcon_r;
                    case UP:
                        return SideIcon;
                }
            }
            if(dir == WEST){
                switch (Fw){
                    case SOUTH:
                        return SideIcon_r;
                    case DOWN:
                        return SideIcon_d;
                    case UP:
                        return SideIcon;
                    case NORTH:
                        return SideIcon_l;
                }
            }
            if(dir == EAST){
                switch (Fw){
                    case SOUTH:
                        return SideIcon_l;
                    case DOWN:
                        return SideIcon_d;
                    case UP:
                        return SideIcon;
                    case NORTH:
                        return SideIcon_r;
                }
            }
        }
        return null;
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister IconRegistry) {
        SideIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecuritySide");

        SideIcon_d = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecuritySide_down");
        SideIcon_r = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecuritySide_right");
        SideIcon_l = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecuritySide_left");

        Monitor_S = TopIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecurityTop_S");
        Monitor_N = TopIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecurityTop_N");
        Monitor_W = TopIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecurityTop_W");
        Monitor_E = TopIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecurityTop_E");

        BottomIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecurityBottom");
        TopIcon = IconRegistry.registerIcon(AppliedIntegrations.modid+":MEServerSecurityTop");
    }
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {

        TileServerSecurity tile = (TileServerSecurity)world.getTileEntity(x,y,z);
        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        boolean save = true;

        if(entity.posY > y){
            tile.fw = UP;
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
            save = false;

        }if(entity.posY < y-1){
            tile.fw = DOWN;
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            save = false;

        }if (l == 0)
        {
            if(save) {
                tile.fw = NORTH;
                world.setBlockMetadataWithNotify(x, y, z, 2, 2);
            }else{
                monitorFw = NORTH;
            }
        }if (l == 1)
        {
            if(save) {
                tile.fw = EAST;
                world.setBlockMetadataWithNotify(x, y, z, 5, 2);
            }else{
                monitorFw = EAST;
            }
        }if (l == 2)
        {
            if(save) {
                tile.fw = SOUTH;
                world.setBlockMetadataWithNotify(x, y, z, 3, 2);
            }else{
                monitorFw = SOUTH;
            }
        }if (l == 3)
        {
            if(save) {
                tile.fw = WEST;
                world.setBlockMetadataWithNotify(x, y, z, 4, 2);
            }else{
                monitorFw = WEST;
            }
        }

    }
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileServerSecurity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float par7, float par8, float par9) {
        super.onBlockActivated(world,x,y,z,p,side,par7,par8,par9);
        if(!p.isSneaking()) {
            p.openGui(AppliedIntegrations.instance, 8, world, x, y, z);
            return true;
        }
        return false;
    }
}

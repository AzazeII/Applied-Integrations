package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Entities.Server.TileServerSecurity;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static net.minecraftforge.common.util.ForgeDirection.*;

public class BlockServerSecurity extends Block implements ITileEntityProvider {

    public boolean isActive;

    public BlockServerSecurity()
    {
        super(Material.IRON);
        this.setHardness(5F);
        this.setUnlocalizedName("ME Server Security Terminal");
        this.setRegistryName("ServerSecurity");
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileServerSecurity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float par7, float par8, float par9) {
        super.onBlockActivated(world,x,y,z,p,side,par7,par8,par9);
        if(!world.isRemote) {
            if (!p.isSneaking()) {
                p.openGui(AppliedIntegrations.instance, 8, world, x, y, z);

                if(world.getTileEntity(x,y,z)!=null) {
                    TileServerSecurity tile = (TileServerSecurity) world.getTileEntity(x, y, z);

                    if(tile.hasMaster())
                        tile.getMaster().requestUpdate();
                }
                return true;
            }
        }
        return false;
    }

}

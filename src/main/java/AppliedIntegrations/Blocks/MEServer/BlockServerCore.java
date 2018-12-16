package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Entities.IAIMultiBlock;
import AppliedIntegrations.Entities.Server.TileServerCore;
import AppliedIntegrations.Entities.Server.TileServerRib;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;

public class BlockServerCore extends AIMultiBlock {

    private final Random rand = new Random();

    public BlockServerCore() {
        this.setBlockName("ME Server Core");
    }
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int par6)
    {
        if (world.isRemote) return;

        ArrayList<ItemStack> drops = new ArrayList<>();

        TileEntity teRaw = world.getTileEntity(x, y, z);

        if (teRaw != null && teRaw instanceof TileServerCore)
        {
            TileServerCore te = (TileServerCore) teRaw;

            for (int i = 0; i < te.inv.getSizeInventory(); i++)
            {
                ItemStack stack = te.inv.getStackInSlot(i);

                if (stack != null) drops.add(stack.copy());
            }
        }

        for (int i = 0;i < drops.size();i++)
        {
            EntityItem item = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, drops.get(i));
            item.setVelocity((rand.nextDouble() - 0.5) * 0.25, rand.nextDouble() * 0.5 * 0.25, (rand.nextDouble() - 0.5) * 0.25);
            world.spawnEntityInWorld(item);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p, int side, float par7, float par8, float par9) {
        super.onBlockActivated(world,x,y,z,p,side,par7,par8,par9);
            if (!p.isSneaking()) {
                if(!world.isRemote) {
                    world.markBlockForUpdate(x,y,z);
                    p.openGui(AppliedIntegrations.instance, 6, world, x, y, z);

                    return true;
            }
        }
        return false;
    }
}

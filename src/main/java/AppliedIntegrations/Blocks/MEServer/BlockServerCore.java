package AppliedIntegrations.Blocks.MEServer;

import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.tile.Server.TileServerCore;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

/**
 * @Author Azazell
 */
public class BlockServerCore extends AIMultiBlock {

    private final Random rand = new Random();

    public BlockServerCore(String reg, String unloc) {
        super(reg, unloc);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity tie, ItemStack stack)
    {
        if (world.isRemote) return;

        ArrayList<ItemStack> drops = new ArrayList<>();

        TileEntity teRaw = world.getTileEntity(pos);

        if (teRaw != null && teRaw instanceof TileServerCore)
        {
            TileServerCore te = (TileServerCore) teRaw;

            for (int i = 0; i < te.driveInv.getSizeInventory(); i++)
            {
                ItemStack istack = te.driveInv.getStackInSlot(i);

                if (istack != null) drops.add(istack.copy());
            }
        }

        for (int i = 0;i < drops.size();i++)
        {
            spawnAsEntity(world, pos, drops.get(i));
        }
    }
}

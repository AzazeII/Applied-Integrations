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
    public BlockServerCore(String reg, String unloc) {
        super(reg, unloc);
    }
}

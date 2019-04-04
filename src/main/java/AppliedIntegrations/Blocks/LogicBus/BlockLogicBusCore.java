package AppliedIntegrations.Blocks.LogicBus;

import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.LogicBus.TileLogicBusCore;
import AppliedIntegrations.tile.LogicBus.TileLogicBusSlave;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @Author Azazell
 */
public class BlockLogicBusCore extends AIMultiBlock {
    public BlockLogicBusCore(String reg, String unloc) {
        super(reg, unloc);
    }

    @Override
    public TileEntity createNewTileEntity(World w, int p_149915_2_) {
        return new TileLogicBusCore();
    }
}

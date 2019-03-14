package AppliedIntegrations.Blocks.LogicBus;

import AppliedIntegrations.Blocks.AIMultiBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLogicBusCore extends AIMultiBlock {
    public BlockLogicBusCore(String reg, String unloc) {
        super(reg, unloc);
    }
}

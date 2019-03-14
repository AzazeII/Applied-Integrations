package AppliedIntegrations.Blocks.LogicBus;

import AppliedIntegrations.Blocks.AIMultiBlock;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import static appeng.block.qnb.BlockQuantumBase.FORMED_STATE;

public class BlockLogicBusPort extends ModeledLogicBus {
    public BlockLogicBusPort(String reg, String unloc) {
        super(reg, unloc);
    }
}

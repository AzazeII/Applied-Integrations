package AppliedIntegrations.Blocks.LogicBus;


import AppliedIntegrations.Blocks.AIMultiBlock;
import AppliedIntegrations.tile.LogicBus.TileLogicBusCore;
import net.minecraft.tileentity.TileEntity;
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

package AppliedIntegrations.Utils;


import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.IAIPatternExtendable;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.Patterns.MultiControllerPattern;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static net.minecraft.util.EnumFacing.Axis.*;

/**
 * @Author Azazell
 */
public class MultiBlockUtils {

	/**
	 * This static method will iterate over given pattern and
	 * will add blocks at their place from pattern to given list
	 *
	 * @param pattern Pattern to validate
	 * @param tile    Check blocks relatively to given pivot. Pivot is tile
	 * @param extra   What to do, after validating block
	 * @return List filled with blocks that matched pattern
	 */
	public static List<? extends IAIMultiBlock> fillListWithPattern(List<BlockData> pattern, TileEntity tile, Consumer<BlockData> extra) {
		List<IAIMultiBlock> blockList = new ArrayList<>();
		int blocksToPlace = pattern.size();

		for (int i = 0; i < pattern.size(); i++) {
			if (!tile.getWorld().isRemote) {
				int x = tile.getPos().getX() + pattern.get(blocksToPlace - 1).x; // (1)
				int y = tile.getPos().getY() + pattern.get(blocksToPlace - 1).y; // (2)
				int z = tile.getPos().getZ() + pattern.get(blocksToPlace - 1).z; // (3)

				List<Block> variants = pattern.get(blocksToPlace - 1).options;

				if (variants.contains(tile.getWorld().getBlockState(new BlockPos(x, y, z)).getBlock())) {
					extra.accept(pattern.get(blocksToPlace - 1));
					IAIMultiBlock multiBlock = (IAIMultiBlock) tile.getWorld().getTileEntity(new BlockPos(x, y, z));
					blockList.add(multiBlock);
					blocksToPlace--;
				}
			}
		}

		return blockList;
	}

	/**
	 * Only for debug purposes. Fills world with given pattern relatively to pivot
	 * @param pattern to fill
	 * @param pivot center
	 */
	public static void fillWorldWithPattern(List<BlockData> pattern, TileEntity pivot) {
		for (BlockData data : pattern) {
			pivot.getWorld().setBlockState(data.getPos().add(pivot.getPos()), data.options.get(0).getDefaultState());
		}
	}

	/**
	 * Create new pattern from base extendable pattern
	 * @param pattern Base pattern
	 * @param axisLengthMap Map for resizing
	 * @return Extended pattern
	 */
	public static IAIPatternExtendable getExtendedPattern(IAIPatternExtendable pattern, Map<EnumFacing.Axis, Integer> axisLengthMap) {
		BlockPos minimal = pattern.getMinimalFrameSize();
		minimal = new BlockPos(minimal.getX() + axisLengthMap.get(X),
				minimal.getY() + axisLengthMap.get(Y),
				minimal.getZ() + axisLengthMap.get(Z));

		return MultiControllerPattern.generateMultiController(minimal);
	}
}

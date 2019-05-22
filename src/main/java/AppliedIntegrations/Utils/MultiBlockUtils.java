package AppliedIntegrations.Utils;


import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.IAIMinimalPattern;
import AppliedIntegrations.api.Multiblocks.IAIPatternExtendable;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author Azazell
 */
public class MultiBlockUtils {
	/**
	 * @param stream block data stream, from which new pattern will be created
	 * @return new pattern from given stream
	 */
	private static IAIMinimalPattern fromStream(Stream<BlockData> stream) {
		return () -> stream.collect(Collectors.toList());
	}

	/**
	 * This static method will iterate over given pattern and
	 * will add blocks at their place from pattern to given list
	 *
	 * @param pattern pattern to validate
	 * @param pivot   Check blocks relatively to given pivot. Pivot must be tile
	 * @param extra   What to do, after validating block
	 * @return List filled with blocks that matched pattern
	 */
	public static List<? extends IAIMultiBlock> fillListWithPattern(List<BlockData> pattern, IMaster pivot, Consumer<BlockData> extra) {

		List<IAIMultiBlock> blockList = new ArrayList<>();

		// Check if pivot is tile
		if (!(pivot instanceof TileEntity)) {
			throw new IllegalStateException("Multiblock pivot must be tile");
		}

		// Convert pivot to tile
		TileEntity tile = (TileEntity) pivot;

		// Count of blocks in pattern, not including pivot
		int blocksToPlace = pattern.size();

		// Iterate until i = pattern.len
		for (int i = 0; i < pattern.size(); i++) {
			// Call on server
			if (!tile.getWorld().isRemote) {
				// Add x, y and z of block in pattern to our location
				int x = tile.getPos().getX() + pattern.get(blocksToPlace - 1).x; // (1)
				int y = tile.getPos().getY() + pattern.get(blocksToPlace - 1).y; // (2)
				int z = tile.getPos().getZ() + pattern.get(blocksToPlace - 1).z; // (3)

				// Get list of block substitutions in pattern
				List<Block> variants = pattern.get(blocksToPlace - 1).options;

				// Get block in world, and check if it's equal to block in pattern
				if (variants.contains(tile.getWorld().getBlockState(new BlockPos(x, y, z)).getBlock())) {
					// Call extra
					extra.accept(pattern.get(blocksToPlace - 1));

					// Get tile
					IAIMultiBlock multiBlock = (IAIMultiBlock) tile.getWorld().getTileEntity(new BlockPos(x, y, z));

					// Add to list
					blockList.add(multiBlock);

					// Remove one block to place
					blocksToPlace--;
				}
			}
		}

		return blockList;
	}

	/**
	 * Extends {@code pattern} by one block for negative and positive facing of given {@code axis}
	 * @param pattern To extend
	 * @param axis For extending
	 * @param length Count of block to extend
	 */
	public static void extendPattern(IAIPatternExtendable pattern, EnumFacing.Axis axis, Integer length) {
		// Get minimal frame of pattern
		IAIMinimalPattern minimal = pattern.getMinimalFrame();

		// Iterate for each axis direction
		for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values()) {
			// Get facing from given axis
			EnumFacing facing = EnumFacing.getFacingFromAxis(direction, axis);

			// Update already existing pattern
			// Get edge of pattern from facing. Add offset to edge
			minimal = addDataToPattern(minimal, fromStream(minimal.getEdgeFromFacing(facing).map((data) -> {
				// Add offset to data
				return data.offset(facing, length);
			})).getPatternData());
		}


	}

	/**
	 * Add all second pattern data values to first pattern
	 * @param pattern to update
	 * @param adjustment with update
	 * @return New pattern
	 */
	private static IAIMinimalPattern addDataToPattern(IAIMinimalPattern pattern, List<BlockData> adjustment) {
		return () -> {
			// Get data list from pattern
			List<BlockData> dataList = pattern.getPatternData();

			// Add stream to this list
			dataList.addAll(adjustment);

			return dataList;
		};
	}
}

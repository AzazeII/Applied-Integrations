package AppliedIntegrations.Utils;

import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultiBlockUtils {
	/**
	 * This static method will iterate over given pattern and
	 * will add blocks at their place from pattern to given list
	 *
	 * @param pattern pattern to validate
	 * @param pivot   Check blocks relatively to given pivot. Pivot must be tile
	 * @param extra   What to do, after validating block
	 * @return List filled with blocks that matched pattern
	 */
	public static List<? extends IAIMultiBlock> fillListWithPattern(BlockData[] pattern, IMaster pivot, Consumer<BlockData> extra) {
		List<IAIMultiBlock> blockList = new ArrayList<>();

		// Check if pivot is tile
		if (!(pivot instanceof TileEntity)) {
			throw new IllegalStateException("Multiblock pivot must be tile");
		}

		// Convert pivot to tile
		TileEntity tile = (TileEntity) pivot;

		// Count of blocks in pattern, not including pivot
		int blocksToPlace = pattern.length;

		// Iterate until i = pattern.len
		for (int i = 0; i < pattern.length; i++) {
			// Call on server
			if (!tile.getWorld().isRemote) {
				// Add x, y and z of block in pattern to our loncatio
				int x = tile.getPos().getX() + pattern[blocksToPlace - 1].x; // (1)
				int y = tile.getPos().getY() + pattern[blocksToPlace - 1].y; // (2)
				int z = tile.getPos().getZ() + pattern[blocksToPlace - 1].z; // (3)

				// Get list of block substitutions in pattern
				List<Block> variants = pattern[blocksToPlace - 1].options;

				// Get block in world, and check if it's equal to block in pattern
				if (variants.contains(tile.getWorld().getBlockState(new BlockPos(x, y, z)).getBlock())) {
					// Call extra
					extra.accept(pattern[blocksToPlace - 1]);

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
}

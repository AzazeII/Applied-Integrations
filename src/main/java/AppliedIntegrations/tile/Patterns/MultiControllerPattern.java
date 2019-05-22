package AppliedIntegrations.tile.Patterns;


import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.BlockType;
import AppliedIntegrations.api.Multiblocks.IAIPatternExtendable;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author Azazell
 */
public class MultiControllerPattern implements IAIPatternExtendable {
	private static final BlockPos[] corners = {
			new BlockPos(1,1,1),
			new BlockPos(-1,1,1),
			new BlockPos(1,-1,1),
			new BlockPos(1,1,-1),

			new BlockPos(-1,-1,1),
			new BlockPos(-1,1,-1),
			new BlockPos(1,-1,-1),
			new BlockPos(-1,-1,-1)
	};

	/**
	 * Create line of given block and write it into list
	 * @param size First line vertex point
	 * @param inverted Second line vertex point
	 * @param b block used for line
	 * @return Line matrix-vector
	 */
	private static List<BlockData> getDataMatrixVector(BlockPos size, BlockPos inverted, Block b) {
		// Create initial data list
		List<BlockData> list = new LinkedList<>();

		// Iterate |size.x| + |inverted.x| times
		for (int x = inverted.getX(); x < size.getX(); x++) {
			// Add block data at fixed y and z, dynamic X
			list.add(new BlockData(x, size.getY(), size.getZ(), b));
		}

		// Iterate |size.y| + |inverted.y| times
		for (int y = inverted.getY(); y < size.getY(); y++) {
			// Add block data at fixed x and z, dynamic Y
			list.add(new BlockData(size.getX(), y, size.getZ(), b));
		}

		// Iterate |size.z| + |inverted.z| times
		for (int z = inverted.getZ(); z < size.getZ(); z++) {
			// Add block data at fixed x and y, dynamic Z
			list.add(new BlockData(size.getX(), size.getY(), z, b));
		}

		// Line (matrix-vector) created!
		return list;
	}

	public static IAIPatternExtendable generateMultiControllerForSize(BlockPos size) {
		// Create initial data list
		List<BlockData> list = new ArrayList<>();

		// Iterate for each corner
		for (BlockPos corner : corners) {
			// Get valued size, which is size multiplied by corner
			BlockPos valuedSize = new BlockPos(
					size.getX() * corner.getX(),
					size.getY() * corner.getY(),
					size.getZ() * corner.getZ());


			// Iterate for each axis
			for (EnumFacing.Axis axisB : EnumFacing.Axis.values()) {
				// Get negative side
				EnumFacing negative = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, axisB);

				// Invert one ordinal of size and create new halfInverted size
				BlockPos halfInverted = new BlockPos(negative.getFrontOffsetX() == 0 ? valuedSize.getX() : valuedSize.getX() * negative.getFrontOffsetX(),
						negative.getFrontOffsetY() == 0 ? valuedSize.getY() : valuedSize.getY() * negative.getFrontOffsetY(),
						negative.getFrontOffsetZ() == 0 ? valuedSize.getZ() : valuedSize.getZ() * negative.getFrontOffsetZ());

				// Draw rib line from invertedA to halfInverted vertices
				list.addAll(getDataMatrixVector(valuedSize, halfInverted, BlocksEnum.BMCRib.b));
			}
		}

		// Add size vector itself
		list.add(new BlockData(size, BlocksEnum.BMCRib.b));

		// Create new anonymous instance of pattern extendable
		return new IAIPatternExtendable() {
			@Override
			public BlockPos getMinimalFrameSize() {
				// Updated size value
				return size;
			}

			@Override
			public List<BlockData> getPatternData() {
				return list;
			}
		};
	}

	@Override
	public List<BlockData> getPatternData() {
		// Create initial list from main data block
		return Arrays.asList(
				// Main axises
				new BlockData(0, 2, 0, BlocksEnum.BMCPort.b),
				new BlockData(2, 0, 0, BlocksEnum.BMCPort.b),
				new BlockData(0, 0, 2, BlocksEnum.BMCPort.b),
				new BlockData(0, 0, -2, BlocksEnum.BMCPort.b),
				new BlockData(-2, 0, 0, BlocksEnum.BMCPort.b),
				new BlockData(0, -2, 0, BlocksEnum.BMCPort.b),
				new BlockData(0, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(1, 0, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, 0, 1, BlocksEnum.BMCHousing.b),
				new BlockData(0, 0, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 0, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, -1, 0, BlocksEnum.BMCHousing.b),

				// Corners near core
				new BlockData(-1, -1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 1, 1, BlocksEnum.BMCHousing.b),

				new BlockData(-1, 1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, -1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, -1, 1, BlocksEnum.BMCHousing.b),

				// Lines
				new BlockData(1, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(1, -1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, 1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(0, -1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(0, 1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(0, -1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 0, 1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 0, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 0, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 0, -1, BlocksEnum.BMCHousing.b),
				// Corners
				new BlockData(-2, -2, -2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(-2, -2, 2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(-2, 2, 2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(2, 2, 2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(-2, 2, -2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(2, 2, -2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(2, -2, -2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(2, -2, 2, BlocksEnum.BMCRib.b, BlockType.Corner),

				// Ribs
				new BlockData(2, 2, 0, BlocksEnum.BMCRib.b),
				new BlockData(2, -2, 0, BlocksEnum.BMCRib.b),
				new BlockData(-2, 2, 0, BlocksEnum.BMCRib.b),
				new BlockData(-2, -2, 0, BlocksEnum.BMCRib.b),
				
				new BlockData(0, 2, 2, BlocksEnum.BMCRib.b),
				new BlockData(0, -2, 2, BlocksEnum.BMCRib.b),
				new BlockData(0, 2, -2, BlocksEnum.BMCRib.b),
				new BlockData(0, -2, -2, BlocksEnum.BMCRib.b),
				
				new BlockData(2, 0, 2, BlocksEnum.BMCRib.b),
				new BlockData(2, 0, -2, BlocksEnum.BMCRib.b),
				new BlockData(-2, 0, 2, BlocksEnum.BMCRib.b),
				new BlockData(-2, 0, -2, BlocksEnum.BMCRib.b),

				// Ribs Adjustments
				new BlockData(2, 2, 1, BlocksEnum.BMCRib.b),
				new BlockData(2, -2, 1, BlocksEnum.BMCRib.b),
				new BlockData(-2, 2, 1, BlocksEnum.BMCRib.b),
				new BlockData(-2, -2, 1, BlocksEnum.BMCRib.b),
				new BlockData(1, 2, 2, BlocksEnum.BMCRib.b),
				new BlockData(1, -2, 2, BlocksEnum.BMCRib.b),
				new BlockData(1, 2, -2, BlocksEnum.BMCRib.b),
				new BlockData(1, -2, -2, BlocksEnum.BMCRib.b),
				new BlockData(2, 1, 2, BlocksEnum.BMCRib.b),
				new BlockData(2, 1, -2, BlocksEnum.BMCRib.b),
				new BlockData(-2, 1, 2, BlocksEnum.BMCRib.b),
				new BlockData(-2, 1, -2, BlocksEnum.BMCRib.b),

				new BlockData(2, 2, -1, BlocksEnum.BMCRib.b),
				new BlockData(2, -2, -1, BlocksEnum.BMCRib.b),
				new BlockData(-2, 2, -1, BlocksEnum.BMCRib.b),
				new BlockData(-2, -2, -1, BlocksEnum.BMCRib.b),
				new BlockData(-1, 2, 2, BlocksEnum.BMCRib.b),
				new BlockData(-1, -2, 2, BlocksEnum.BMCRib.b),
				new BlockData(-1, 2, -2, BlocksEnum.BMCRib.b),
				new BlockData(-1, -2, -2, BlocksEnum.BMCRib.b),
				new BlockData(2, -1, 2, BlocksEnum.BMCRib.b),
				new BlockData(2, -1, -2, BlocksEnum.BMCRib.b),
				new BlockData(-2, -1, 2, BlocksEnum.BMCRib.b),
				new BlockData(-2, -1, -2, BlocksEnum.BMCRib.b),

				// South edge
				new BlockData(1, 0, 2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 0, 2, BlocksEnum.BMCHousing.b),
				new BlockData(0, 1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(0, -1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(1, 1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(1, -1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -1, 2, BlocksEnum.BMCHousing.b),

				// North edge
				new BlockData(1, 0, -2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 0, -2, BlocksEnum.BMCHousing.b),
				new BlockData(0, 1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(0, -1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(1, 1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(1, -1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -1, -2, BlocksEnum.BMCHousing.b),
				
				// East edge
				new BlockData(2, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(2, -1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(2, 0, 1, BlocksEnum.BMCHousing.b),
				new BlockData(2, 0, -1, BlocksEnum.BMCHousing.b),
				new BlockData(2, 1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(2, 1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(2, -1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(2, -1, -1, BlocksEnum.BMCHousing.b),

				// West edge
				new BlockData(-2, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-2, -1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 0, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 0, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, -1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, -1, -1, BlocksEnum.BMCHousing.b),
				
				// Up edge
				new BlockData(1, 2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, 2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(0, 2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 2, -1, BlocksEnum.BMCHousing.b),
				
				// Down edge
				new BlockData(1, -2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, -2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(0, -2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, -2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(1, -2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -2, -1, BlocksEnum.BMCHousing.b)
		);
	}

	@Override
	public BlockPos getMinimalFrameSize() {
		return new BlockPos(1, 1, 1);
	}
}

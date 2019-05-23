package AppliedIntegrations.tile.Patterns;


import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.BlockType;
import AppliedIntegrations.api.Multiblocks.IAIPatternExtendable;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.minecraft.util.EnumFacing.Axis.*;

/**
 * @Author Azazell
 */
public class MultiControllerPattern implements IAIPatternExtendable {
	// Array of all corners represented by block positions
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

	@Nonnull
	private static BlockPos resizeVector(@Nonnull BlockPos size, @Nonnull EnumFacing facing) {
		// Check if axis is X
		if (facing.getAxis() == X) {
			return new BlockPos(size.getX(), size.getY() - 1, size.getZ() - 1);

		// Check if axis is Y
		}else if (facing.getAxis() == Y){
			return new BlockPos(size.getX() - 1, size.getY(), size.getZ() - 1);

		// Check if axis is Z
		}else if (facing.getAxis() == Z) {
			return new BlockPos(size.getX() - 1, size.getY() - 1, size.getZ());
		}

		return size;
	}

	private static void forPosOnAxis(BlockPos sizeVec, BlockPos inverted, Consumer<BlockPos> func, EnumFacing.Axis axis) {
		// Check if axis is X
		if (axis == X) {
			// Iterate from -Y to Y
			for (int y = inverted.getY(); y < sizeVec.getY() + 1; ++y) {
				// Iterate from -Z to Z
				for (int z = inverted.getZ(); z < sizeVec.getZ() + 1; ++z) {
					// Call lambda
					func.accept(new BlockPos(sizeVec.getX(), y, z));
				}
			}
		}

		// Check if axis is Y
		if (axis == Y) {
			// Iterate from -X to X
			for (int x = inverted.getX(); x < sizeVec.getX() + 1; ++x) {
				// Iterate from -Z to Z
				for (int z = inverted.getZ(); z < sizeVec.getZ() + 1; ++z) {
					// Call lambda
					func.accept(new BlockPos(x, sizeVec.getY(), z));
				}
			}
		}

		// Check if axis is Z
		if (axis == Z) {
			// Iterate from -X to X
			for (int x = inverted.getX(); x < sizeVec.getX() + 1; ++x) {
				// Iterate from -Y to Y
				for (int y = inverted.getY(); y < sizeVec.getY() + 1; ++y) {
					// Call lambda
					func.accept(new BlockPos(x, y, sizeVec.getZ()));
				}
			}
		}
	}

	/**
	 * Fill list with block data edge of options and given vector
	 * @param sizeVec Size of edge
	 * @param options Block options for data
	 * @param axis Axis of edge facing
	 * @return edge-data-list
	 */
	@Nonnull
	private static List<BlockData> fillEdge(BlockPos sizeVec, Block[] options, EnumFacing.Axis axis) {
		// Create initial list
		List<BlockData> list = new ArrayList<>();

		// Invert positions components that not on given axis
		BlockPos inverted = new BlockPos(
				axis == X ? sizeVec.getX() : -sizeVec.getX(),
				axis == Y ? sizeVec.getY() : -sizeVec.getY(),
				axis == Z ? sizeVec.getZ() : -sizeVec.getZ());

		// Iterate for each position on 2d plane on given axis
		forPosOnAxis(sizeVec, inverted, (BlockPos pos) -> list.add(new BlockData(pos, options)), axis);
		
		return list;
	}

	/**
	 * Create line of given block and write it into list
	 * @param size First line vertex point
	 * @param inverted Second line vertex point
	 * @param options blocks used for line
	 * @return Line matrix-vector
	 */
	private static List<BlockData> getDataMatrixVector(BlockPos size, BlockPos inverted, Block[] options) {
		// Create initial data list
		List<BlockData> list = new LinkedList<>();

		// Iterate |size.x| + |inverted.x| times
		for (int x = inverted.getX(); x < size.getX(); x++) {
			// Add block data at fixed y and z, dynamic X
			list.add(new BlockData(x, size.getY(), size.getZ(), options));
		}

		// Iterate |size.y| + |inverted.y| times
		for (int y = inverted.getY(); y < size.getY(); y++) {
			// Add block data at fixed x and z, dynamic Y
			list.add(new BlockData(size.getX(), y, size.getZ(), options));
		}

		// Iterate |size.z| + |inverted.z| times
		for (int z = inverted.getZ(); z < size.getZ(); z++) {
			// Add block data at fixed x and y, dynamic Z
			list.add(new BlockData(size.getX(), size.getY(), z, options));
		}

		// Add size vector itself
		list.add(new BlockData(size, options));

		// Line (matrix-vector) created!
		return list;
	}

	public static IAIPatternExtendable generateMultiController(BlockPos size) {
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
			for (EnumFacing.Axis axis : EnumFacing.Axis.values()) {
				// Get negative side
				EnumFacing negative = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, axis);

				// Invert one ordinal of size and create new halfInverted size
				BlockPos halfInverted = new BlockPos(
						negative.getFrontOffsetX() == 0 ? valuedSize.getX() : valuedSize.getX() * negative.getFrontOffsetX(),
						negative.getFrontOffsetY() == 0 ? valuedSize.getY() : valuedSize.getY() * negative.getFrontOffsetY(),
						negative.getFrontOffsetZ() == 0 ? valuedSize.getZ() : valuedSize.getZ() * negative.getFrontOffsetZ());

				// Draw rib line from invertedA to halfInverted vertices
				list.addAll(getDataMatrixVector(valuedSize, halfInverted, new Block[]{BlocksEnum.BMCRib.b}));
			}
		}

		// Iterate for each facing
		for (EnumFacing facing : EnumFacing.VALUES){
			// Remove one block for all axises not equal to axis of current facing
			BlockPos valuedSize = resizeVector(size, facing);

			// Check if facing is negative
			if (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
				// Flip axis of block pos
				valuedSize = new BlockPos(
						facing.getFrontOffsetX() == 0 ? valuedSize.getX() : valuedSize.getX() * facing.getFrontOffsetX(),
						facing.getFrontOffsetY() == 0 ? valuedSize.getY() : valuedSize.getY() * facing.getFrontOffsetY(),
						facing.getFrontOffsetZ() == 0 ? valuedSize.getZ() : valuedSize.getZ() * facing.getFrontOffsetZ());
			}

			// Fill edge with array of housing and port
			list.addAll(fillEdge(valuedSize, new Block[]{BlocksEnum.BMCHousing.b, BlocksEnum.BMCPort.b}, facing.getAxis()));
		}

		// Remove duplicated list entries
		list = list.stream().distinct().collect(Collectors.toList());

		// Create final list
		List<BlockData> finalList = list;

		// Create new anonymous instance of pattern extendable
		return new IAIPatternExtendable() {
			@Override
			public BlockPos getMinimalFrameSize() {
				// Updated size value
				return size;
			}

			@Override
			public List<BlockData> getPatternData() {
				return finalList;
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

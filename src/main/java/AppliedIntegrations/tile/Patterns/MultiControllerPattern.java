package AppliedIntegrations.tile.Patterns;


import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.IAIPatternExtendable;
import appeng.api.util.AEPartLocation;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.*;
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
		if (facing.getAxis() == X) {
			return new BlockPos(size.getX(), size.getY() - 1, size.getZ() - 1);

		} else if (facing.getAxis() == Y){
			return new BlockPos(size.getX() - 1, size.getY(), size.getZ() - 1);

		} else if (facing.getAxis() == Z) {
			return new BlockPos(size.getX() - 1, size.getY() - 1, size.getZ());
		}

		return size;
	}

	private static void forPosOnAxis(BlockPos sizeVec, BlockPos inverted, Consumer<BlockPos> func, EnumFacing.Axis axis) {
		if (axis == X) {
			for (int y = inverted.getY(); y < sizeVec.getY() + 1; ++y) {
				for (int z = inverted.getZ(); z < sizeVec.getZ() + 1; ++z) {
					func.accept(new BlockPos(sizeVec.getX(), y, z));
				}
			}
		}

		if (axis == Y) {
			for (int x = inverted.getX(); x < sizeVec.getX() + 1; ++x) {
				for (int z = inverted.getZ(); z < sizeVec.getZ() + 1; ++z) {
					func.accept(new BlockPos(x, sizeVec.getY(), z));
				}
			}
		}

		if (axis == Z) {
			for (int x = inverted.getX(); x < sizeVec.getX() + 1; ++x) {
				for (int y = inverted.getY(); y < sizeVec.getY() + 1; ++y) {
					func.accept(new BlockPos(x, y, sizeVec.getZ()));
				}
			}
		}
	}

	/**
	 * Replace all empty in pattern with data with given block options
	 * @param size Size vector
	 * @param options Block options
	 * @return Inversed empty space array
	 */
	@Nonnull
	private static List<BlockData> inverseEmptySpace(BlockPos size, Block[] options) {
		List<BlockData> list = new ArrayList<>();
		BlockPos inverted = size.subtract(size).subtract(size);

		for (int z = inverted.getZ(); z < size.getZ() + 1; ++z){
			for (int y = inverted.getY(); y < size.getY() + 1; ++y){
				for (int x = inverted.getX(); x < size.getX() + 1; ++x){
					if (x == 0 && y == 0 && z == 0)
						continue;

					list.add(new BlockData(x, y, z, options));
				}
			}
		}
		
		return list;
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
		List<BlockData> list = new ArrayList<>();
		BlockPos inverted = new BlockPos(
				axis == X ? sizeVec.getX() : -sizeVec.getX(),
				axis == Y ? sizeVec.getY() : -sizeVec.getY(),
				axis == Z ? sizeVec.getZ() : -sizeVec.getZ());

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
	@Nonnull
	private static List<BlockData> getDataMatrixVector(BlockPos size, BlockPos inverted, Block[] options) {
		List<BlockData> list = new LinkedList<>();

		for (int x = inverted.getX(); x < size.getX(); x++) {
			list.add(new BlockData(x, size.getY(), size.getZ(), options));
		}

		for (int y = inverted.getY(); y < size.getY(); y++) {
			list.add(new BlockData(size.getX(), y, size.getZ(), options));
		}

		for (int z = inverted.getZ(); z < size.getZ(); z++) {
			list.add(new BlockData(size.getX(), size.getY(), z, options));
		}

		list.add(new BlockData(size, options));
		return list;
	}

	/**
	 * @param size Vector of distance from (0,0,0) to POSITIVE corner of pattern
	 * @return Generated pattern, (0,0,0) is always empty
	 */
	public static IAIPatternExtendable generateMultiController(BlockPos size) {
		// Fill ribs of multi-block: ::getDataMatrixVector
		List<BlockData> list = new ArrayList<>();
		Map<AEPartLocation, List<BlockPos>> edgeMap = new HashMap<AEPartLocation, List<BlockPos>>() {{
			for (AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
				put(side, new ArrayList<>());
			}
		}};

		for (BlockPos corner : corners) {
			BlockPos valuedSize = new BlockPos(
					size.getX() * corner.getX(),
					size.getY() * corner.getY(),
					size.getZ() * corner.getZ());


			for (EnumFacing.Axis axis : EnumFacing.Axis.values()) {
				EnumFacing negative = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, axis);
				BlockPos halfInverted = new BlockPos(
						negative.getFrontOffsetX() == 0 ? valuedSize.getX() : valuedSize.getX() * negative.getFrontOffsetX(),
						negative.getFrontOffsetY() == 0 ? valuedSize.getY() : valuedSize.getY() * negative.getFrontOffsetY(),
						negative.getFrontOffsetZ() == 0 ? valuedSize.getZ() : valuedSize.getZ() * negative.getFrontOffsetZ());

				// Draw rib line from invertedA to halfInverted vertices
				list.addAll(getDataMatrixVector(valuedSize, halfInverted, new Block[]{BlocksEnum.BMCRib.b}));
			}
		}

		// Fill edges if multi-block: ::fillEdge
		for (EnumFacing facing : EnumFacing.VALUES){
			BlockPos valuedSize = resizeVector(size, facing);
			if (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
				valuedSize = new BlockPos(
						facing.getFrontOffsetX() == 0 ? valuedSize.getX() : valuedSize.getX() * facing.getFrontOffsetX(),
						facing.getFrontOffsetY() == 0 ? valuedSize.getY() : valuedSize.getY() * facing.getFrontOffsetY(),
						facing.getFrontOffsetZ() == 0 ? valuedSize.getZ() : valuedSize.getZ() * facing.getFrontOffsetZ());
			}

			List<BlockData> edge = fillEdge(valuedSize, new Block[]{BlocksEnum.BMCHousing.b, BlocksEnum.BMCPort.b}, facing.getAxis());
			list.addAll(edge);
			for (BlockData data : edge) {
				edgeMap.get(AEPartLocation.fromFacing(facing)).add(data.getPos());
			}
		}

		// Fill inner space of multi-block: ::inverseEmptySpace
		// Remove Central block from pattern
		list.addAll(inverseEmptySpace(size.add(-1, -1, -1), new Block[]{BlocksEnum.BMCHousing.b}));
		list = list.stream().distinct().collect(Collectors.toList());
		List<BlockData> finalList = list;

		return new IAIPatternExtendable() {
			@Override
			public BlockPos getMinimalFrameSize() {
				return size;
			}

			@Override
			public Map<AEPartLocation, List<BlockPos>> getPosEdgeMap() {
				return edgeMap;
			}

			@Override
			public List<BlockData> getPatternData() {
				return finalList;
			}
		};
	}

	// Don't use this for this pattern, generate new with MultiBlockUtils instead.
	@Deprecated
	@Override
	public List<BlockData> getPatternData() {
		return new ArrayList<>();
	}

	@Override
	public BlockPos getMinimalFrameSize() {
		return new BlockPos(1, 1, 1);
	}
}

/*
Old multi-block manually generated pattern
Arrays.asList(
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
 */

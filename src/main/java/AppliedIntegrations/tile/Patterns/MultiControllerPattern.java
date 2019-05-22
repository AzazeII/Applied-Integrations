package AppliedIntegrations.tile.Patterns;


import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.BlockType;
import AppliedIntegrations.api.Multiblocks.IAIMinimalPattern;
import AppliedIntegrations.api.Multiblocks.IAIPatternExtendable;
import net.minecraft.util.EnumFacing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @Author Azazell
 */
public class MultiControllerPattern implements IAIPatternExtendable {
	private Map<EnumFacing, List<BlockData>> edgeMap = new HashMap<>();

	private IAIMinimalPattern keyFrame = () -> Arrays.asList(
		// Corners
		new BlockData(-1, -1, -1, BlocksEnum.BMCRib.b),
		new BlockData(-1, -1, 1, BlocksEnum.BMCRib.b),
		new BlockData(-1, 1, 1, BlocksEnum.BMCRib.b),
		new BlockData(1, 1, 1, BlocksEnum.BMCRib.b),

		new BlockData(-1, 1, -1, BlocksEnum.BMCRib.b),
		new BlockData(1, 1, -1, BlocksEnum.BMCRib.b),
		new BlockData(1, -1, -1, BlocksEnum.BMCRib.b),
		new BlockData(1, -1, 1, BlocksEnum.BMCRib.b),

		// Ribs
		new BlockData(1,-1,0, BlocksEnum.BMCRib.b),
		new BlockData(-1,1,0, BlocksEnum.BMCRib.b),
		new BlockData(1,-1,0, BlocksEnum.BMCRib.b),
		new BlockData(-1,1,0, BlocksEnum.BMCRib.b),

		new BlockData(0,-1,1, BlocksEnum.BMCRib.b),
		new BlockData(0,1,1, BlocksEnum.BMCRib.b),
		new BlockData(0,-1,-1, BlocksEnum.BMCRib.b),
		new BlockData(0,1,-1, BlocksEnum.BMCRib.b),

		new BlockData(1,0,1, BlocksEnum.BMCRib.b),
		new BlockData(1,0,-1, BlocksEnum.BMCRib.b),
		new BlockData(-1,0,1, BlocksEnum.BMCRib.b),
		new BlockData(-1,0,-1, BlocksEnum.BMCRib.b),

		// Ports
		new BlockData(1,0,0, BlocksEnum.BMCPort.b),
		new BlockData(-1,0,0, BlocksEnum.BMCPort.b),
		new BlockData(0,1,0, BlocksEnum.BMCPort.b),
		new BlockData(0,-1,0, BlocksEnum.BMCPort.b),
		new BlockData(0,0,1, BlocksEnum.BMCPort.b),
		new BlockData(0,0,-1, BlocksEnum.BMCPort.b)
	);

	private List<BlockData> ribs = Arrays.asList(
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
			new BlockData(-2, -1, -2, BlocksEnum.BMCRib.b)
	);

	public MultiControllerPattern() {
		// South
		edgeMap.put(EnumFacing.SOUTH, Arrays.asList(
				new BlockData(1, 0, 2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 0, 2, BlocksEnum.BMCHousing.b),
				new BlockData(0, 1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(0, -1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(1, 1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(1, -1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -1, 2, BlocksEnum.BMCHousing.b)
		));

		// North
		edgeMap.put(EnumFacing.NORTH, Arrays.asList(
				new BlockData(1, 0, -2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 0, -2, BlocksEnum.BMCHousing.b),
				new BlockData(0, 1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(0, -1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(1, 1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(1, -1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -1, -2, BlocksEnum.BMCHousing.b)
		));

		// East
		edgeMap.put(EnumFacing.EAST, Arrays.asList(
				new BlockData(2, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(2, -1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(2, 0, 1, BlocksEnum.BMCHousing.b),
				new BlockData(2, 0, -1, BlocksEnum.BMCHousing.b),
				new BlockData(2, 1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(2, 1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(2, -1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(2, -1, -1, BlocksEnum.BMCHousing.b)
		));

		// West
		edgeMap.put(EnumFacing.WEST, Arrays.asList(
				new BlockData(-2, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-2, -1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 0, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 0, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, -1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, -1, -1, BlocksEnum.BMCHousing.b))
		);

		// Up
		edgeMap.put(EnumFacing.UP, Arrays.asList(
				new BlockData(1, 2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, 2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(0, 2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 2, -1, BlocksEnum.BMCHousing.b))
		);

		// Down
		edgeMap.put(EnumFacing.DOWN, Arrays.asList(
				new BlockData(1, -2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, -2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(0, -2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, -2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(1, -2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -2, -1, BlocksEnum.BMCHousing.b))
		);

	}

	@Override
	public List<BlockData> getPatternData() {
		// Create initial list
		List<BlockData> dataList = Arrays.asList(
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
				new BlockData(-1, 0, -1, BlocksEnum.BMCHousing.b)
		);

		// Add edges to list
		// Iterate for each edge in map
		for (List<BlockData> data : edgeMap.values()) {
			// Add data to data list
			dataList.addAll(data);
		}

		// Add ribs to list
		dataList.addAll(ribs);

		return dataList;
	}

	@Override
	public Stream<BlockData> getEdgeFromFacing(EnumFacing facing) {
		return edgeMap.get(facing).stream();
	}

	@Override
	public IAIMinimalPattern getMinimalFrame() {
		return keyFrame;
	}
}

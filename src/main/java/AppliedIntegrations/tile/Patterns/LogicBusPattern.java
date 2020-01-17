package AppliedIntegrations.tile.Patterns;


import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.IAIPattern;

import java.util.Arrays;
import java.util.List;

import static AppliedIntegrations.Blocks.BlocksEnum.BLBRibs;

/**
 * @Author Azazell
 */
public class LogicBusPattern implements IAIPattern {
	@Override
	public List<BlockData> getPatternData() {
		return Arrays.asList(
				// Corners
				new BlockData(1, 1, 1, BLBRibs.b),
				new BlockData(-1, 1, 1, BLBRibs.b),
				new BlockData(1, -1, 1, BLBRibs.b),
				new BlockData(-1, -1, 1, BLBRibs.b),
				new BlockData(-1, 1, -1, BLBRibs.b),
				new BlockData(1, 1, -1, BLBRibs.b),
				new BlockData(1, -1, -1, BLBRibs.b),
				new BlockData(-1, -1, -1, BLBRibs.b),

				// up crest
				new BlockData(0, 1, 0, BLBRibs.b),

				// corners
				new BlockData(0, 1, 1, BLBRibs.b),
				new BlockData(0, 1, -1, BLBRibs.b),
				new BlockData(1, 1, 0, BLBRibs.b),
				new BlockData(-1, 1, 0, BLBRibs.b),

				// down crest
				new BlockData(0, -1, 0, BLBRibs.b),

				// corners
				new BlockData(0, -1, 1, BLBRibs.b),
				new BlockData(0, -1, -1, BLBRibs.b),
				new BlockData(1, -1, 0, BLBRibs.b),
				new BlockData(-1, -1, 0, BLBRibs.b)
		);
	}
}

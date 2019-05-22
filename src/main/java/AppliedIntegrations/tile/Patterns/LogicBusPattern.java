package AppliedIntegrations.tile.Patterns;


import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.IAIPattern;

import static AppliedIntegrations.Blocks.BlocksEnum.BLBRibs;

public class LogicBusPattern implements IAIPattern {
	@Override
	public BlockData[] getPatternData() {
		return new BlockData[]{
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
				// up
				new BlockData(0, 1, 0, BLBRibs.b),

				// corners
				new BlockData(0, 1, 1, BLBRibs.b),
				new BlockData(0, 1, -1, BLBRibs.b),
				new BlockData(1, 1, 0, BLBRibs.b),
				new BlockData(-1, 1, 0, BLBRibs.b),

				// down crest
				// down
				new BlockData(0, -1, 0, BLBRibs.b),

				// corners
				new BlockData(0, -1, 1, BLBRibs.b),
				new BlockData(0, -1, -1, BLBRibs.b),
				new BlockData(1, -1, 0, BLBRibs.b),
				new BlockData(-1, -1, 0, BLBRibs.b),};
	}
}

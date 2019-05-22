package AppliedIntegrations.tile.Patterns;


import AppliedIntegrations.api.Multiblocks.BlockData;

import static AppliedIntegrations.Blocks.BlocksEnum.BLBRibs;

/**
 * @Author Azazell
 */
public class AIPatterns {
	public static final BlockData[] ME_LOGIC = {
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

	public static final MultiControllerPattern ME_MULTI_CONTROLLER = new MultiControllerPattern();
}

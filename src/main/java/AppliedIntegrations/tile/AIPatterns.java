package AppliedIntegrations.tile;


import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.BlockType;

import static AppliedIntegrations.Blocks.BlocksEnum.*;

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

	public static final BlockData[] ME_SERVER = {
			// Main axises
			new BlockData(0, 2, 0, BSPort.b, BSHousing.b),
			new BlockData(0, 1, 0, BSHousing.b),
			new BlockData(2, 0, 0, BSPort.b, BSHousing.b),
			new BlockData(1, 0, 0, BSHousing.b),
			new BlockData(0, 0, 2, BSPort.b, BSHousing.b),
			new BlockData(0, 0, 1, BSHousing.b),
			//0,0,0;
			new BlockData(0, 0, -1, BSHousing.b),
			new BlockData(0, 0, -2, BSPort.b, BSHousing.b),
			new BlockData(-1, 0, 0, BSHousing.b),
			new BlockData(-2, 0, 0, BSPort.b, BSHousing.b),
			new BlockData(0, -1, 0, BSHousing.b),
			new BlockData(0, -2, 0, BSPort.b, BSHousing.b),

			// Corners
			new BlockData(-1, -1, -1, BSHousing.b),
			new BlockData(-1, -1, 1, BSHousing.b),
			new BlockData(-1, 1, 1, BSHousing.b),
			new BlockData(1, 1, 1, BSHousing.b),

			new BlockData(-1, 1, -1, BSHousing.b),
			new BlockData(1, 1, -1, BSHousing.b),
			new BlockData(1, -1, -1, BSHousing.b),
			new BlockData(1, -1, 1, BSHousing.b),
			// Lines
			new BlockData(1, 1, 0, BSHousing.b),
			new BlockData(1, -1, 0, BSHousing.b),
			new BlockData(-1, 1, 0, BSHousing.b),
			new BlockData(-1, -1, 0, BSHousing.b),
			new BlockData(0, 1, 1, BSHousing.b),
			new BlockData(0, -1, 1, BSHousing.b),
			new BlockData(0, 1, -1, BSHousing.b),
			new BlockData(0, -1, -1, BSHousing.b),
			new BlockData(1, 0, 1, BSHousing.b),
			new BlockData(1, 0, -1, BSHousing.b),
			new BlockData(-1, 0, 1, BSHousing.b),
			new BlockData(-1, 0, -1, BSHousing.b),
			// Final Layer:
			// 0,0,0
			//Corners
			new BlockData(-2, -2, -2, BSRib.b, BlockType.Corner),
			new BlockData(-2, -2, 2, BSRib.b, BlockType.Corner),
			new BlockData(-2, 2, 2, BSRib.b, BlockType.Corner),
			new BlockData(2, 2, 2, BSRib.b, BlockType.Corner),

			new BlockData(-2, 2, -2, BSRib.b, BlockType.Corner),
			new BlockData(2, 2, -2, BSRib.b, BlockType.Corner),
			new BlockData(2, -2, -2, BSRib.b, BlockType.Corner),
			new BlockData(2, -2, 2, BSRib.b, BlockType.Corner),
			//Ribs
			new BlockData(2, 2, 0, BSRib.b),
			new BlockData(2, -2, 0, BSRib.b),
			new BlockData(-2, 2, 0, BSRib.b),
			new BlockData(-2, -2, 0, BSRib.b),
			new BlockData(0, 2, 2, BSRib.b),
			new BlockData(0, -2, 2, BSRib.b),
			new BlockData(0, 2, -2, BSRib.b),
			new BlockData(0, -2, -2, BSRib.b),
			new BlockData(2, 0, 2, BSRib.b),
			new BlockData(2, 0, -2, BSRib.b),
			new BlockData(-2, 0, 2, BSRib.b),
			new BlockData(-2, 0, -2, BSRib.b),
			//RibsAdjustments
			new BlockData(2, 2, 1, BSRib.b),
			new BlockData(2, -2, 1, BSRib.b),
			new BlockData(-2, 2, 1, BSRib.b),
			new BlockData(-2, -2, 1, BSRib.b),
			new BlockData(1, 2, 2, BSRib.b),
			new BlockData(1, -2, 2, BSRib.b),
			new BlockData(1, 2, -2, BSRib.b),
			new BlockData(1, -2, -2, BSRib.b),
			new BlockData(2, 1, 2, BSRib.b),
			new BlockData(2, 1, -2, BSRib.b),
			new BlockData(-2, 1, 2, BSRib.b),
			new BlockData(-2, 1, -2, BSRib.b),

			new BlockData(2, 2, -1, BSRib.b),
			new BlockData(2, -2, -1, BSRib.b),
			new BlockData(-2, 2, -1, BSRib.b),
			new BlockData(-2, -2, -1, BSRib.b),
			new BlockData(-1, 2, 2, BSRib.b),
			new BlockData(-1, -2, 2, BSRib.b),
			new BlockData(-1, 2, -2, BSRib.b),
			new BlockData(-1, -2, -2, BSRib.b),
			new BlockData(2, -1, 2, BSRib.b),
			new BlockData(2, -1, -2, BSRib.b),
			new BlockData(-2, -1, 2, BSRib.b),
			new BlockData(-2, -1, -2, BSRib.b),

			// Edges
			// South
			new BlockData(1, 0, 2, BSHousing.b, BSPort.b),
			new BlockData(-1, 0, 2, BSHousing.b, BSPort.b),
			new BlockData(0, 1, 2, BSHousing.b, BSPort.b),
			new BlockData(0, -1, 2, BSHousing.b, BSPort.b),
			new BlockData(1, 1, 2, BSPort.b),
			new BlockData(1, -1, 2, BSPort.b),
			new BlockData(-1, 1, 2, BSPort.b),
			new BlockData(-1, -1, 2, BSPort.b),

			// North
			new BlockData(1, 0, -2, BSHousing.b, BSPort.b),
			new BlockData(-1, 0, -2, BSHousing.b, BSPort.b),
			new BlockData(0, 1, -2, BSHousing.b, BSPort.b),
			new BlockData(0, -1, -2, BSHousing.b, BSPort.b),
			new BlockData(1, 1, -2, BSPort.b),
			new BlockData(1, -1, -2, BSPort.b),
			new BlockData(-1, 1, -2, BSPort.b),
			new BlockData(-1, -1, -2, BSPort.b),

			// East
			new BlockData(2, 1, 0, BSHousing.b, BSPort.b),
			new BlockData(2, -1, 0, BSHousing.b, BSPort.b),
			new BlockData(2, 0, 1, BSHousing.b, BSPort.b),
			new BlockData(2, 0, -1, BSHousing.b, BSPort.b),
			new BlockData(2, 1, 1, BSPort.b),
			new BlockData(2, 1, -1, BSPort.b),
			new BlockData(2, -1, 1, BSPort.b),
			new BlockData(2, -1, -1, BSPort.b),

			// West
			new BlockData(-2, 1, 0, BSHousing.b, BSPort.b),
			new BlockData(-2, -1, 0, BSHousing.b, BSPort.b),
			new BlockData(-2, 0, 1, BSHousing.b, BSPort.b),
			new BlockData(-2, 0, -1, BSHousing.b, BSPort.b),
			new BlockData(-2, 1, 1, BSPort.b),
			new BlockData(-2, 1, -1, BSPort.b),
			new BlockData(-2, -1, 1, BSPort.b),
			new BlockData(-2, -1, -1, BSPort.b),

			// Up
			new BlockData(1, 2, 0, BSHousing.b, BSPort.b),
			new BlockData(-1, 2, 0, BSHousing.b, BSPort.b),
			new BlockData(0, 2, 1, BSHousing.b, BSPort.b),
			new BlockData(0, 2, -1, BSHousing.b, BSPort.b),
			new BlockData(1, 2, 1, BSPort.b),
			new BlockData(1, 2, -1, BSPort.b),
			new BlockData(-1, 2, 1, BSPort.b),
			new BlockData(-1, 2, -1, BSPort.b),

			// Down
			new BlockData(1, -2, 0, BSHousing.b, BSPort.b),
			new BlockData(-1, -2, 0, BSHousing.b, BSPort.b),
			new BlockData(0, -2, 1, BSHousing.b, BSPort.b),
			new BlockData(0, -2, -1, BSHousing.b, BSPort.b),
			new BlockData(1, -2, 1, BSPort.b),
			new BlockData(1, -2, -1, BSPort.b),
			new BlockData(-1, -2, 1, BSPort.b),
			new BlockData(-1, -2, -1, BSPort.b),};
}

package AppliedIntegrations.tile;


import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.BlockType;

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

	public static final BlockData[] ME_SERVER = {
			// Main axises
			new BlockData(0,2,0, BlocksEnum.BSPort.b),
			new BlockData(0,1,0, BlocksEnum.BSHousing.b),
			new BlockData(2,0,0, BlocksEnum.BSPort.b),
			new BlockData(1,0,0, BlocksEnum.BSHousing.b),
			new BlockData(0,0,2, BlocksEnum.BSPort.b),
			new BlockData(0,0,1, BlocksEnum.BSHousing.b),
			//0,0,0;
			new BlockData(0,0,-1, BlocksEnum.BSHousing.b),
			new BlockData(0,0,-2, BlocksEnum.BSPort.b),
			new BlockData(-1,0,0, BlocksEnum.BSHousing.b),
			new BlockData(-2,0,0, BlocksEnum.BSPort.b),
			new BlockData(0,-1,0, BlocksEnum.BSHousing.b),
			new BlockData(0,-2,0, BlocksEnum.BSPort.b),

			// Corners
			new BlockData(-1,-1,-1, BlocksEnum.BSHousing.b),
			new BlockData(-1,-1,1, BlocksEnum.BSHousing.b),
			new BlockData(-1,1,1, BlocksEnum.BSHousing.b),
			new BlockData(1,1,1, BlocksEnum.BSHousing.b),

			new BlockData(-1,1,-1, BlocksEnum.BSHousing.b),
			new BlockData(1,1,-1, BlocksEnum.BSHousing.b),
			new BlockData(1,-1,-1, BlocksEnum.BSHousing.b),
			new BlockData(1,-1,1, BlocksEnum.BSHousing.b),
			// Lines
			new BlockData(1,1,0, BlocksEnum.BSHousing.b),
			new BlockData(1,-1,0, BlocksEnum.BSHousing.b),
			new BlockData(-1,1,0, BlocksEnum.BSHousing.b),
			new BlockData(-1,-1,0, BlocksEnum.BSHousing.b),
			new BlockData(0,1,1, BlocksEnum.BSHousing.b),
			new BlockData(0,-1,1, BlocksEnum.BSHousing.b),
			new BlockData(0,1,-1, BlocksEnum.BSHousing.b),
			new BlockData(0,-1,-1, BlocksEnum.BSHousing.b),
			new BlockData(1,0,1, BlocksEnum.BSHousing.b),
			new BlockData(1,0,-1, BlocksEnum.BSHousing.b),
			new BlockData(-1,0,1, BlocksEnum.BSHousing.b),
			new BlockData(-1,0,-1, BlocksEnum.BSHousing.b),
			// Final Layer:
			// 0,0,0
			//Corners
			new BlockData(-2,-2,-2, BlocksEnum.BSRib.b, BlockType.Corner),
			new BlockData(-2,-2,2, BlocksEnum.BSRib.b, BlockType.Corner),
			new BlockData(-2,2,2, BlocksEnum.BSRib.b, BlockType.Corner),
			new BlockData(2,2,2, BlocksEnum.BSRib.b, BlockType.Corner),

			new BlockData(-2,2,-2, BlocksEnum.BSRib.b, BlockType.Corner),
			new BlockData(2,2,-2, BlocksEnum.BSRib.b, BlockType.Corner),
			new BlockData(2,-2,-2, BlocksEnum.BSRib.b, BlockType.Corner),
			new BlockData(2,-2,2, BlocksEnum.BSRib.b, BlockType.Corner),
			//Ribs
			new BlockData(2,2,0, BlocksEnum.BSRib.b),
			new BlockData(2,-2,0, BlocksEnum.BSRib.b),
			new BlockData(-2,2,0, BlocksEnum.BSRib.b),
			new BlockData(-2,-2,0, BlocksEnum.BSRib.b),
			new BlockData(0,2,2, BlocksEnum.BSRib.b),
			new BlockData(0,-2,2, BlocksEnum.BSRib.b),
			new BlockData(0,2,-2, BlocksEnum.BSRib.b),
			new BlockData(0,-2,-2, BlocksEnum.BSRib.b),
			new BlockData(2,0,2, BlocksEnum.BSRib.b),
			new BlockData(2,0,-2, BlocksEnum.BSRib.b),
			new BlockData(-2,0,2, BlocksEnum.BSRib.b),
			new BlockData(-2,0,-2, BlocksEnum.BSRib.b),
			//RibsAdjustments
			new BlockData(2,2,1, BlocksEnum.BSRib.b),
			new BlockData(2,-2,1, BlocksEnum.BSRib.b),
			new BlockData(-2,2,1, BlocksEnum.BSRib.b),
			new BlockData(-2,-2,1, BlocksEnum.BSRib.b),
			new BlockData(1,2,2, BlocksEnum.BSRib.b),
			new BlockData(1,-2,2, BlocksEnum.BSRib.b),
			new BlockData(1,2,-2, BlocksEnum.BSRib.b),
			new BlockData(1,-2,-2, BlocksEnum.BSRib.b),
			new BlockData(2,1,2, BlocksEnum.BSRib.b),
			new BlockData(2,1,-2, BlocksEnum.BSRib.b),
			new BlockData(-2,1,2, BlocksEnum.BSRib.b),
			new BlockData(-2,1,-2, BlocksEnum.BSRib.b),

			new BlockData(2,2,-1, BlocksEnum.BSRib.b),
			new BlockData(2,-2,-1, BlocksEnum.BSRib.b),
			new BlockData(-2,2,-1, BlocksEnum.BSRib.b),
			new BlockData(-2,-2,-1, BlocksEnum.BSRib.b),
			new BlockData(-1,2,2, BlocksEnum.BSRib.b),
			new BlockData(-1,-2,2, BlocksEnum.BSRib.b),
			new BlockData(-1,2,-2, BlocksEnum.BSRib.b),
			new BlockData(-1,-2,-2, BlocksEnum.BSRib.b),
			new BlockData(2,-1,2, BlocksEnum.BSRib.b),
			new BlockData(2,-1,-2, BlocksEnum.BSRib.b),
			new BlockData(-2,-1,2, BlocksEnum.BSRib.b),
			new BlockData(-2,-1,-2, BlocksEnum.BSRib.b),
			// Edges
			// South
			new BlockData(1,0,2,BlocksEnum.BSHousing.b),
			new BlockData(-1,0,2,BlocksEnum.BSHousing.b),
			new BlockData(0,1,2,BlocksEnum.BSHousing.b),
			new BlockData(0,-1,2,BlocksEnum.BSHousing.b),
			new BlockData(1,1,2,BlocksEnum.BSHousing.b),
			new BlockData(1,-1,2,BlocksEnum.BSHousing.b),
			new BlockData(-1,1,2,BlocksEnum.BSHousing.b),
			new BlockData(-1,-1,2,BlocksEnum.BSHousing.b),
			// North
			new BlockData(1,0,-2,BlocksEnum.BSHousing.b),
			new BlockData(-1,0,-2,BlocksEnum.BSHousing.b),
			new BlockData(0,1,-2,BlocksEnum.BSHousing.b),
			new BlockData(0,-1,-2,BlocksEnum.BSHousing.b),
			new BlockData(1,1,-2,BlocksEnum.BSHousing.b),
			new BlockData(1,-1,-2,BlocksEnum.BSHousing.b),
			new BlockData(-1,1,-2,BlocksEnum.BSHousing.b),
			new BlockData(-1,-1,-2,BlocksEnum.BSHousing.b),
			// East
			new BlockData(2,1, 0, BlocksEnum.BSHousing.b),
			new BlockData(2,-1, 0, BlocksEnum.BSHousing.b),
			new BlockData(2,0, 1, BlocksEnum.BSHousing.b),
			new BlockData(2,0, -1, BlocksEnum.BSHousing.b),
			new BlockData(2,1, 1, BlocksEnum.BSHousing.b),
			new BlockData(2,1, -1, BlocksEnum.BSHousing.b),
			new BlockData(2,-1, 1, BlocksEnum.BSHousing.b),
			new BlockData(2,-1, -1, BlocksEnum.BSHousing.b),
			// West
			new BlockData(-2,1, 0, BlocksEnum.BSHousing.b),
			new BlockData(-2,-1, 0, BlocksEnum.BSHousing.b),
			new BlockData(-2,0, 1, BlocksEnum.BSHousing.b),
			new BlockData(-2,0, -1, BlocksEnum.BSHousing.b),
			new BlockData(-2,1, 1, BlocksEnum.BSHousing.b),
			new BlockData(-2,1, -1, BlocksEnum.BSHousing.b),
			new BlockData(-2,-1, 1, BlocksEnum.BSHousing.b),
			new BlockData(-2,-1, -1, BlocksEnum.BSHousing.b),
			// Up
			new BlockData(1,2, 0, BlocksEnum.BSHousing.b),
			new BlockData(-1,2, 0, BlocksEnum.BSHousing.b),
			new BlockData(0,2, 1, BlocksEnum.BSHousing.b),
			new BlockData(0,2, -1, BlocksEnum.BSHousing.b),
			new BlockData(1,2, 1, BlocksEnum.BSHousing.b),
			new BlockData(1,2, -1, BlocksEnum.BSHousing.b),
			new BlockData(-1,2, 1, BlocksEnum.BSHousing.b),
			new BlockData(-1,2, -1, BlocksEnum.BSHousing.b),
			// Down
			new BlockData(1,-2, 0, BlocksEnum.BSHousing.b),
			new BlockData(-1,-2, 0, BlocksEnum.BSHousing.b),
			new BlockData(0,-2, 1, BlocksEnum.BSHousing.b),
			new BlockData(0,-2, -1, BlocksEnum.BSHousing.b),
			new BlockData(1,-2, 1, BlocksEnum.BSHousing.b),
			new BlockData(1,-2, -1, BlocksEnum.BSHousing.b),
			new BlockData(-1,-2, 1, BlocksEnum.BSHousing.b),
			new BlockData(-1,-2, -1, BlocksEnum.BSHousing.b),

	};
}

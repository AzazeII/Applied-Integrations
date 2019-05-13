package AppliedIntegrations.tile;

import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.BlockType;

/**
 * @Author Azazell
 */
public class AIPatterns {

    public static final BlockData[] ME_LOGIC = {
            // Corners
            new BlockData(1,1,1, BlocksEnum.BLBRibs.b),
            new BlockData(-1,1,1, BlocksEnum.BLBRibs.b),
            new BlockData(1,-1,1, BlocksEnum.BLBRibs.b),
            new BlockData(-1,-1,1, BlocksEnum.BLBRibs.b),
            new BlockData(-1,1,-1, BlocksEnum.BLBRibs.b),
            new BlockData(1,1,-1, BlocksEnum.BLBRibs.b),
            new BlockData(1,-1,-1, BlocksEnum.BLBRibs.b),
            new BlockData(-1,-1,-1, BlocksEnum.BLBRibs.b),

            // up crest
            // up
            new BlockData(0,1,0, BlocksEnum.BLBRibs.b),

            // corners
            new BlockData(0,1,1, BlocksEnum.BLBRibs.b),
            new BlockData(0,1,-1, BlocksEnum.BLBRibs.b),
            new BlockData(1,1,0, BlocksEnum.BLBRibs.b),
            new BlockData(-1,1,0, BlocksEnum.BLBRibs.b),

            // down crest
            // down
            new BlockData(0, -1, 0, BlocksEnum.BLBRibs.b),

            // corners
            new BlockData(0,-1,1, BlocksEnum.BLBRibs.b),
            new BlockData(0,-1,-1, BlocksEnum.BLBRibs.b),
            new BlockData(1,-1,0, BlocksEnum.BLBRibs.b),
            new BlockData(-1,-1,0, BlocksEnum.BLBRibs.b),
    };

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
            new BlockData(1,0,2,BlocksEnum.BSDrive.b),
            new BlockData(-1,0,2,BlocksEnum.BSDrive.b),
            new BlockData(0,1,2,BlocksEnum.BSDrive.b),
            new BlockData(0,-1,2,BlocksEnum.BSDrive.b),
            new BlockData(1,1,2,BlocksEnum.BSDrive.b),
            new BlockData(1,-1,2,BlocksEnum.BSDrive.b),
            new BlockData(-1,1,2,BlocksEnum.BSDrive.b),
            new BlockData(-1,-1,2,BlocksEnum.BSDrive.b),
            // North
            new BlockData(1,0,-2,BlocksEnum.BSDrive.b),
            new BlockData(-1,0,-2,BlocksEnum.BSDrive.b),
            new BlockData(0,1,-2,BlocksEnum.BSDrive.b),
            new BlockData(0,-1,-2,BlocksEnum.BSDrive.b),
            new BlockData(1,1,-2,BlocksEnum.BSDrive.b),
            new BlockData(1,-1,-2,BlocksEnum.BSDrive.b),
            new BlockData(-1,1,-2,BlocksEnum.BSDrive.b),
            new BlockData(-1,-1,-2,BlocksEnum.BSDrive.b),
            // East
            new BlockData(2,1, 0, BlocksEnum.BSDrive.b),
            new BlockData(2,-1, 0, BlocksEnum.BSDrive.b),
            new BlockData(2,0, 1, BlocksEnum.BSDrive.b),
            new BlockData(2,0, -1, BlocksEnum.BSDrive.b),
            new BlockData(2,1, 1, BlocksEnum.BSDrive.b),
            new BlockData(2,1, -1, BlocksEnum.BSDrive.b),
            new BlockData(2,-1, 1, BlocksEnum.BSDrive.b),
            new BlockData(2,-1, -1, BlocksEnum.BSDrive.b),
            // West
            new BlockData(-2,1, 0, BlocksEnum.BSDrive.b),
            new BlockData(-2,-1, 0, BlocksEnum.BSDrive.b),
            new BlockData(-2,0, 1, BlocksEnum.BSDrive.b),
            new BlockData(-2,0, -1, BlocksEnum.BSDrive.b),
            new BlockData(-2,1, 1, BlocksEnum.BSDrive.b),
            new BlockData(-2,1, -1, BlocksEnum.BSDrive.b),
            new BlockData(-2,-1, 1, BlocksEnum.BSDrive.b),
            new BlockData(-2,-1, -1, BlocksEnum.BSDrive.b),
            // Up
            new BlockData(1,2, 0, BlocksEnum.BSDrive.b),
            new BlockData(-1,2, 0, BlocksEnum.BSDrive.b),
            new BlockData(0,2, 1, BlocksEnum.BSDrive.b),
            new BlockData(0,2, -1, BlocksEnum.BSDrive.b),
            new BlockData(1,2, 1, BlocksEnum.BSDrive.b),
            new BlockData(1,2, -1, BlocksEnum.BSDrive.b),
            new BlockData(-1,2, 1, BlocksEnum.BSDrive.b),
            new BlockData(-1,2, -1, BlocksEnum.BSDrive.b),
            // Down
            new BlockData(1,-2, 0, BlocksEnum.BSDrive.b),
            new BlockData(-1,-2, 0, BlocksEnum.BSDrive.b),
            new BlockData(0,-2, 1, BlocksEnum.BSDrive.b),
            new BlockData(0,-2, -1, BlocksEnum.BSDrive.b),
            new BlockData(1,-2, 1, BlocksEnum.BSDrive.b),
            new BlockData(1,-2, -1, BlocksEnum.BSDrive.b),
            new BlockData(-1,-2, 1, BlocksEnum.BSDrive.b),
            new BlockData(-1,-2, -1, BlocksEnum.BSDrive.b),

    };

    public static final BlockData[] ME_SERVER_FILL = {
            //Ribs
            new BlockData(2,2,0, BlocksEnum.BSRib.b,3),
            new BlockData(2,-2,0, BlocksEnum.BSRib.b,3),
            new BlockData(-2,2,0, BlocksEnum.BSRib.b,3),
            new BlockData(-2,-2,0, BlocksEnum.BSRib.b,3),

            new BlockData(0,2,2, BlocksEnum.BSRib.b,1),
            new BlockData(0,-2,2, BlocksEnum.BSRib.b,1),
            new BlockData(0,2,-2, BlocksEnum.BSRib.b,1),
            new BlockData(0,-2,-2, BlocksEnum.BSRib.b,1),
            new BlockData(2,0,2, BlocksEnum.BSRib.b,2),
            new BlockData(2,0,-2, BlocksEnum.BSRib.b,2),
            new BlockData(-2,0,2, BlocksEnum.BSRib.b,2),
            new BlockData(-2,0,-2, BlocksEnum.BSRib.b,2),
            //RibsAdjustments
            new BlockData(2,2,1, BlocksEnum.BSRib.b,3),
            new BlockData(2,-2,1, BlocksEnum.BSRib.b,3),
            new BlockData(-2,2,1, BlocksEnum.BSRib.b,3),
            new BlockData(-2,-2,1, BlocksEnum.BSRib.b,3),
            new BlockData(1,2,2, BlocksEnum.BSRib.b,1),
            new BlockData(1,-2,2, BlocksEnum.BSRib.b,1),
            new BlockData(1,2,-2, BlocksEnum.BSRib.b,1),
            new BlockData(1,-2,-2, BlocksEnum.BSRib.b,1),
            new BlockData(2,1,2, BlocksEnum.BSRib.b,2),
            new BlockData(2,1,-2, BlocksEnum.BSRib.b,2),
            new BlockData(-2,1,2, BlocksEnum.BSRib.b,2),
            new BlockData(-2,1,-2, BlocksEnum.BSRib.b,2),

            new BlockData(2,2,-1, BlocksEnum.BSRib.b,3),
            new BlockData(2,-2,-1, BlocksEnum.BSRib.b,3),
            new BlockData(-2,2,-1, BlocksEnum.BSRib.b,3),
            new BlockData(-2,-2,-1, BlocksEnum.BSRib.b,3),
            new BlockData(-1,2,2, BlocksEnum.BSRib.b,1),
            new BlockData(-1,-2,2, BlocksEnum.BSRib.b,1),
            new BlockData(-1,2,-2, BlocksEnum.BSRib.b,1),
            new BlockData(-1,-2,-2, BlocksEnum.BSRib.b,1),
            new BlockData(2,-1,2, BlocksEnum.BSRib.b,2),
            new BlockData(2,-1,-2, BlocksEnum.BSRib.b,2),
            new BlockData(-2,-1,2, BlocksEnum.BSRib.b,2),
            new BlockData(-2,-1,-2, BlocksEnum.BSRib.b,2),
    };
}

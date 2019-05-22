package AppliedIntegrations.tile.Patterns;


import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.api.Multiblocks.BlockData;
import AppliedIntegrations.api.Multiblocks.BlockType;
import AppliedIntegrations.api.Multiblocks.IAIPattern;

/**
 * @Author Azazell
 */
public class MultiControllerPattern implements IAIPattern {
	@Override
	public BlockData[] getPatternData() {
		return new BlockData[]{
				// Main axises
				new BlockData(0, 2, 0, BlocksEnum.BMCPort.b),
				new BlockData(0, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(2, 0, 0, BlocksEnum.BMCPort.b),
				new BlockData(1, 0, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, 0, 2, BlocksEnum.BMCPort.b),
				new BlockData(0, 0, 1, BlocksEnum.BMCHousing.b),
				//0,0,0;
				new BlockData(0, 0, -1, BlocksEnum.BMCHousing.b),
				new BlockData(0, 0, -2, BlocksEnum.BMCPort.b),
				new BlockData(-1, 0, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 0, 0, BlocksEnum.BMCPort.b),
				new BlockData(0, -1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, -2, 0, BlocksEnum.BMCPort.b),

				// Corners
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
				// Final Layer:
				// 0,0,0
				//Corners
				new BlockData(-2, -2, -2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(-2, -2, 2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(-2, 2, 2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(2, 2, 2, BlocksEnum.BMCRib.b, BlockType.Corner),

				new BlockData(-2, 2, -2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(2, 2, -2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(2, -2, -2, BlocksEnum.BMCRib.b, BlockType.Corner),
				new BlockData(2, -2, 2, BlocksEnum.BMCRib.b, BlockType.Corner),
				//Ribs
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
				//RibsAdjustments
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
				// Edges
				// South
				new BlockData(1, 0, 2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 0, 2, BlocksEnum.BMCHousing.b),
				new BlockData(0, 1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(0, -1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(1, 1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(1, -1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 1, 2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -1, 2, BlocksEnum.BMCHousing.b),
				// North
				new BlockData(1, 0, -2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 0, -2, BlocksEnum.BMCHousing.b),
				new BlockData(0, 1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(0, -1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(1, 1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(1, -1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 1, -2, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -1, -2, BlocksEnum.BMCHousing.b),
				// East
				new BlockData(2, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(2, -1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(2, 0, 1, BlocksEnum.BMCHousing.b),
				new BlockData(2, 0, -1, BlocksEnum.BMCHousing.b),
				new BlockData(2, 1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(2, 1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(2, -1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(2, -1, -1, BlocksEnum.BMCHousing.b),
				// West
				new BlockData(-2, 1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-2, -1, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 0, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 0, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, 1, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, -1, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-2, -1, -1, BlocksEnum.BMCHousing.b),
				// Up
				new BlockData(1, 2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, 2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(0, 2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(1, 2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, 2, -1, BlocksEnum.BMCHousing.b),
				// Down
				new BlockData(1, -2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -2, 0, BlocksEnum.BMCHousing.b),
				new BlockData(0, -2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(0, -2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(1, -2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(1, -2, -1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -2, 1, BlocksEnum.BMCHousing.b),
				new BlockData(-1, -2, -1, BlocksEnum.BMCHousing.b),};
	}
}

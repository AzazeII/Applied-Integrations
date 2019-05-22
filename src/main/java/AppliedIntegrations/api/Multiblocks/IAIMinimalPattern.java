package AppliedIntegrations.api.Multiblocks;


import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.stream.Stream;

/**
 * @Author Azazell
 * @apiNote This interface represents any multiblock pattern from
 * applied integrations <H>Which can be extended by calling method extend</H>
 */
public interface IAIMinimalPattern extends IAIPatternExtendable{
	@Override
	default IAIMinimalPattern getMinimalFrame() {
		// Since this pattern is minimal, then it can't be even more minimized
		return this;
	}

	@Override
	default Stream<BlockData> getEdgeFromFacing(EnumFacing facing) {
		return getPatternData().stream().filter((data) -> data.isPropertyOfSide(facing));
	}

	/**
	 * Override this if default calculation method isn't optimised for your tasks
	 * @return current maximum x, y and z range from core to edge of multiblock
	 */
	default BlockPos getSize(){
		// Create max position vector
		BlockPos maxVector = new BlockPos(0,0,0);

		// Iterate for each block data
		for (BlockData data : getPatternData()) {
			// Check if one of current position components greater than components of max vector
			if (data.getPos().compareTo(maxVector) > 0) {
				// Update max position vector
				maxVector = data.getPos();
			}
		}

		// Return max position vector
		return maxVector;
	}
}

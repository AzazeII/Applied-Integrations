package AppliedIntegrations.api.Multiblocks;


import appeng.api.util.AEPartLocation;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Azazell
 * @apiNote This interface represents any multiblock pattern from
 * applied integrations <H>Which can be extended by calling method extend</H>
 */
public interface IAIPatternExtendable extends IAIPattern {

	/**
	 * @return Size of minimal frame of this pattern. For multi-controller it's {@code new BlockPos(1,1,1);}
	 */
	BlockPos getMinimalFrameSize();

	/**
	 * @return Map of list of block positions of edge blocks
	 */
	default Map<AEPartLocation, List<BlockPos>> getPosEdgeMap() {
		return new HashMap<>();
	}
}

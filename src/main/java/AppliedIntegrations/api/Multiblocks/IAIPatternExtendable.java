package AppliedIntegrations.api.Multiblocks;


import net.minecraft.util.EnumFacing;

import java.util.stream.Stream;

/**
 * @Author Azazell
 * @apiNote This interface represents any multiblock pattern from
 * applied integrations <H>Which can be extended by calling method extend</H>
 */
public interface IAIPatternExtendable extends IAIPattern {
	/**
	 * @param facing edge side
	 * @return Geometrical edge of pattern from given facing
	 */
	Stream<BlockData> getEdgeFromFacing(EnumFacing facing);

	/**
	 * @return Variant of this multi-block with minimal size
	 */
	IAIMinimalPattern getMinimalFrame();
}

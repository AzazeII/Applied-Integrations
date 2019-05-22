package AppliedIntegrations.api.Multiblocks;


import net.minecraft.util.math.BlockPos;

/**
 * @Author Azazell
 * @apiNote This interface represents any multiblock pattern from
 * applied integrations <H>Which can be extended by calling method extend</H>
 */
public interface IAIPatternExtendable extends IAIPattern {

	/**
	 * @return Size of minimal frame of this pattern. For multi-controller it's {@Code new BlockPos(1,1,1);}
	 */
	BlockPos getMinimalFrameSize();
}

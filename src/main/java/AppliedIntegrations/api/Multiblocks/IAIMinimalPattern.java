package AppliedIntegrations.api.Multiblocks;


import net.minecraft.util.EnumFacing;

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
}

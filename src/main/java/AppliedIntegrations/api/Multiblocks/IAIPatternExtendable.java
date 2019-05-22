package AppliedIntegrations.api.Multiblocks;


import net.minecraft.util.EnumFacing;

/**
 * @Author Azazell
 * @apiNote This interface represents any multiblock pattern from
 * applied integrations <H>Which can be extended by calling method extend</H>
 */
public interface IAIPatternExtendable extends IAIPattern{
	BlockData[] extendPattern(EnumFacing.Axis axis);
}

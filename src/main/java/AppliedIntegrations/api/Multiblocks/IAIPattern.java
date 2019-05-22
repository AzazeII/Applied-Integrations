package AppliedIntegrations.api.Multiblocks;

/**
 * @Author Azazell
 * @apiNote This interface represents any multiblock pattern from
 * applied integrations
 */
public interface IAIPattern {
	/**
	 * @return Array of block data variables
	 */
	BlockData[] getPatternData();
}

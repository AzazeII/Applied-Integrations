package AppliedIntegrations.api.Multiblocks;


import java.util.List;

/**
 * @Author Azazell
 * @apiNote This interface represents any multiblock pattern from
 * applied integrations
 */
public interface IAIPattern {
	/**
	 * @return Array of block data variables
	 */
	List<BlockData> getPatternData();
}

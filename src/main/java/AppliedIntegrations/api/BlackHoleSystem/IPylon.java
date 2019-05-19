package AppliedIntegrations.api.BlackHoleSystem;

/**
 * @Author Azazell
 * <p>
 * Class, used to mark any pylon
 */
public interface IPylon {
	/**
	 * @param singularity Update linked singularity
	 */
	void setSingularity(ISingularity singularity);

	/**
	 * @param newValue Update drain value
	 */
	void setDrain(boolean newValue);

	/**
	 * Update cell array of pylon
	 */
	void postCellInventoryEvent();
}

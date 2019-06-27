package AppliedIntegrations.Container.Sync;


import AppliedIntegrations.api.Storage.LiquidAIEnergy;

/**
 * @Author Azazell
 */
public interface IFilterContainer {
	void updateEnergy(LiquidAIEnergy energy, int index);
}

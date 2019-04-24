package AppliedIntegrations.Parts;

import AppliedIntegrations.api.Storage.LiquidAIEnergy;

/**
 * @Author Azazell
 */
public interface IEnergyMachine
{
    void updateFilter(LiquidAIEnergy energy, int index);
}

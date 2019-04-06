package AppliedIntegrations.Parts;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;

/**
 * @Author Azazell
 */
public interface IEnergyMachine
{
    void updateFilter(LiquidAIEnergy energy, int index);
}

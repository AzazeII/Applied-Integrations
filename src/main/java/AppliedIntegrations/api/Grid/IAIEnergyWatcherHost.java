package AppliedIntegrations.api.Grid;

import AppliedIntegrations.api.Storage.LiquidAIEnergy;

/**
 * @Author Azazell
 */
public interface IAIEnergyWatcherHost
{

    void onEnergyChange(LiquidAIEnergy energy, long storedAmount, long changeAmount );

    void updateWatcher( IAIEnergyWatcher newWatcher );
}

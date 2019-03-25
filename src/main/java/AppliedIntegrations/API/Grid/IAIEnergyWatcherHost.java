package AppliedIntegrations.API.Grid;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;

/**
 * @Author Azazell
 */
public interface IAIEnergyWatcherHost
{

    void onEnergyChange(LiquidAIEnergy energy, long storedAmount, long changeAmount );

    void updateWatcher( IAIEnergyWatcher newWatcher );
}

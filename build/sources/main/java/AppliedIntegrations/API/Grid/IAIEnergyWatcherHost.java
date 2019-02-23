package AppliedIntegrations.API.Grid;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;

public interface IAIEnergyWatcherHost
{

    void onEnergyChange(LiquidAIEnergy energy, long storedAmount, long changeAmount );

    void updateWatcher( IAIEnergyWatcher newWatcher );
}

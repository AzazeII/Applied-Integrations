package AppliedIntegrations.API.Grid;

import AppliedIntegrations.API.LiquidAIEnergy;
import appeng.api.networking.energy.IEnergyWatcher;

public interface IAIEnergyWatcherHost
{

    void onEnergyChange(LiquidAIEnergy energy, long storedAmount, long changeAmount );

    void updateWatcher( IAIEnergyWatcher newWatcher );
}

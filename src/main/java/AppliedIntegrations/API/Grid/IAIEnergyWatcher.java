package AppliedIntegrations.API.Grid;

import AppliedIntegrations.API.LiquidAIEnergy;

import java.util.Collection;

public interface IAIEnergyWatcher extends Collection<LiquidAIEnergy>
{
        /**
         * Return the host for this watcher.
         *
         * @return
         */
        IAIEnergyWatcherHost getHost();
}

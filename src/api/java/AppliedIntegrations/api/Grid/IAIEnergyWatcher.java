package AppliedIntegrations.api.Grid;

import AppliedIntegrations.api.Storage.LiquidAIEnergy;

import java.util.Collection;

/**
 * @Author Azazell
 */
public interface IAIEnergyWatcher extends Collection<LiquidAIEnergy>
{
        /**
         * Return the host for this watcher.
         *
         * @return
         */
        IAIEnergyWatcherHost getHost();
}

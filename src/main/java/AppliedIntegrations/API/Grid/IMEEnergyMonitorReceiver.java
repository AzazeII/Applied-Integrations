package AppliedIntegrations.API.Grid;

import AppliedIntegrations.API.Storage.IEnergyStack;

import javax.annotation.Nonnull;
/**
 * @Author Azazell
 */
public interface IMEEnergyMonitorReceiver
{
    /**
     * Verifies that the receiver is still valid.<br>
     * If returning false the receiver will be removed from the monitor
     * and the receiver should NOT call removeListener itself.
     *
     * @param verificationToken
     * @return False if the receiver should no longer get updates.
     */
    boolean isValid( @Nonnull Object verificationToken );

    /**
     * Called when a change to the stored energy occurs.
     *
     * @param fromMonitor
     * @param changes
     */
    void postChange( @Nonnull IMEEnergyMonitor fromMonitor, @Nonnull Iterable<IEnergyStack> changes );
}
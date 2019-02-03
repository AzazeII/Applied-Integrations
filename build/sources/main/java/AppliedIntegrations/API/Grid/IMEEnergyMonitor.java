package AppliedIntegrations.API.Grid;

import AppliedIntegrations.API.IEnergyStack;
import AppliedIntegrations.API.LiquidAIEnergy;
import appeng.api.config.Actionable;
import appeng.me.helpers.BaseActionSource;

import javax.annotation.Nonnull;
import java.util.Collection;
/**
 * @Author Azazell
 */
public interface IMEEnergyMonitor {
    /**
     * Adds a listener to the energy grid.
     *
     * @param listener
     * @param verificationToken
     * Token used to verify the receiver is still valid, and wants to continue receiving events.
     */
    void addListener(@Nonnull IMEEnergyMonitorReceiver listener, @Nonnull Object verificationToken );

    /**
     * Extract the specified energy from the network.<br>
     *
     * @param energy
     * @param amount
     * @param mode
     * @param source
     * @param powered
     * If true will take the required power for the extraction, respecting the mode setting.
     * @return The amount extracted.
     */
    long extractEnergy(@Nonnull LiquidAIEnergy energy, long amount, @Nonnull Actionable mode, @Nonnull BaseActionSource source, boolean powered );

    /**
     * Returns the how much of the specified energy is in the network.
     *
     * @param energy
     * @return
     */
    long getEnergyAmount( @Nonnull LiquidAIEnergy energy );

    /**
     * Gets the list of energy in the network.
     *
     * @return
     */
    @Nonnull
    Collection<IEnergyStack> getEnergyList();

    /**
     * Inject the specified energy into the network.<br>
     *
     * @param energy
     * @param amount
     * @param mode
     * @param source
     * @param powered
     * If true will take the required power for the injection, respecting the mode setting.
     * @return The amount that could <strong>not</strong> be injected.
     */
    long injectEnergy(@Nonnull LiquidAIEnergy energy, long amount, @Nonnull Actionable mode, @Nonnull BaseActionSource source, boolean powered );


    public void removeListener( @Nonnull IMEEnergyMonitorReceiver listener );
}

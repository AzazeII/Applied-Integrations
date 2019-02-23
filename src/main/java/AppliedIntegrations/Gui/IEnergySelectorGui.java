package AppliedIntegrations.Gui;

import AppliedIntegrations.API.IEnergySelectorContainer;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/**
 * @Author Azazell
 */
public interface IEnergySelectorGui
        extends IWidgetHost
{
    /**
     * Return the selector container.
     *
     * @return
     */
    @Nonnull
    IEnergySelectorContainer getContainer();

    /**
     * Return the selected energy, or null if no energy is selected.
     *
     * @return
     */
    @Nullable
    LiquidAIEnergy getSelectedEnergy();

}
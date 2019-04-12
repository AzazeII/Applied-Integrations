package AppliedIntegrations.Gui;

import AppliedIntegrations.API.IEnergySelectorContainer;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/**
 * @Author Azazell
 */
public interface IEnergySelectorGui
        extends IWidgetHost {
    // Container linked to selector gui
    @Nonnull
    IEnergySelectorContainer getContainer();

    // Currently selected energy
    @Nullable
    LiquidAIEnergy getSelectedEnergy();

    // Setter for current energy
    void setSelectedEnergy(@Nullable LiquidAIEnergy energy);
}
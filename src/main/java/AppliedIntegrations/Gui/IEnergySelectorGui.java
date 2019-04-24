package AppliedIntegrations.Gui;

import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.api.IEnergySelectorContainer;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;

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

    // Setter for current energy amount
    void setAmount(long stackSize);
}
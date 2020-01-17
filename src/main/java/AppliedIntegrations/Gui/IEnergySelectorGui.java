package AppliedIntegrations.Gui;


import AppliedIntegrations.Gui.Hosts.IWidgetHost;
import AppliedIntegrations.api.IEnergySelectorContainer;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public interface IEnergySelectorGui extends IWidgetHost {
	@Nonnull
	IEnergySelectorContainer getContainer();

	@Nullable
	LiquidAIEnergy getSelectedEnergy();

	void setSelectedEnergy(@Nullable LiquidAIEnergy energy);
	void setAmount(long stackSize);
}
package AppliedIntegrations.Gui.Hosts;


import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.api.ISyncHost;
import appeng.helpers.IPriorityHost;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 * Extended version of normal IPriorityHost, pay attention to implemented ISyncHost interface
 */
public interface IPriorityHostExtended extends IPriorityHost, ISyncHost {
	@Nonnull
	AIGuiHandler.GuiEnum getGui();
}

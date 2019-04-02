package AppliedIntegrations.Gui;

import AppliedIntegrations.API.ISyncHost;
import AppliedIntegrations.Container.AIContainer;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;
/**
 * @Author Azazell
 */
public interface IWidgetHost{

    int getLeft();

    int getTop();

    ISyncHost getSyncHost();

    void setSyncHost(ISyncHost host);

}

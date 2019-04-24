package AppliedIntegrations.Gui.Hosts;

import AppliedIntegrations.api.ISyncHost;

/**
 * @Author Azazell
 */
public interface IWidgetHost{

    int getLeft();

    int getTop();

    ISyncHost getSyncHost();

    void setSyncHost(ISyncHost host);

}

package AppliedIntegrations.Container.Sync;
import AppliedIntegrations.api.ISyncHost;

/**
 * @Author Azazell
 */
public interface ISyncHostHolder {
	ISyncHost getSyncHost();

	void setSyncHost(ISyncHost host);
}

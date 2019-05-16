package AppliedIntegrations.Gui.ServerGUI.SubGui;


import appeng.api.util.AEPartLocation;

/**
 * @Author Azazell
 */
public class NetworkData {

	public boolean isServerNetwork;

	public AEPartLocation dir;

	public int id;

	public NetworkData(boolean isServerNetwork, AEPartLocation networkDirection, int network) {

		this.id = network;
		this.isServerNetwork = isServerNetwork;
		this.dir = networkDirection;
	}
}

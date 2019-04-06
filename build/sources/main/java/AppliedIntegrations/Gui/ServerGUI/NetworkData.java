package AppliedIntegrations.Gui.ServerGUI;

import appeng.api.util.AEPartLocation;

import java.util.EnumSet;

/**
 * @Author Azazell
 */
public class NetworkData {

    public boolean isServerNetwork;
    public AEPartLocation dir;
    public int id;

    public EnumSet<NetworkPermissions> serverPermissions;

    public NetworkData(boolean isServerNetwork, AEPartLocation networkDirection, int network){

        this.id = network;
        this.isServerNetwork = isServerNetwork;
        this.dir = networkDirection;
    }
}

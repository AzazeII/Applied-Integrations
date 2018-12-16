package AppliedIntegrations.Gui.ServerGUI;

import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;

public class NetworkData {

    public boolean isServerNetwork;
    public ForgeDirection dir;
    public int id;

    public EnumSet<NetworkPermissions> serverPermissions;

    public NetworkData(boolean isServerNetwork, ForgeDirection networkDirection, int network){

        this.id = network;
        this.isServerNetwork = isServerNetwork;
        this.dir = networkDirection;
    }
}

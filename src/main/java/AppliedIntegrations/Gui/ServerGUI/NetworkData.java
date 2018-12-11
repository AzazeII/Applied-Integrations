package AppliedIntegrations.Gui.ServerGUI;

import appeng.api.networking.IGrid;
import net.minecraftforge.common.util.ForgeDirection;

public class NetworkData {

    public boolean isServerNetwork;
    public ForgeDirection dir;
    public int id;

    public NetworkData(boolean isServerNetwork, ForgeDirection networkDirection, int network){

        this.id = network;
        this.isServerNetwork = isServerNetwork;
        this.dir = networkDirection;
    }
}

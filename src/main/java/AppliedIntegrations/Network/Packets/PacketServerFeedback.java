package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.Gui.ServerGUI.NetworkPermissions;
import AppliedIntegrations.Network.AIPacket;
import appeng.api.config.SecurityPermissions;
import appeng.api.util.AEPartLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.LinkedHashMap;

/**
 * @Author Azazell
 * @Usage This packet needed to update network permissions on me server @Link[port]
 */
public class PacketServerFeedback extends AIPacket<PacketServerFeedback> {

    public PacketServerFeedback(){ }

    public PacketServerFeedback(TileServerCore master, boolean active, int ServerID, AEPartLocation port, LinkedHashMap<SecurityPermissions,NetworkPermissions> networkPermissions){
            master.onFeedback(active,ServerID,port,networkPermissions);
    }



    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

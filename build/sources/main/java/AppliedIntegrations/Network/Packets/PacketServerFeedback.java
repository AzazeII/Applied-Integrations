package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.tile.Server.TileServerCore;
import AppliedIntegrations.Gui.ServerGUI.NetworkPermissions;
import appeng.api.config.SecurityPermissions;
import appeng.api.util.AEPartLocation;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.LinkedHashMap;

/**
 * @Author Azazell
 * @Usage This packet needed to update network permissions on me server @Link[port]
 */
public class PacketServerFeedback extends AIPacket {

    public PacketServerFeedback(){ }

    public PacketServerFeedback(TileServerCore master, boolean active, int ServerID, AEPartLocation port, LinkedHashMap<SecurityPermissions,NetworkPermissions> networkPermissions){
        master.onFeedback(active,ServerID,port,networkPermissions);
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}

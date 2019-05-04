package AppliedIntegrations.Network.Packets.Server;

import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.tile.Server.TileServerRib;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage This packet needed to sync server states of server rib with client states
 */
public class PacketRibSync extends AIPacket {

    public boolean nodeActivity;
    public TileServerRib rib;

    public PacketRibSync(){}

    public PacketRibSync(TileServerRib rib, boolean activity){
        this.rib = rib;
        this.nodeActivity = activity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.rib = (TileServerRib) readTile(buf);
        this.nodeActivity = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeTile(rib, buf);
        buf.writeBoolean(nodeActivity);
    }
}

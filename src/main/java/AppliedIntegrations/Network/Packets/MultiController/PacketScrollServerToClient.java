package AppliedIntegrations.Network.Packets.MultiController;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import io.netty.buffer.ByteBuf;

import static AppliedIntegrations.Network.ClientPacketHelper.readSyncHostClient;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage used to sync scroll between client tile and server container
 */
public class PacketScrollServerToClient extends AIPacket {
	public ISyncHost host;
	public int scroll;

	public PacketScrollServerToClient() {

	}

	public PacketScrollServerToClient(int scroll, TileMultiControllerCore master) {
		this.scroll = scroll;
		this.host = master;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		scroll = buf.readInt();
		host = readSyncHostClient(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(scroll);
		writeSyncHost(host, buf,true);
	}
}

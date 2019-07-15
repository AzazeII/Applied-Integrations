package AppliedIntegrations.Network.Packets.MultiController;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Client -> Server
 * @Usage used to sync scroll between server and client container
 */
public class PacketScrollSync extends AIPacket {
	public ISyncHost host;
	public int scroll;

	public PacketScrollSync() {

	}

	public PacketScrollSync(int scroll, TileMultiControllerCore master) {
		this.scroll = scroll;
		this.host = master;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		scroll = buf.readInt();
		host = readSyncHost(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(scroll);
		writeSyncHost(host, buf,true);
	}
}

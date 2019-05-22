package AppliedIntegrations.Network.Packets.Server;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.tile.MultiController.TileMultiControllerRib;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage This packet needed to sync server states of server rib with client states
 */
public class PacketRibSync extends AIPacket {

	public boolean nodeActivity;

	public TileMultiControllerRib rib;

	public PacketRibSync() {

	}

	public PacketRibSync(TileMultiControllerRib rib, boolean activity) {

		this.rib = rib;
		this.nodeActivity = activity;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		this.rib = (TileMultiControllerRib) readTile(buf);
		this.nodeActivity = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {

		writeTile(rib, buf);
		buf.writeBoolean(nodeActivity);
	}
}

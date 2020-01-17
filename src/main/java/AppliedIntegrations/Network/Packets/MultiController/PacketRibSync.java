package AppliedIntegrations.Network.Packets.MultiController;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.tile.MultiController.TileMultiControllerRib;
import io.netty.buffer.ByteBuf;

import static AppliedIntegrations.Network.ClientPacketHelper.readSyncHostClient;

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
		this.rib = (TileMultiControllerRib) readSyncHostClient(buf);
		this.nodeActivity = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(rib, buf, false);
		buf.writeBoolean(nodeActivity);
	}
}

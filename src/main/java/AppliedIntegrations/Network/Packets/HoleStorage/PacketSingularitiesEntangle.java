package AppliedIntegrations.Network.Packets.HoleStorage;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileWhiteHole;
import io.netty.buffer.ByteBuf;

import static AppliedIntegrations.Network.ClientPacketHelper.readSyncHostClient;

/**
 * @Author Azazell
 * @Side Server -> Client
 */
public class PacketSingularitiesEntangle extends AIPacket {
	public TileBlackHole blackHole;
	public TileWhiteHole whiteHole;

	public PacketSingularitiesEntangle() {

	}

	public PacketSingularitiesEntangle(TileBlackHole blackHole, TileWhiteHole whiteHole) {
		this.blackHole = blackHole;
		this.whiteHole = whiteHole;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		blackHole = (TileBlackHole) readSyncHostClient(buf);
		whiteHole = (TileWhiteHole) readSyncHostClient(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(blackHole, buf, false);
		writeSyncHost(whiteHole, buf, false);
	}
}

package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage This packet needed only for syncing energy interface with it's gui. Just send this packet, and it will update energy type of gui bar.
 */
public class PacketBarChange extends AIPacket {
	public LiquidAIEnergy energy;

	public ISyncHost host;

	public PacketBarChange() {

	}

	public PacketBarChange(LiquidAIEnergy energy, ISyncHost host) {
		this.energy = energy;
		this.host = host;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.energy = LiquidAIEnergy.linkedIndexMap.get(buf.readInt());
		this.host = readSyncHostClient(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.energy.getIndex());
		writeSyncHost(host, buf, false);
	}
}

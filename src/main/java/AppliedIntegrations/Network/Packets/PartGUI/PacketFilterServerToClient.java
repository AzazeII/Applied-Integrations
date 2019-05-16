package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage This packet needed to update GUI filter, send it on gui launch
 */
public class PacketFilterServerToClient extends AIPacket {

	public LiquidAIEnergy energy;

	public int index;

	public ISyncHost host;

	public PacketFilterServerToClient() {

	}

	public PacketFilterServerToClient(LiquidAIEnergy energy, int index, ISyncHost host) {

		super(host.getPos().getX(), host.getPos().getY(), host.getPos().getZ(), host.getSide().getFacing(), host.getWorld());
		this.energy = energy;
		this.index = index;
		this.host = host;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		this.energy = readEnergy(buf);
		this.index = buf.readInt();
		this.host = readSyncHost(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		writeEnergy(energy, buf);
		buf.writeInt(index);
		writeSyncHost(host, buf);
	}
}

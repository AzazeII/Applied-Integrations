package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.api.ISyncHost;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage Send this packet, whenever you want to mark gui as "Gui of THIS machine", ex:
 * you want to send data to PartEnergyStorage gui, then you need to mark gui as gui of that host, to mark gui just send this packet.
 */
public class PacketCoordinateInit extends AIPacket {

	public ISyncHost host;

	public PacketCoordinateInit() {

	}

	public PacketCoordinateInit(ISyncHost host) {
		super(host.getPos().getX(), host.getPos().getY(), host.getPos().getZ(), host.getSide().getFacing(), host.getWorld());
		this.host = host;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		boolean isPart = buf.readBoolean();

		if (isPart) {
			host = readPart(buf);
		} else {
			host = (ISyncHost) readTile(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(host, buf);
	}
}

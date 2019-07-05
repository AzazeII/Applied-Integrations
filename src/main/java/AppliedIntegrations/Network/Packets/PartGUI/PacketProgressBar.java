package AppliedIntegrations.Network.Packets.PartGUI;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.IEnergyInterface;
import appeng.api.util.AEPartLocation;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage This packet is only needed for updating energy
 *  bar in Energy interface, when you need to update current energy value, you can send this packet
 */
public class PacketProgressBar extends AIPacket {

	public IEnergyInterface sender;
	public AEPartLocation energySide;
	public Number stored;

	public PacketProgressBar() {

	}

	public PacketProgressBar(IEnergyInterface sender, AEPartLocation energySide, Number stored) {
		this.sender = sender;
		this.energySide = energySide;
		this.stored = stored;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		sender = (IEnergyInterface) readSyncHostClient(buf);
		energySide = AEPartLocation.values()[buf.readByte()];
		stored = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(sender, buf, false);
		buf.writeByte(energySide.ordinal());
		buf.writeDouble(stored.doubleValue());
	}
}

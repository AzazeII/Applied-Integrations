package AppliedIntegrations.Network.Packets;
import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import io.netty.buffer.ByteBuf;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Author Azazell
 * @Side Client -> Server
 * @Usage used to change priority value on server
 */
public class PacketPriorityChange extends AIPacket {
	public IPriorityHostExtended host;
	public String text;

	public PacketPriorityChange() {

	}

	public PacketPriorityChange(String text, IPriorityHostExtended host) {
		this.text = text;
		this.host = host;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.host = (IPriorityHostExtended) readSyncHost(buf);

		final byte[] bytes = new byte[buf.readInt()];
		buf.readBytes(bytes);
		this.text = new String(bytes, UTF_8);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(host, buf,true);

		final byte[] bytes = text.getBytes(UTF_8);
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}
}

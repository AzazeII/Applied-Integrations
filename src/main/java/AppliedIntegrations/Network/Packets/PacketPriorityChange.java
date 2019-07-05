package AppliedIntegrations.Network.Packets;
import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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
		ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(host, buf,true);
		ByteBufUtil.writeUtf8(buf, text);
	}
}

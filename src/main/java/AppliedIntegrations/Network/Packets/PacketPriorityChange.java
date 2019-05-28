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

		super(host.getHostPos().getX(), host.getHostPos().getY(), host.getHostPos().getZ(), host.getHostSide().getFacing(), host.getHostWorld());
		this.text = text;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		host = (IPriorityHostExtended) readPart(buf);

		ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		writePart(buf);

		ByteBufUtil.writeUtf8(buf, text);
	}
}

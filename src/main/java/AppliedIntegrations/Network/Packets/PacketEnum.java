package AppliedIntegrations.Network.Packets;
import AppliedIntegrations.api.IEnumHost;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Client -> Server
 * @Usage Used to pass client value to server
 */
public class PacketEnum extends AIPacket {
	// Enum of enums xD
	public enum EnumEnum {
		RedstoneMode,
		SortOrder;
	}

	public IEnumHost host;
	public Enum val;

	public PacketEnum() {

	}

	public PacketEnum(Enum val, IEnumHost syncHost) {
		this.val = val;
		this.host = syncHost;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		host = (IEnumHost) readSyncHost(buf);
		val = readEnum(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(host, buf, true);
		writeEnum(val, buf);
	}
}

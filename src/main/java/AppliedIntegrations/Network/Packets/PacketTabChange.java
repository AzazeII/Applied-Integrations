package AppliedIntegrations.Network.Packets;
import AppliedIntegrations.api.ISyncHost;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 */
public class PacketTabChange extends AIPacket {
	public ISyncHost syncHost;
	public Enum tabEnum;

	public PacketTabChange() {

	}

	public PacketTabChange(ISyncHost syncHost, Enum tabEnum) {
		this.syncHost = syncHost;
		this.tabEnum = tabEnum;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		tabEnum = readEnum(buf);
		syncHost = readSyncHost(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeEnum(tabEnum, buf);
		writeSyncHost(syncHost, buf, true);
	}
}

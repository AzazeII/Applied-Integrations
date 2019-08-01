package AppliedIntegrations.Network.Packets;
import AppliedIntegrations.api.ISyncHost;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

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
		try {
			Class enumClass = Class.forName(ByteBufUtils.readUTF8String(buf));

			tabEnum = (Enum) enumClass.getEnumConstants()[buf.readInt()];
			syncHost = readSyncHost(buf);
		} catch(ClassNotFoundException ignored) {}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, tabEnum.getDeclaringClass().getName());
		buf.writeInt(tabEnum.ordinal());
		writeSyncHost(syncHost, buf, true);
	}
}

package AppliedIntegrations.Network.Packets.MultiController;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.tile.IAIMultiBlock;
import AppliedIntegrations.tile.IMaster;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage This packet needed to sync server master with client master, which is used by server TESRs
 */
public class PacketMasterSync extends AIPacket {

	public IAIMultiBlock slave;

	public IMaster master;

	public PacketMasterSync() {

	}

	public PacketMasterSync(IAIMultiBlock slave, IMaster master) {

		this.slave = slave;
		this.master = master;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		slave = (IAIMultiBlock) readSyncHostClient(buf);

		master = buf.readBoolean() ? null : (IMaster) readSyncHostClient(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		writeSyncHost(slave, buf, false);

		buf.writeBoolean(master == null);

		writeSyncHost(master, buf, false);
	}
}

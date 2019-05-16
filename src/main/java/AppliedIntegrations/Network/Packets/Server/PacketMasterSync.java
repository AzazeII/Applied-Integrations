package AppliedIntegrations.Network.Packets.Server;


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

		slave = (IAIMultiBlock) readSyncHost(buf);

		master = buf.readBoolean() ? null : (IMaster) readSyncHost(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		writeSyncHost(slave, buf);

		buf.writeBoolean(master == null);

		writeSyncHost(master, buf);
	}
}

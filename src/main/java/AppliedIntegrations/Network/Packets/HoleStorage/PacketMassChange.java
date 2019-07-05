package AppliedIntegrations.Network.Packets.HoleStorage;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.BlackHoleSystem.ISingularity;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

import static AppliedIntegrations.Network.ClientPacketHelper.readSyncHostClient;

/**
 * @Author Azazell
 * @Side Server -> Client
 */
public class PacketMassChange extends AIPacket {

	public ISingularity singularity;

	public BlockPos pos;

	public PacketMassChange() {

	}

	public PacketMassChange(ISingularity singularity, BlockPos pos) {
		this.pos = pos;
		this.singularity = singularity;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		pos = readPos(buf);
		singularity = (ISingularity) readSyncHostClient(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		writePos(pos, buf);
		writeSyncHost(singularity, buf, false);
	}
}

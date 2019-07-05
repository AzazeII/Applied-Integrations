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
public class PacketSingularitySync extends AIPacket {

	public boolean shouldDrain;

	public ISingularity operatedTile;

	public BlockPos pos;

	public float beamState;

	public PacketSingularitySync() {

	}

	public PacketSingularitySync(ISingularity operatedTile, float beamState, boolean shouldDrain, BlockPos pos) {

		this.pos = pos;
		this.operatedTile = operatedTile;
		this.beamState = beamState;
		this.shouldDrain = shouldDrain;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		pos = readPos(buf);
		beamState = buf.readFloat();
		shouldDrain = buf.readBoolean();

		boolean isNull = buf.readBoolean();

		if (!isNull) {
			operatedTile = (ISingularity) readSyncHostClient(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {

		writePos(pos, buf);
		buf.writeFloat(beamState);
		buf.writeBoolean(shouldDrain);

		if (operatedTile != null) {
			buf.writeBoolean(false);
			writeSyncHost(operatedTile, buf, false);
		} else {
			buf.writeBoolean(true);
		}
	}
}

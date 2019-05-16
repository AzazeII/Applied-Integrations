package AppliedIntegrations.Network.Packets.HoleStorage;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.BlackHoleSystem.ISingularity;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

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
			operatedTile = (ISingularity) readTile(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {

		writePos(pos, buf);
		buf.writeFloat(beamState);
		buf.writeBoolean(shouldDrain);

		if (operatedTile != null) {
			buf.writeBoolean(false);
			writeTile((TileEntity) operatedTile, buf);
		} else {
			buf.writeBoolean(true);
		}
	}
}

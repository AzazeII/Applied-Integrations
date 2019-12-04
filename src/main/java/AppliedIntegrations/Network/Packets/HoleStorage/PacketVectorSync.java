package AppliedIntegrations.Network.Packets.HoleStorage;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * @Author Azazell
 * @Side Server -> Client
 */
public class PacketVectorSync extends AIPacket {
	public TileMETurretFoundation.Ammo ammo;
	public Vec3d blackHolePos;
	public Vec3d whiteHolePos;
	public Vec3d direction;
	public BlockPos tilePos;

	public PacketVectorSync() {}

	public PacketVectorSync(Vec3d direction, Vec3d blackHolePos, Vec3d whiteHolePos, TileMETurretFoundation.Ammo ammo, BlockPos tile) {
		this.direction = direction;
		this.blackHolePos = blackHolePos;
		this.whiteHolePos = whiteHolePos;
		this.tilePos = tile;
		this.ammo = ammo;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		direction = readVec(buf);
		blackHolePos = readVec(buf);
		whiteHolePos = readVec(buf);
		tilePos = readPos(buf);
		ammo = (TileMETurretFoundation.Ammo) readEnum(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeVec(direction, buf);
		writeVec(blackHolePos, buf);
		writeVec(whiteHolePos, buf);
		writePos(tilePos, buf);
		writeEnum(ammo, buf);
	}
}

package AppliedIntegrations.Network.Packets.HoleStorage;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.tile.HoleStorageSystem.TileMETurretFoundation;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * @Author Azazell
 * @Side Server -> Client
 */
public class PacketVectorSync extends AIPacket {
	public TileMETurretFoundation.Ammo ammo;
	public BlockPos blackHolePos;
	public BlockPos whiteHolePos;
	public BlockPos direction;
	public BlockPos tilePos;

	public PacketVectorSync() {}

	public PacketVectorSync(BlockPos direction, BlockPos blackHolePos, BlockPos whiteHolePos, TileMETurretFoundation.Ammo ammo, BlockPos tile) {
		this.direction = direction;
		this.blackHolePos = blackHolePos;
		this.whiteHolePos = whiteHolePos;
		this.tilePos = tile;
		this.ammo = ammo;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		direction = readPos(buf);
		blackHolePos = readPos(buf);
		whiteHolePos = readPos(buf);
		tilePos = readPos(buf);
		ammo = (TileMETurretFoundation.Ammo) readEnum(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writePos(direction, buf);
		writePos(blackHolePos, buf);
		writePos(whiteHolePos, buf);
		writePos(tilePos, buf);
		writeEnum(ammo, buf);
	}
}

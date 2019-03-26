package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.tile.Additions.TileMETurretFoundation;
import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public class PacketVectorSync extends AIPacket {

    public BlockPos vecA;
    public TileMETurretFoundation vecB;

    public PacketVectorSync(){}

    public PacketVectorSync(BlockPos playerPos, TileMETurretFoundation tile) {
        vecA = playerPos;
        vecB = tile;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        writePos(vecA, buf);
        writeTile(vecB, buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        vecA = readPos(buf);
        vecB = (TileMETurretFoundation)readTile(buf);
    }
}

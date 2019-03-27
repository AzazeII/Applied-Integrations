package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.tile.Additions.TileMETurretFoundation;
import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PacketVectorSync extends AIPacket {

    public BlockPos vecA;
    public BlockPos tile;

    public PacketVectorSync(){}

    public PacketVectorSync(BlockPos playerPos, BlockPos tile) {
        this.vecA = playerPos;
        this.tile = tile;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        vecA = readPos(buf);
        tile = readPos(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePos(vecA, buf);
        writePos(tile, buf);
    }
}

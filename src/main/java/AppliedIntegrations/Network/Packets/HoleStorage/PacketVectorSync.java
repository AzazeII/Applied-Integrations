package AppliedIntegrations.Network.Packets.HoleStorage;

import AppliedIntegrations.Network.Packets.AIPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * @Author Azazell
 * @Side Server -> Client
 */
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

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.ISingularity;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketSingularitySync extends AIPacket {

    public ISingularity operatedTile;
    public BlockPos pos;

    public PacketSingularitySync(){ }

    public PacketSingularitySync(ISingularity operatedTile, BlockPos pos){
        this.pos = pos;
        this.operatedTile = operatedTile;

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = readPos(buf);
        operatedTile = (ISingularity)readTile(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePos(pos, buf);
        writeTile((TileEntity) operatedTile, buf);
    }
}

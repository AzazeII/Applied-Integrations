package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.ISingularity;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketMassChange extends AIPacket {

    public ISingularity singularity;
    public BlockPos pos;

    public PacketMassChange(){ }

    public PacketMassChange(ISingularity singularity, BlockPos pos){
        this.pos = pos;
        this.singularity = singularity;

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = readPos(buf);
        singularity = (ISingularity)readTile(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePos(pos, buf);
        writeTile((TileEntity) singularity, buf);
    }
}
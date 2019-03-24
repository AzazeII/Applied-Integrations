package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.tile.Additions.storage.TileSingularity;
import io.netty.buffer.ByteBuf;

public class PacketSingularityChange extends AIPacket {

    public TileSingularity singularity;
    public int mass;

    public PacketSingularityChange(int mass, TileSingularity singularity) {
        this.mass = mass;
        this.singularity = singularity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        writeTile(singularity, buf);
        buf.writeInt(mass);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        singularity = (TileSingularity)readTile(buf);
        mass = buf.readInt();
    }
}

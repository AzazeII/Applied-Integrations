package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.tile.Additions.singularities.TileBlackHole;
import io.netty.buffer.ByteBuf;

public class PacketSingularityChange extends AIPacket {

    public TileBlackHole singularity;
    public int mass;

    public PacketSingularityChange(int mass, TileBlackHole singularity) {
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
        singularity = (TileBlackHole)readTile(buf);
        mass = buf.readInt();
    }
}

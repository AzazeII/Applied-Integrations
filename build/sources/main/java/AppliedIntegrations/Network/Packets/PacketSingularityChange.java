package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.tile.HoleStorageSystem.singularities.TileBlackHole;
import io.netty.buffer.ByteBuf;

public class PacketSingularityChange extends AIPacket {

    public TileBlackHole singularity;
    public long mass;

    public PacketSingularityChange(){}

    public PacketSingularityChange(long mass, TileBlackHole singularity) {
        this.mass = mass;
        this.singularity = singularity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        writeTile(singularity, buf);
        buf.writeLong(mass);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        singularity = (TileBlackHole)readTile(buf);
        mass = buf.readLong();
    }
}

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.tile.TileEnergyInterface;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Usage This packet is only needed for updating energy bar in Energy interface, when you need to update current energy value, you can send this packet
 */
public class PacketProgressBar extends AIPacket {

    public PartEnergyInterface sender;

    public PacketProgressBar(){

    }

    public PacketProgressBar(PartEnergyInterface sender){
        super(sender.getX(), sender.getY(), sender.getZ(), sender.getSide().getFacing(), sender.getHostTile().getWorld());
        this.sender = sender;
    }

    public PacketProgressBar(TileEnergyInterface sender) {
        super(sender.x(), sender.y(), sender.z(), null, sender.getWorld());

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        sender = (PartEnergyInterface) readPart(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePart(buf);
    }
}

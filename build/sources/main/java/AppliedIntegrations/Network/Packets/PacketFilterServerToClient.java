package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Usage This packet needed to update GUI filter, send it on gui launch
 */
public class PacketFilterServerToClient extends AIPacket {

    public LiquidAIEnergy energy;
    public int index;
    public AIPart part;

    public PacketFilterServerToClient(){

    }

    public PacketFilterServerToClient(LiquidAIEnergy energy, int index, AIPart part){
        super(part.getX(), part.getY(), part.getZ(), part.getSide().getFacing(), part.getHostTile().getWorld());
        this.energy = energy;
        this.index = index;
        this.part = part;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
       this.energy = readEnergy(buf);
       this.index = buf.readInt();
       this.part = readPart(buf);
    }


    @Override
    public void toBytes(ByteBuf buf) {
        writeEnergy(energy, buf);
        buf.writeInt(index);
        writePart(buf);
    }
}

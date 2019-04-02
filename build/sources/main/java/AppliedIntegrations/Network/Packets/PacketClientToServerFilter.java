package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.ISyncHost;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Usage This packet needed to write feedback from gui to part, send it when your filter in gui is updated
 */
public class PacketClientToServerFilter extends AIPacket{

    public LiquidAIEnergy energy;
    public int index;

    public AIPart clientPart;

    public PacketClientToServerFilter(){

    }

    public PacketClientToServerFilter(ISyncHost host, LiquidAIEnergy energy, int index) {
        super(host.getPos().getX(), host.getPos().getY(), host.getPos().getZ(), host.getSide().getFacing(), host.getWorld());
        this.energy = energy;
        this.index = index;
    }

    // Decode serialized data
    @Override
    public void fromBytes(ByteBuf buf) {
        clientPart = readPart(buf);
        energy = readEnergy(buf);
        index = buf.readInt();
    }

    // Encode data from client to server
    @Override
    public void toBytes(ByteBuf buf) {
        writePart(buf);
        writeEnergy(energy, buf);
        buf.writeInt(index);
    }
}

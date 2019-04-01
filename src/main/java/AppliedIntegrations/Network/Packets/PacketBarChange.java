package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Usage This packet needed only for syncing energy interface with it's gui. Just send this packet, and it will update energy type of gui bar.
 */
public class PacketBarChange extends AIPacket {
    public LiquidAIEnergy energy;
    public AIPart part;

    public PacketBarChange(){

    }

    public PacketBarChange(LiquidAIEnergy energy, AIPart part){
        super(part.getX(), part.getY(), part.getZ(), part.getSide().getFacing(), part.getWorld());
        this.energy = energy;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.energy = LiquidAIEnergy.linkedIndexMap.get(buf.readInt());

        part = getPart(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.energy.getIndex());

        setPart(buf);
    }
}

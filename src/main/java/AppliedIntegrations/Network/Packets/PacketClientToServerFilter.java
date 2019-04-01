package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.ISyncHost;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

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
        clientPart = getPart(buf);
        energy = getEnergy(buf);
        index = buf.readInt();
    }

    // Encode data from client to server
    @Override
    public void toBytes(ByteBuf buf) {
        setPart(buf);
        setEnergy(energy, buf);
        buf.writeInt(index);
    }
}

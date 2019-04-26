package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.tile.AITile;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Usage This packet needed to update GUI filter, send it on gui launch
 */
public class PacketFilterServerToClient extends AIPacket {

    public LiquidAIEnergy energy;
    public int index;
    public ISyncHost host;

    public PacketFilterServerToClient(){

    }

    public PacketFilterServerToClient(LiquidAIEnergy energy, int index, ISyncHost host){
        super(host.getPos().getX(), host.getPos().getY(), host.getPos().getZ(), host.getSide().getFacing(), host.getWorld());
        this.energy = energy;
        this.index = index;
        this.host = host;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.energy = readEnergy(buf);
        this.index = buf.readInt();

        boolean isPart = buf.readBoolean();

        if(isPart)
            host = readPart(buf);
        else
            host = (ISyncHost) readTile(buf);
    }


    @Override
    public void toBytes(ByteBuf buf) {
        writeEnergy(energy, buf);
        buf.writeInt(index);

        writeSyncHost(host, buf);
    }
}

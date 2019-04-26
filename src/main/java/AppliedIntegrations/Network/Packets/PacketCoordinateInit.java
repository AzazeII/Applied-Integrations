package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.AITile;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

/**
 * @Author Azazell
 *
 * @Usage Send this packet, whenever you want to mark gui as "Gui of THIS machine", ex:
 * you want to send data to PartEnergyStorage gui, then you need to mark gui as gui of that host, to mark gui just send this packet.
 */
public class PacketCoordinateInit extends AIPacket {

    public final boolean isOwnerPart;

    public ISyncHost host;

    public PacketCoordinateInit(){
        isOwnerPart = false;
    }

    public PacketCoordinateInit(int x, int y, int z, World w){
        super(x,y,z,null,w);
        isOwnerPart = false;
    }

    public PacketCoordinateInit(ISyncHost host){
        super(host.getPos().getX(), host.getPos().getY(), host.getPos().getZ(), host.getSide().getFacing(), host.getWorld());
        this.isOwnerPart = true;
        this.host = host;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean isPart = buf.readBoolean();

        if(isPart)
            host = readPart(buf);
        else
            host = (ISyncHost) readTile(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writeSyncHost(host, buf);
    }
}

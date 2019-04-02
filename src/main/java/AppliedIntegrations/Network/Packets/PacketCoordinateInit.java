package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Parts.AIPart;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

/**
 * @Author Azazell
 *
 * @Usage Send this packet, whenever you want to mark gui as "Gui of THIS machine", ex:
 * you want to send data to PartEnergyStorage gui, then you need to mark gui as gui of that part, to mark gui just send this packet.
 */
public class PacketCoordinateInit extends AIPacket {

    public final boolean isOwnerPart;

    public AIPart part;

    public PacketCoordinateInit(){
        isOwnerPart = false;
    }

    public PacketCoordinateInit(int x, int y, int z, World w){
        super(x,y,z,null,w);
        isOwnerPart = false;
    }

    public PacketCoordinateInit(AIPart part){
        super(part.getX(), part.getY(), part.getZ(), part.getSide().getFacing(), part.getHostTile().getWorld());
        isOwnerPart = true;

        this.part = part;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        part = readPart(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePart(buf);
    }
}

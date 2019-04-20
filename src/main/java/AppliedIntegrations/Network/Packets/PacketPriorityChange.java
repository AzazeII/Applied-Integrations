package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.IPriorityHostExtended;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketPriorityChange extends AIPacket {

    public IPriorityHostExtended host;
    public String text;

    public PacketPriorityChange(){

    }

    public PacketPriorityChange(String text, IPriorityHostExtended host) {
        super(host.getPos().getX(), host.getPos().getY(), host.getPos().getZ(), host.getSide().getFacing(), host.getWorld());
        this.text = text;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        host = (IPriorityHostExtended) readPart(buf);

        ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePart(buf);

        ByteBufUtil.writeUtf8(buf, text);
    }
}

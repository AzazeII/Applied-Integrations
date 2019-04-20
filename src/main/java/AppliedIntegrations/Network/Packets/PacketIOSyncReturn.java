package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.ISyncHost;
import appeng.api.config.RedstoneMode;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketIOSyncReturn extends AIPacket{

    public ISyncHost host;
    public RedstoneMode mode;

    public PacketIOSyncReturn(RedstoneMode currentValue, ISyncHost syncHost) {
        super(syncHost.getPos().getX(), syncHost.getPos().getY(), syncHost.getPos().getZ(), syncHost.getSide().getFacing(), syncHost.getWorld());
        this.mode = currentValue;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        host = readPart(buf);
        mode = RedstoneMode.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePart(buf);
        buf.writeInt(mode.ordinal());
    }
}

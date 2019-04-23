package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.ISyncHost;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SortOrder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import scala.util.control.Exception;

public class PacketSyncReturn extends AIPacket{

    public ISyncHost host;
    public Enum mode;

    // Enum of enums xD
    public enum EnumEnum{
        RedstoneMode,
        SortOrder;
    }

    public PacketSyncReturn() { }

    public PacketSyncReturn(Enum currentValue, ISyncHost syncHost) {
        super(syncHost.getPos().getX(), syncHost.getPos().getY(), syncHost.getPos().getZ(), syncHost.getSide().getFacing(), syncHost.getWorld());
        this.mode = currentValue;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        host = readPart(buf);

        // Get enum ordinal of enum class
        byte enumerationOrdinal = buf.readByte();

        // Get enum value
        EnumEnum enumClazz = EnumEnum.values()[enumerationOrdinal];

        // Check for each enum of enumeration above
        // Read it's ordinal
        if (enumClazz == EnumEnum.RedstoneMode)
            mode = RedstoneMode.values()[buf.readByte()];
        if (enumClazz == EnumEnum.SortOrder)
            mode = SortOrder.values()[buf.readByte()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePart(buf);

        // Check for each enum of enumeration above
        // Write it's ordinal
        if (mode instanceof RedstoneMode)
            buf.writeByte(EnumEnum.RedstoneMode.ordinal());
        if (mode instanceof SortOrder)
            buf.writeByte(EnumEnum.SortOrder.ordinal());

        buf.writeByte(mode.ordinal());
    }
}

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.AIPart;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFullSync extends AIPacket {

    public AIPart part;
    public byte filterSize, upgradeCount;
    public boolean redstoneControl;

    public PacketFullSync() {}

    public PacketFullSync(byte filterSize, boolean redstoneControlled, byte upgradeSpeedCount, AIOPart aioPart) {
        super(aioPart.getX(), aioPart.getY(), aioPart.getZ(), aioPart.getSide().getFacing(), aioPart.getWorld());

        this.filterSize = filterSize;
        this.redstoneControl = redstoneControlled;
        this.upgradeCount = upgradeSpeedCount;

        this.part = aioPart;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Read everything
        part = readPart(buf);

        filterSize = buf.readByte();
        upgradeCount = buf.readByte();
        redstoneControl = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // Write everything
        writePart(buf);

        buf.writeByte(filterSize);
        buf.writeByte(upgradeCount);
        buf.writeBoolean(redstoneControl);
    }
}

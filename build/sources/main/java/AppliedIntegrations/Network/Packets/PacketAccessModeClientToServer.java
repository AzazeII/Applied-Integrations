package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Parts.AIPart;
import appeng.api.config.AccessRestriction;
import io.netty.buffer.ByteBuf;

public class PacketAccessModeClientToServer extends AIPacket {

    public AIPart bus;
    public AccessRestriction val;

    public PacketAccessModeClientToServer(){}

    public PacketAccessModeClientToServer(AccessRestriction currentValue, AIPart part) {
        super(part.getX(), part.getY(), part.getZ(), part.getSide().getFacing(), part.getWorld());
        // Set val
        val = currentValue;

        // Set part
        bus = part;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        bus = readPart(buf);

        val = AccessRestriction.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        writePart(buf);

        int i = 0;

        // Iterate over all restrictions
        for(AccessRestriction restriction : AccessRestriction.values()){
            // Check if restriction present val
            if(restriction == val)
                // Write it's index
                buf.writeInt(i);
            i++;
        }
    }
}
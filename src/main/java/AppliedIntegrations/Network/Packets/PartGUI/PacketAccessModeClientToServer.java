package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.Parts.AIPart;
import appeng.api.config.AccessRestriction;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Client -> Server
 */
public class PacketAccessModeClientToServer extends AIPacket {

	public AIPart bus;

	public AccessRestriction val;

	public PacketAccessModeClientToServer() {

	}

	public PacketAccessModeClientToServer(AccessRestriction currentValue, AIPart part) {
		val = currentValue;
		bus = part;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		bus = (AIPart) readSyncHost(buf);

		val = AccessRestriction.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(bus, buf, true);

		int i = 0;

		// Iterate over all restrictions
		for (AccessRestriction restriction : AccessRestriction.values()) {
			// Check if restriction present val
			if (restriction == val)
			// Write it's index
			{
				buf.writeInt(i);
			}
			i++;
		}
	}
}

package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import appeng.api.config.AccessRestriction;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 * @Side Server -> Client
 */
public class PacketAccessModeServerToClient extends AIPacket {

	public AccessRestriction access;

	public PartEnergyStorage partEnergyStorage;

	public PacketAccessModeServerToClient() {

	}

	public PacketAccessModeServerToClient(AccessRestriction accessRestriction, PartEnergyStorage part) {
		partEnergyStorage = part;
		access = accessRestriction;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		partEnergyStorage = (PartEnergyStorage) readSyncHostClient(buf);

		access = AccessRestriction.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(partEnergyStorage, buf, false);

		int i = 0;

		// Iterate over all restrictions
		for (AccessRestriction restriction : AccessRestriction.values()) {
			// Check if restriction present val
			if (restriction == access)
			// Write it's index
			{
				buf.writeInt(i);
			}
			i++;
		}
	}
}

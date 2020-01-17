package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import appeng.api.config.AccessRestriction;
import io.netty.buffer.ByteBuf;

import static AppliedIntegrations.Network.ClientPacketHelper.readSyncHostClient;

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
		access = (AccessRestriction) readEnum(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(partEnergyStorage, buf, false);
		writeEnum(access, buf);
	}
}

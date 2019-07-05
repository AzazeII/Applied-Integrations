package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import io.netty.buffer.ByteBuf;

import javax.annotation.Nonnull;

/**
 * @Author Azazell
 * @Usage This packet needed to write feedback from gui to host, send it when your filter in gui is updated
 * @see AppliedIntegrations.Network.Handlers.PartGUI.HandlerClientToServerFilter
 */
public class PacketClientToServerFilter extends AIPacket {

	public LiquidAIEnergy energy;

	public int index;

	public ISyncHost host;

	public PacketClientToServerFilter() {

	}

	public PacketClientToServerFilter(@Nonnull ISyncHost host, LiquidAIEnergy energy, int index) {
		this.energy = energy;
		this.index = index;
		this.host = host;
	}

	// Decode serialized data
	@Override
	public void fromBytes(ByteBuf buf) {
		host = readSyncHost(buf);
		energy = readEnergy(buf);
		index = buf.readInt();
	}


	// Encode data from client to server
	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(host, buf, true);
		writeEnergy(energy, buf);
		buf.writeInt(index);
	}
}

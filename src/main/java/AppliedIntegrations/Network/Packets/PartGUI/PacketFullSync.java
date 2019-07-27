package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.config.RedstoneMode;
import io.netty.buffer.ByteBuf;

import static AppliedIntegrations.Network.ClientPacketHelper.readSyncHostClient;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage Fully sync upgradable host with it's container
 */
public class PacketFullSync extends AIPacket {

	public RedstoneMode redstoneMode;

	public ISyncHost part;

	public byte filterSize;

	public boolean redstoneControl;

	public PacketFullSync() {

	}

	public PacketFullSync(byte filterSize, RedstoneMode redstoneMode, boolean redstoneControlled, ISyncHost host) {
		this.filterSize = filterSize;
		this.redstoneControl = redstoneControlled;
		this.redstoneMode = redstoneMode;

		this.part = host;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// Read everything
		part = readSyncHostClient(buf);

		filterSize = buf.readByte();
		redstoneControl = buf.readBoolean();
		redstoneMode = RedstoneMode.values()[buf.readByte()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Write everything
		writeSyncHost(part, buf, false);

		buf.writeByte(filterSize);
		buf.writeBoolean(redstoneControl);
		buf.writeByte(redstoneMode.ordinal());
	}
}

package AppliedIntegrations.Network.Packets.PartGUI;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.config.FuzzyMode;
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
	public boolean compareFuzzy;
	public FuzzyMode fuzzyMode;

	public PacketFullSync() {

	}

	public PacketFullSync(byte filterSize, RedstoneMode redstoneMode, FuzzyMode fuzzyMode, boolean redstoneControlled, boolean compareFuzzy, ISyncHost host) {
		this.filterSize = filterSize;
		this.redstoneControl = redstoneControlled;
		this.redstoneMode = redstoneMode;
		this.compareFuzzy = compareFuzzy;
		this.fuzzyMode = fuzzyMode;

		this.part = host;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// Read everything
		part = readSyncHostClient(buf);

		filterSize = buf.readByte();
		redstoneControl = buf.readBoolean();
		compareFuzzy = buf.readBoolean();
		redstoneMode = (RedstoneMode) readEnum(buf);
		fuzzyMode = (FuzzyMode) readEnum(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Write everything
		writeSyncHost(part, buf, false);

		buf.writeByte(filterSize);
		buf.writeBoolean(redstoneControl);
		buf.writeBoolean(compareFuzzy);
		writeEnum(redstoneMode, buf);
		writeEnum(fuzzyMode, buf);
	}
}

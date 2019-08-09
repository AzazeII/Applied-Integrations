package AppliedIntegrations.Network.Packets.PartGUI;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.ISyncHost;
import appeng.api.config.FuzzyMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.YesNo;
import io.netty.buffer.ByteBuf;

import static AppliedIntegrations.Network.ClientPacketHelper.readSyncHostClient;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage Fully sync upgradable host with it's container
 */
public class PacketFullSync extends AIPacket {
	public ISyncHost part;
	public byte filterSize;
	public boolean redstoneControl;
	public boolean autoCrafting;
	public boolean compareFuzzy;
	public FuzzyMode fuzzyMode;
	public YesNo craftOnly;
	public RedstoneMode redstoneMode;

	public PacketFullSync() {

	}

	public PacketFullSync(byte filterSize, RedstoneMode redstoneMode, FuzzyMode fuzzyMode, YesNo craftOnly, boolean redstoneControlled, boolean compareFuzzy,
	                      boolean autoCrafting, ISyncHost host) {
		this.filterSize = filterSize;
		this.redstoneControl = redstoneControlled;
		this.autoCrafting = autoCrafting;
		this.redstoneMode = redstoneMode;
		this.compareFuzzy = compareFuzzy;
		this.fuzzyMode = fuzzyMode;
		this.craftOnly = craftOnly;

		this.part = host;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// Read everything
		part = readSyncHostClient(buf);

		filterSize = buf.readByte();
		redstoneControl = buf.readBoolean();
		compareFuzzy = buf.readBoolean();
		autoCrafting = buf.readBoolean();
		redstoneMode = (RedstoneMode) readEnum(buf);
		fuzzyMode = (FuzzyMode) readEnum(buf);
		craftOnly = (YesNo) readEnum(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// Write everything
		writeSyncHost(part, buf, false);

		buf.writeByte(filterSize);
		buf.writeBoolean(redstoneControl);
		buf.writeBoolean(compareFuzzy);
		buf.writeBoolean(autoCrafting);
		writeEnum(redstoneMode, buf);
		writeEnum(fuzzyMode, buf);
		writeEnum(craftOnly, buf);
	}
}

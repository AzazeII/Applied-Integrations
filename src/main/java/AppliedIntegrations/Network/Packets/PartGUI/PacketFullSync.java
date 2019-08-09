package AppliedIntegrations.Network.Packets.PartGUI;
import AppliedIntegrations.Inventory.Manager.UpgradeInventoryManager;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.Parts.Interaction.PartInteraction;
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

	public PacketFullSync() { }

	public PacketFullSync(UpgradeInventoryManager upgradeInventoryManager, ISyncHost interaction) {
		this.filterSize = upgradeInventoryManager.filterSize;
		this.redstoneControl = upgradeInventoryManager.redstoneControlled;
		this.autoCrafting = upgradeInventoryManager.autoCrafting;
		this.redstoneMode = upgradeInventoryManager.redstoneMode;
		this.compareFuzzy = upgradeInventoryManager.fuzzyCompare;
		this.fuzzyMode = upgradeInventoryManager.fuzzyMode;
		this.craftOnly = upgradeInventoryManager.craftOnly;

		this.part = interaction;
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

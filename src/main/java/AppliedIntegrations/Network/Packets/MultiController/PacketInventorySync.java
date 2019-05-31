package AppliedIntegrations.Network.Packets.MultiController;


import AppliedIntegrations.Inventory.AIGridNodeInventory;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketInventorySync extends AIPacket {
	public AIGridNodeInventory inventory;
	public TileMultiControllerCore host;
	private static final String key = "#INV_TAG_LIST";

	public PacketInventorySync () {}

	public PacketInventorySync(AIGridNodeInventory cardInventory, TileMultiControllerCore token) {
		super(token);

		this.host = token;
		this.inventory = cardInventory;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.host = (TileMultiControllerCore) readSyncHost(buf);
		this.inventory = new AIGridNodeInventory("Network Card Slots", 45, 1, host.getCardManager());
		this.inventory.readFromNBT(ByteBufUtils.readTag(buf).getTagList(key, 10));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();

		tag.setTag(key, inventory.writeToNBT());

		writeSyncHost(host, buf);
		ByteBufUtils.writeTag(buf, tag);
	}
}

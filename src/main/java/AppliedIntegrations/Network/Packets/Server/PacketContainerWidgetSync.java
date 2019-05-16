package AppliedIntegrations.Network.Packets.Server;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.tile.Server.TileServerSecurity;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * @Author Azazell
 */
public class PacketContainerWidgetSync extends AIPacket {

	public int slotY;

	public int slotX;

	public ISyncHost host;

	public ItemStack itemStack;

	public PacketContainerWidgetSync() {

	}

	public PacketContainerWidgetSync(ItemStack itemStack, TileServerSecurity terminal, int slotX, int slotY) {

		this.itemStack = itemStack;
		this.host = terminal;
		this.slotX = slotX;
		this.slotY = slotY;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		slotX = buf.readInt();
		slotY = buf.readInt();

		itemStack = new ItemStack(ByteBufUtils.readTag(buf));
		host = readSyncHost(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		buf.writeInt(slotX);
		buf.writeInt(slotY);

		ByteBufUtils.writeTag(buf, itemStack.writeToNBT(new NBTTagCompound()));
		writeSyncHost(host, buf);
	}
}

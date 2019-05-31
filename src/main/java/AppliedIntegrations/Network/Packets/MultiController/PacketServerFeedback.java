package AppliedIntegrations.Network.Packets.MultiController;


import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * @Author Azazell
 * @Side Client -> Server
 * @Usage This packet needed to update network card tag data on server from client
 */
public class PacketServerFeedback extends AIPacket {

	public NBTTagCompound tag;

	public TileMultiControllerTerminal terminal;

	public PacketServerFeedback() {

	}

	public PacketServerFeedback(NBTTagCompound tag, TileMultiControllerTerminal terminal) {

		this.tag = tag;
		this.terminal = terminal;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		tag = ByteBufUtils.readTag(buf);
		terminal = (TileMultiControllerTerminal) readSyncHost(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		ByteBufUtils.writeTag(buf, tag);
		writeSyncHost(terminal, buf);
	}
}

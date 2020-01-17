package AppliedIntegrations.Network.Packets;


import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Gui.Hosts.IPriorityHostExtended;
import AppliedIntegrations.Parts.AIPart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @Author Azazell
 * @Side Client -> Server
 * @Usage Send this packet to change current gui
 */
public class PacketGuiShift extends AIPacket {
	public EntityPlayer player;

	public AIGuiHandler.GuiEnum gui;

	public AIPart part;

	public PacketGuiShift() {

	}

	public PacketGuiShift(AIGuiHandler.GuiEnum newGui, IPriorityHostExtended part) {
		this.part = (AIPart) part;
		this.gui = newGui;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		part = (AIPart) readSyncHost(buf);
		gui = AIGuiHandler.GuiEnum.values()[buf.readInt()];
		player = Minecraft.getMinecraft().player;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(part, buf, true);
		buf.writeInt(gui.ordinal());
	}
}

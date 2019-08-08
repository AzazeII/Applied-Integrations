package AppliedIntegrations.Network.Packets.PartGUI;
import AppliedIntegrations.Gui.Part.Interaction.Buttons.ClickMode;
import AppliedIntegrations.Network.ClientPacketHelper;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.Parts.Interaction.PartInteraction;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 */
public class PacketClickModeServerToClient extends AIPacket {
	public ClickMode mode;
	public PartInteraction bus;

	public PacketClickModeServerToClient() {

	}

	public PacketClickModeServerToClient(PartInteraction plane, boolean sneaking) {
		this.mode = sneaking ? ClickMode.SHIFT_CLICK : ClickMode.CLICK;
		this.bus = plane;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		bus = (PartInteraction) ClientPacketHelper.readSyncHostClient(buf);
		mode = (ClickMode) readEnum(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeSyncHost(bus, buf, false);
		writeEnum(mode, buf);
	}
}

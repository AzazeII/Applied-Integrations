package AppliedIntegrations.Network.Packets.PartGUI;
import AppliedIntegrations.Gui.Part.Interaction.Buttons.ClickMode;
import AppliedIntegrations.Network.Packets.AIPacket;
import AppliedIntegrations.Parts.Interaction.PartInteraction;
import io.netty.buffer.ByteBuf;

/**
 * @Author Azazell
 */
public class PacketClickModeClientToServer extends AIPacket {
	public PartInteraction bus;
	public ClickMode mode;

	public PacketClickModeClientToServer() {

	}

	public PacketClickModeClientToServer(ClickMode mode, PartInteraction bus) {
		this.mode = mode;
		this.bus = bus;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		mode = (ClickMode) readEnum(buf);
		bus = (PartInteraction) readSyncHost(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		writeEnum(mode, buf);
		writeSyncHost(bus, buf, true);
	}
}
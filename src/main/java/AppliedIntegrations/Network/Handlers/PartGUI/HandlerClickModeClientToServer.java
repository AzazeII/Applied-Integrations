package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Network.Packets.PartGUI.PacketClickModeClientToServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerClickModeClientToServer implements IMessageHandler<PacketClickModeClientToServer, PacketClickModeClientToServer> {
	@Override
	public PacketClickModeClientToServer onMessage(PacketClickModeClientToServer message, MessageContext ctx) {
		message.bus.setSneakingMode(message.mode);

		return null;
	}
}

package AppliedIntegrations.Network.Handlers.MultiController;


import AppliedIntegrations.Network.Packets.MultiController.PacketServerFeedback;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerServerFeedback implements IMessageHandler<PacketServerFeedback, PacketServerFeedback> {
	public HandlerServerFeedback() {

	}

	@Override
	public PacketServerFeedback onMessage(PacketServerFeedback message, MessageContext ctx) {
		if (message.terminal != null) {
			message.terminal.updateCardData(message.tag);
		}

		return null;
	}
}

package AppliedIntegrations.Network.Handlers.Server;


import AppliedIntegrations.Network.Packets.Server.PacketServerFeedback;
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
		// Check not null
		if (message.terminal != null)
		// Update data
		{
			message.terminal.updateCardData(message.tag);
		}

		return null;
	}
}

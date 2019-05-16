package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketPriorityChange;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerPriorityChange implements IMessageHandler<PacketPriorityChange, PacketPriorityChange> {
	@Override
	public PacketPriorityChange onMessage(PacketPriorityChange message, MessageContext ctx) {
		// Set priority to parsed int, every thing is simple :P
		message.host.setPriority(Integer.parseInt(message.text));

		return null;
	}
}

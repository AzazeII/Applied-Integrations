package AppliedIntegrations.Network.Handlers;


import AppliedIntegrations.Network.Packets.PacketPriorityChange;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerPriorityChange implements IMessageHandler<PacketPriorityChange, PacketPriorityChange> {
	@Override
	public PacketPriorityChange onMessage(PacketPriorityChange message, MessageContext ctx) {
		message.host.setPriority(Integer.parseInt(message.text));
		return null;
	}
}

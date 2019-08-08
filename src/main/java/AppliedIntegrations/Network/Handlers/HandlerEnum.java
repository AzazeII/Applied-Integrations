package AppliedIntegrations.Network.Handlers;
import AppliedIntegrations.Network.Packets.PacketEnum;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerEnum implements IMessageHandler<PacketEnum, PacketEnum> {
	@Override
	public PacketEnum onMessage(PacketEnum message, MessageContext ctx) {
		message.host.setEnumVal(message.val);
		return null;
	}
}

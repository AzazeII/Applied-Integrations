package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Network.Packets.PartGUI.PacketSelectedStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerSelectedStack implements IMessageHandler<PacketSelectedStack, PacketSelectedStack> {
	@Override
	public PacketSelectedStack onMessage(PacketSelectedStack message, MessageContext ctx) {
		message.part.selectedEnergy = message.selectedEnergy;
		return null;
	}
}

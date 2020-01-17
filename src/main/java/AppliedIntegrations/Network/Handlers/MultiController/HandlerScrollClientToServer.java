package AppliedIntegrations.Network.Handlers.MultiController;
import AppliedIntegrations.Network.Packets.MultiController.PacketScrollClientToServer;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerScrollClientToServer implements IMessageHandler<PacketScrollClientToServer, PacketScrollClientToServer> {
	@Override
	public PacketScrollClientToServer onMessage(PacketScrollClientToServer message, MessageContext ctx) {
		TileMultiControllerCore host = (TileMultiControllerCore) message.host;
		host.setSlotDiff(message.scroll);
		return null;
	}
}

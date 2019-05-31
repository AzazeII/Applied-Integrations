package AppliedIntegrations.Network.Handlers.MultiController;


import AppliedIntegrations.Network.Packets.MultiController.PacketContainerWidgetSync;
import AppliedIntegrations.tile.MultiController.TileMultiControllerTerminal;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerContainerWidgetSync implements IMessageHandler<PacketContainerWidgetSync, PacketContainerWidgetSync> {

	public HandlerContainerWidgetSync() {

	}

	@Override
	public PacketContainerWidgetSync onMessage(PacketContainerWidgetSync message, MessageContext ctx) {
		// Get host and request slot update
		// Cast host
		TileMultiControllerTerminal host = (TileMultiControllerTerminal) message.host;

		// Request update
		host.updateWidgetSlotLink(message.slotX, message.slotY, message.itemStack);

		return null;
	}
}

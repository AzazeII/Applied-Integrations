package AppliedIntegrations.Network.Handlers.MultiController;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerCore;
import AppliedIntegrations.Network.Packets.MultiController.PacketScrollServerToClient;
import AppliedIntegrations.tile.MultiController.TileMultiControllerCore;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerScrollServerToClient implements IMessageHandler<PacketScrollServerToClient, PacketScrollServerToClient> {
	@Override
	public PacketScrollServerToClient onMessage(PacketScrollServerToClient message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container container = Minecraft.getMinecraft().player.openContainer;
			if (container instanceof ContainerMultiControllerCore) {
				ContainerMultiControllerCore CMCC = (ContainerMultiControllerCore) container;
				if (CMCC.getSyncHost().compareTo(message.host, true)) {
					TileMultiControllerCore host = (TileMultiControllerCore) CMCC.getSyncHost();

					host.setSlotDiff(message.scroll);
				}
			}
		});

		return null;
	}
}

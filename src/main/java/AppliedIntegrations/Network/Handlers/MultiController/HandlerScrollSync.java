package AppliedIntegrations.Network.Handlers.MultiController;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerCore;
import AppliedIntegrations.Network.Packets.MultiController.PacketScrollSync;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerScrollSync implements IMessageHandler<PacketScrollSync, PacketScrollSync> {
	@Override
	public PacketScrollSync onMessage(PacketScrollSync message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container c = Minecraft.getMinecraft().player.openContainer;

			if (c instanceof ContainerMultiControllerCore) {
				ContainerMultiControllerCore hostContainer = (ContainerMultiControllerCore) c;

				hostContainer.setSlotDiff(message.scroll);
			}
		});
		return null;
	}
}

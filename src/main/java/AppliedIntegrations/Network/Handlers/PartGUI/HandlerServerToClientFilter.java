package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.Sync.IFilterContainer;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFilterServerToClient;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerServerToClientFilter implements IMessageHandler<PacketFilterServerToClient, PacketFilterServerToClient> {

	public HandlerServerToClientFilter() {

	}

	@Override
	public PacketFilterServerToClient onMessage(PacketFilterServerToClient message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container container = Minecraft.getMinecraft().player.openContainer;

			if (container instanceof IFilterContainer) {
				IFilterContainer filterHost = (IFilterContainer) container;

				if (container instanceof AIContainer) {
					AIContainer baseGUI = (AIContainer) container;
					if (baseGUI.getSyncHost() != null && baseGUI.getSyncHost().compareTo(message.host, true)) {
						filterHost.updateEnergy(message.energy, message.index);
					}
				}
			}
		});

		return null;
	}
}

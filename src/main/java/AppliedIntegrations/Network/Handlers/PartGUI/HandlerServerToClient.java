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
public class HandlerServerToClient implements IMessageHandler<PacketFilterServerToClient, PacketFilterServerToClient> {

	public HandlerServerToClient() {

	}

	@Override
	public PacketFilterServerToClient onMessage(PacketFilterServerToClient message, MessageContext ctx) {
		// Schedule task for later call on client thread/computer
		Minecraft.getMinecraft().addScheduledTask(() -> {
			// Get container
			Container container = Minecraft.getMinecraft().player.openContainer;

			// Check if container is filter container
			if (container instanceof IFilterContainer) {
				// Cast container
				IFilterContainer filterGUI = (IFilterContainer) container;

				// Check if gui is base gui
				if (container instanceof AIContainer) {
					// Cast GUI
					AIContainer baseGUI = (AIContainer) container;

					// Check not null && Check if we are updating correct GUI
					if (baseGUI.getSyncHost() != null && baseGUI.getSyncHost().compareTo(message.host, true)) {
						// Update energy in GUI
						filterGUI.updateEnergy(message.energy, message.index);
					}
				}
			}
		});

		return null;
	}
}

package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Network.Packets.PartGUI.PacketProgressBar;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerProgressBar implements IMessageHandler<PacketProgressBar, PacketProgressBar> {

	public HandlerProgressBar() {

	}

	@Override
	public PacketProgressBar onMessage(PacketProgressBar message, MessageContext ctx) {
		// Invoke later on client thread
		Minecraft.getMinecraft().addScheduledTask(() -> {
			// Get current screen
			Container container = Minecraft.getMinecraft().player.openContainer;

			// Check if container instanceof energy interface container
			if (container instanceof ContainerEnergyInterface) {
				// Cast container
				ContainerEnergyInterface CEI = (ContainerEnergyInterface) container;

				// Check if we are updating correct container
				if (CEI.getSyncHost().equals(message.sender)) {
					// Pass call to container
					CEI.onStorageUpdate(message.energySide, message.sender, message.stored);
				}
			}
		});

		return null;
	}
}

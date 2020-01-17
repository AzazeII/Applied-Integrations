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
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container container = Minecraft.getMinecraft().player.openContainer;

			if (container instanceof ContainerEnergyInterface) {
				ContainerEnergyInterface CEI = (ContainerEnergyInterface) container;

				if (CEI.getSyncHost().equals(message.sender)) {
					CEI.onStorageUpdate(message.energySide, message.sender, message.stored);
				}
			}
		});

		return null;
	}
}

package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Container.part.ContainerEnergyInterface;
import AppliedIntegrations.Network.Packets.PartGUI.PacketBarChange;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerBarChange implements IMessageHandler<PacketBarChange, PacketBarChange> {

	public HandlerBarChange() {

	}

	@Override
	public PacketBarChange onMessage(PacketBarChange message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container container = Minecraft.getMinecraft().player.openContainer;

			if (container instanceof ContainerEnergyInterface) {
				ContainerEnergyInterface CEI = (ContainerEnergyInterface) container;

				// Check if we are updating correct container
				if (CEI.getSyncHost().compareTo(message.host, false)) {
					// Update linked energy
					CEI.linkedMetric = message.energy;
				}
			}
		});
		return null;
	}
}

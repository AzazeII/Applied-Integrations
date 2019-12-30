package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Container.part.ContainerEnergyTerminal;
import AppliedIntegrations.Network.Packets.PartGUI.PacketTerminalUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerTerminalUpdate implements IMessageHandler<PacketTerminalUpdate, PacketTerminalUpdate> {
	public HandlerTerminalUpdate() {

	}

	@Override
	public PacketTerminalUpdate onMessage(PacketTerminalUpdate message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container container = Minecraft.getMinecraft().player.openContainer;
			if (container instanceof ContainerEnergyTerminal) {
				// Get terminal container
				ContainerEnergyTerminal dualityTerminal = (ContainerEnergyTerminal) container;

				// Check if we are updating correct GUI
				if ((dualityTerminal.getSyncHost().compareTo(message.part, true))) {
					dualityTerminal.updateList(message.list);
					dualityTerminal.sortMode = message.order;
					dualityTerminal.sortButton.set(message.order);
				}
			}
		});

		return null;
	}
}

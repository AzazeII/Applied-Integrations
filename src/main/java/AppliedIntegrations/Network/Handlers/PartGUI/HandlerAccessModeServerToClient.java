package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Container.part.ContainerEnergyStorage;
import AppliedIntegrations.Network.Packets.PartGUI.PacketAccessModeServerToClient;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerAccessModeServerToClient implements IMessageHandler<PacketAccessModeServerToClient, PacketAccessModeServerToClient> {

	@Override
	public PacketAccessModeServerToClient onMessage(PacketAccessModeServerToClient message, MessageContext ctx) {

		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container container = Minecraft.getMinecraft().player.openContainer;
			if (container instanceof ContainerEnergyStorage) {
				ContainerEnergyStorage CES = (ContainerEnergyStorage) container;
				if (CES.getSyncHost().compareTo(message.partEnergyStorage, true)) {
					CES.accessMode.set(message.access);
				}
			}
		});

		return null;
	}
}

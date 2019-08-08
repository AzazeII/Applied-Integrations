package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Container.part.ContainerInteractionBus;
import AppliedIntegrations.Network.Packets.PartGUI.PacketClickModeServerToClient;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerClickModeServerToClient implements IMessageHandler<PacketClickModeServerToClient, PacketClickModeServerToClient> {
	@Override
	public PacketClickModeServerToClient onMessage(PacketClickModeServerToClient message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container container = Minecraft.getMinecraft().player.openContainer;
			if (container instanceof ContainerInteractionBus) {
				ContainerInteractionBus CIB = (ContainerInteractionBus) container;

				if (CIB.getSyncHost().compareTo(message.bus, true)) {
					CIB.shiftClickButton.mode = message.mode;
				}
			}
		});
		return null;
	}
}

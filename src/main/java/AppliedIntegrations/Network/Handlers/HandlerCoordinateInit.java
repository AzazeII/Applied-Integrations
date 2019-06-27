package AppliedIntegrations.Network.Handlers;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerCoordinateInit implements IMessageHandler<PacketCoordinateInit, PacketCoordinateInit> {

	public HandlerCoordinateInit() {

	}

	@Override
	public PacketCoordinateInit onMessage(PacketCoordinateInit message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container c = Minecraft.getMinecraft().player.openContainer;

			if (c instanceof AIContainer) {
				AIContainer hostContainer = (AIContainer) c;

				hostContainer.setSyncHost(message.host);
			}
		});
		return null;
	}
}

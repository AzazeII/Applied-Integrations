package AppliedIntegrations.Network.Handlers.HoleStorage;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketSingularitiesEntangle;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerSingularitiesEntangle implements IMessageHandler<PacketSingularitiesEntangle, PacketSingularitiesEntangle> {
	@Override
	public PacketSingularitiesEntangle onMessage(PacketSingularitiesEntangle message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			if (message.whiteHole == null || message.blackHole == null) {
				return;
			}

			// Link two holes together allowing render to draw line between them
			message.whiteHole.entangledHole = message.blackHole;
			message.blackHole.entangledHole = message.whiteHole;
			message.whiteHole.notifyClientAboutSingularitiesEntangle = true;
		});
		return null;
	}
}

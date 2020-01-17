package AppliedIntegrations.Network.Handlers.MultiController;


import AppliedIntegrations.Network.Packets.MultiController.PacketRibSync;
import AppliedIntegrations.tile.MultiController.TileMultiControllerRib;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerRibSync implements IMessageHandler<PacketRibSync, PacketRibSync> {

	public HandlerRibSync() {

	}

	@Override
	public PacketRibSync onMessage(PacketRibSync message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			TileMultiControllerRib rib = (TileMultiControllerRib) Minecraft.getMinecraft().world.getTileEntity(message.rib.getPos());
			if (rib != null) {
				rib.isActive = message.nodeActivity;
			}
		});

		return null;
	}
}

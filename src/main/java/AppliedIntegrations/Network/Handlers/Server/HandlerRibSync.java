package AppliedIntegrations.Network.Handlers.Server;

import AppliedIntegrations.Network.Packets.Server.PacketRibSync;
import AppliedIntegrations.tile.Server.TileServerRib;
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
			// Get client minecraft world, then get position of recorded tile entity and get tile with client world
			TileServerRib rib = (TileServerRib) Minecraft.getMinecraft().world.getTileEntity(message.rib.getPos());

			// Check not null
			if (rib != null) {
				// Update activity of rib
				rib.isActive = message.nodeActivity;
			}
		});

		return null;
	}
}

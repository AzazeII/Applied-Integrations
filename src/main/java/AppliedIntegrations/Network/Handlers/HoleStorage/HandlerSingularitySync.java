package AppliedIntegrations.Network.Handlers.HoleStorage;


import AppliedIntegrations.Network.Packets.HoleStorage.PacketPylonSingularitySync;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerSingularitySync implements IMessageHandler<PacketPylonSingularitySync, PacketPylonSingularitySync> {

	public HandlerSingularitySync() {

	}

	@Override
	public PacketPylonSingularitySync onMessage(PacketPylonSingularitySync message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			TileMEPylon te = (TileMEPylon) Minecraft.getMinecraft().world.getTileEntity(message.pos);
			te.operatedTile = message.operatedTile;
			te.beamDrain = message.beamState;
			te.shouldDrain = message.shouldDrain;
		});

		return null;
	}
}


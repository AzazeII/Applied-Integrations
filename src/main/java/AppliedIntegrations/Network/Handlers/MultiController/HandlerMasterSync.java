package AppliedIntegrations.Network.Handlers.MultiController;


import AppliedIntegrations.Network.Packets.MultiController.PacketMasterSync;
import AppliedIntegrations.tile.IAIMultiBlock;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerMasterSync implements IMessageHandler<PacketMasterSync, PacketMasterSync> {
	@Override
	public PacketMasterSync onMessage(PacketMasterSync message, MessageContext ctx) {
		// Schedule master update
		Minecraft.getMinecraft().addScheduledTask(() -> {
			// Cast tile to multi block
			IAIMultiBlock multiBlock = (IAIMultiBlock) Minecraft.getMinecraft().world.getTileEntity(message.slave.getHostPos());

			// Check not null
			if (multiBlock != null) {
				// Set master
				multiBlock.setMaster(message.master);
			}
		});
		return null;
	}
}

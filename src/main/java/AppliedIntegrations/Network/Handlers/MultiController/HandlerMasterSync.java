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
		Minecraft.getMinecraft().addScheduledTask(() -> {
			IAIMultiBlock multiBlock = (IAIMultiBlock) Minecraft.getMinecraft().world.getTileEntity(message.slave.getHostPos());
			if (multiBlock != null) {
				multiBlock.setMaster(message.master);
			}
		});
		return null;
	}
}

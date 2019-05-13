package AppliedIntegrations.Network.Handlers.Server;

import AppliedIntegrations.Network.Packets.Server.PacketMasterSync;
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
        Minecraft.getMinecraft().addScheduledTask(() -> message.slave.setMaster(message.master));
        return null;
    }
}

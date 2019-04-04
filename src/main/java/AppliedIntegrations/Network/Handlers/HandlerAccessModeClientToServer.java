package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketAccessModeClientToServer;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerAccessModeClientToServer implements IMessageHandler<PacketAccessModeClientToServer,PacketAccessModeClientToServer> {
    @Override
    public PacketAccessModeClientToServer onMessage(PacketAccessModeClientToServer message, MessageContext ctx) {
        if(message.bus instanceof PartEnergyStorage)
            ((PartEnergyStorage)message.bus).setAccess(message.val);

        return null;
    }
}

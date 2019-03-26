package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketVectorSync;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerVectorSync implements IMessageHandler<PacketVectorSync, PacketVectorSync> {
    @Override
    public PacketVectorSync onMessage(PacketVectorSync message, MessageContext ctx) {

        message.vecB.renderingDirection = message.vecA;

        return null;
    }
}

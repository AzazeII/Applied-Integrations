package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketServerFeedback;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerServerFeedback implements IMessageHandler<PacketServerFeedback, PacketServerFeedback> {

    public HandlerServerFeedback(){

    }

    @Override
    public PacketServerFeedback onMessage(PacketServerFeedback message, MessageContext ctx) {
        return null;
    }
}

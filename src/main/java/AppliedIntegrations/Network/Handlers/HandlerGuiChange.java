package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Network.Packets.PacketGuiChange;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerGuiChange implements IMessageHandler<PacketGuiChange, PacketGuiChange> {

    public HandlerGuiChange(){

    }

    @Override
    public PacketGuiChange onMessage(PacketGuiChange message, MessageContext ctx) {
        message.p.openGui(AppliedIntegrations.instance,9,
                message.p.world,
                message.x,
                message.y,
                message.z);
        return null;
    }
}

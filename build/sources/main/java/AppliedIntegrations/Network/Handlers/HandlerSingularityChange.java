package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketSingularityChange;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerSingularityChange implements IMessageHandler<PacketSingularityChange, PacketSingularityChange> {
    @Override
    public PacketSingularityChange onMessage(PacketSingularityChange message, MessageContext ctx) {
        message.singularity.setMassFromServer(message.mass);
        return null;
    }
}

package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketIOSyncReturn;
import AppliedIntegrations.Parts.AIOPart;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerIOSyncReturn implements IMessageHandler<PacketIOSyncReturn, PacketIOSyncReturn> {
    @Override
    public PacketIOSyncReturn onMessage(PacketIOSyncReturn message, MessageContext ctx) {

        ((AIOPart)message.host).setRedstoneMode(message.mode);

        return null;
    }
}

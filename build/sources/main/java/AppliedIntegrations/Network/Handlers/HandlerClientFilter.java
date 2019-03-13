package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketClientFilter;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AILog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerClientFilter implements IMessageHandler<PacketClientFilter, PacketClientFilter> {

    public HandlerClientFilter(){

    }

    @Override
    public PacketClientFilter onMessage(PacketClientFilter message, MessageContext ctx) {
        if(message.clientPart instanceof IEnergyMachine){
            ((IEnergyMachine) message.clientPart).updateFilter(message.energy, message.index);
        }
        return null;
    }
}

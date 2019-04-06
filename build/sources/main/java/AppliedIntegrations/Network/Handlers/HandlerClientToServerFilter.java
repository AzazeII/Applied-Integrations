package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketClientToServerFilter;
import AppliedIntegrations.Parts.IEnergyMachine;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerClientToServerFilter implements IMessageHandler<PacketClientToServerFilter, PacketClientToServerFilter> {

    public HandlerClientToServerFilter(){

    }

    @Override
    public PacketClientToServerFilter onMessage(PacketClientToServerFilter message, MessageContext ctx) {
        if(message.clientPart instanceof IEnergyMachine){
            ((IEnergyMachine) message.clientPart).updateFilter(message.energy, message.index);
        }

        return null;
    }
}

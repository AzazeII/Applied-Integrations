package AppliedIntegrations.Network.Handlers.PartGUI;

import AppliedIntegrations.Network.Packets.PartGUI.PacketClientToServerFilter;
import AppliedIntegrations.Parts.IEnergyMachine;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerClientToServerFilter implements IMessageHandler<PacketClientToServerFilter, PacketClientToServerFilter> {

	public HandlerClientToServerFilter() {

	}

	@Override
	public PacketClientToServerFilter onMessage(PacketClientToServerFilter message, MessageContext ctx) {
		if (message.host instanceof IEnergyMachine) {
			((IEnergyMachine) message.host).updateFilter(message.energy, message.index);
		}

		return null;
	}
}

package AppliedIntegrations.Network.Handlers;


import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerGuiShift implements IMessageHandler<PacketGuiShift, PacketGuiShift> {
	@Override
	public PacketGuiShift onMessage(PacketGuiShift message, MessageContext ctx) {
		AIGuiHandler.open(message.gui, message.player, message.part.getHostSide(), message.part.getHostTile().getPos());
		return null;
	}
}

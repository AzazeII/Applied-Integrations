package AppliedIntegrations.Network.Handlers.PartGUI;


import AppliedIntegrations.Network.Packets.PartGUI.PacketSyncReturn;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SortOrder;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerSyncReturn implements IMessageHandler<PacketSyncReturn, PacketSyncReturn> {
	@Override
	public PacketSyncReturn onMessage(PacketSyncReturn message, MessageContext ctx) {
		// Check for enum type (^)
		// Update enum of host (&)
		if (message.mode instanceof RedstoneMode) // ^1
		{
			((AIOPart) message.host).setRedstoneMode((RedstoneMode) message.mode); // &1
		}
		if (message.mode instanceof SortOrder) // ^2
		{
			((PartEnergyTerminal) message.host).setSortMode((SortOrder) message.mode); // &2
		}
		return null;
	}
}

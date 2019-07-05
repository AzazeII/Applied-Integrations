package AppliedIntegrations.Network.Handlers.PartGUI;


import AppliedIntegrations.Network.Packets.PartGUI.PacketSyncReturn;
import AppliedIntegrations.Parts.AIOPart;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SortOrder;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerSyncReturn implements IMessageHandler<PacketSyncReturn, PacketSyncReturn> {
	@Override
	public PacketSyncReturn onMessage(PacketSyncReturn message, MessageContext ctx) {
		if (message.mode instanceof RedstoneMode) {
			((AIOPart) message.host).setRedstoneMode((RedstoneMode) message.mode);
		}

		if (message.mode instanceof SortOrder) {
			((PartEnergyTerminal) message.host).setSortMode((SortOrder) message.mode);
		}
		return null;
	}
}

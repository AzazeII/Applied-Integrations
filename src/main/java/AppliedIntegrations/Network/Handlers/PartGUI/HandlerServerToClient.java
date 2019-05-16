package AppliedIntegrations.Network.Handlers.PartGUI;

import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFilterServerToClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerServerToClient implements IMessageHandler<PacketFilterServerToClient, PacketFilterServerToClient> {

	public HandlerServerToClient() {

	}

	@Override
	public PacketFilterServerToClient onMessage(PacketFilterServerToClient message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Gui gui = Minecraft.getMinecraft().currentScreen;
			if (gui instanceof IFilterGUI) {
				if (gui instanceof AIBaseGui) {
					// Check if we are updating correct GUI
					if (((AIBaseGui) gui).getSyncHost().compareTo(message.host, true)) {
						((IFilterGUI) gui).updateEnergy(message.energy, message.index);
					}
				}
			}
		});

		return null;
	}
}

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
		// Schedule later call on client thread/computer
		Minecraft.getMinecraft().addScheduledTask(() -> {
			// Get GUI
			Gui gui = Minecraft.getMinecraft().currentScreen;

			// Check if gui is filter gui
			if (gui instanceof IFilterGUI) {
				// Cast GUI
				IFilterGUI filterGUI = (IFilterGUI) gui;

				// Check if gui is base gui
				if (gui instanceof AIBaseGui) {
					// Cast GUI
					AIBaseGui baseGUI = (AIBaseGui) gui;

					// Check not null && Check if we are updating correct GUI
					if (baseGUI.getSyncHost() != null && baseGUI.getSyncHost().compareTo(message.host, true)) {
						// Update energy in GUI
						filterGUI.updateEnergy(message.energy, message.index);
					}
				}
			}
		});

		return null;
	}
}

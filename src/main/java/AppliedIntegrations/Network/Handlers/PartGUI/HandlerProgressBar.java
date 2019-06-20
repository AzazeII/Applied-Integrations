package AppliedIntegrations.Network.Handlers.PartGUI;


import AppliedIntegrations.Gui.Part.GuiEnergyInterface;
import AppliedIntegrations.Network.Packets.PartGUI.PacketProgressBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerProgressBar implements IMessageHandler<PacketProgressBar, PacketProgressBar> {

	public HandlerProgressBar() {

	}

	@Override
	public PacketProgressBar onMessage(PacketProgressBar message, MessageContext ctx) {
		// Invoke later on client thread
		Minecraft.getMinecraft().addScheduledTask(() -> {
			// Get current screen
			Gui g = Minecraft.getMinecraft().currentScreen;

			// Check if gui instanceof energy interface gui
			if (g instanceof GuiEnergyInterface) {
				// Cast gui
				GuiEnergyInterface GEI = (GuiEnergyInterface) g;

				// Check if we are updating correct GUI
				if (GEI.getSyncHost().equals(message.sender)) {
					// Pass call to GUI
					GEI.onStorageUpdate(message.energy, message.energySide, message.sender);
				}
			}
		});

		return null;
	}
}

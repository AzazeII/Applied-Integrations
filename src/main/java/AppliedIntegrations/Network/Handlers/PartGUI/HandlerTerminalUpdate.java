package AppliedIntegrations.Network.Handlers.PartGUI;


import AppliedIntegrations.Gui.Part.GuiEnergyTerminalDuality;
import AppliedIntegrations.Network.Packets.PartGUI.PacketTerminalUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerTerminalUpdate implements IMessageHandler<PacketTerminalUpdate, PacketTerminalUpdate> {

	public HandlerTerminalUpdate() {

	}

	@Override
	public PacketTerminalUpdate onMessage(PacketTerminalUpdate message, MessageContext ctx) {

		Minecraft.getMinecraft().addScheduledTask(() -> {
			Gui gui = Minecraft.getMinecraft().currentScreen;
			if (gui instanceof GuiEnergyTerminalDuality) {
				// Get terminal gui
				GuiEnergyTerminalDuality dualityTerminal = (GuiEnergyTerminalDuality) gui;

				// Check if we are updating correct GUI
				if ((dualityTerminal.getSyncHost().compareTo(message.part, true))) {
					dualityTerminal.updateList(message.list);
					dualityTerminal.sortMode = message.order;
					dualityTerminal.sortButton.set(message.order);
				}
			}
		});

		return null;
	}
}

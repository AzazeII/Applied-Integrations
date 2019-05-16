package AppliedIntegrations.Network.Handlers.PartGUI;

import AppliedIntegrations.Gui.Part.GuiEnergyIO;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFullSync;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerFullSync implements IMessageHandler<PacketFullSync, PacketFullSync> {
	@Override
	public PacketFullSync onMessage(PacketFullSync message, MessageContext ctx) {

		Minecraft.getMinecraft().addScheduledTask(() -> {
			Gui gui = Minecraft.getMinecraft().currentScreen;
			if (gui instanceof GuiEnergyIO) {
				GuiEnergyIO GEIO = (GuiEnergyIO) gui;

				// Check not null
				if (GEIO.getSyncHost() == null) {
					return;
				}

				// Compare sync hosts
				if (GEIO.getSyncHost().compareTo(message.part, true)) {
					// Update each state
					GEIO.updateState(message.redstoneControl, message.redstoneMode, message.filterSize);
				}
			}
		});

		return null;
	}
}

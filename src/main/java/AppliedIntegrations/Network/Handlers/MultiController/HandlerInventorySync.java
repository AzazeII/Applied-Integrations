package AppliedIntegrations.Network.Handlers.MultiController;


import AppliedIntegrations.Gui.MultiController.GuiMultiControllerCore;
import AppliedIntegrations.Network.Packets.MultiController.PacketInventorySync;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class HandlerInventorySync implements IMessageHandler<PacketInventorySync, PacketInventorySync> {
	public HandlerInventorySync() {}

	@Override
	public PacketInventorySync onMessage(PacketInventorySync message, MessageContext ctx) {
		// Schedule update
		Minecraft.getMinecraft().addScheduledTask(() -> {
			// Get current GUI
			Gui gui = Minecraft.getMinecraft().currentScreen;

			// Check if gui is gui of multi-controller core
			if (gui instanceof GuiMultiControllerCore) {
				// Cast to GuiMultiControllerCore
				GuiMultiControllerCore guiCore = (GuiMultiControllerCore) gui;

				// Check if we are updating correct GUI
				if (guiCore.getSyncHost().compareTo(message.host, true)) {
					// Create initial list
					List<IAEItemStack> stackList = new ArrayList<>();

					// Iterate for each stack in inventory
					for (ItemStack stack : message.inventory.slots) {
						// Add item stack to list
						stackList.add(AEItemStack.fromItemStack(stack));
					}

					// Call update receive method
					guiCore.receiveServerData(stackList);
				}
			}
		});

		return null;
	}
}

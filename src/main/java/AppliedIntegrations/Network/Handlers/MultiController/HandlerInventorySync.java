package AppliedIntegrations.Network.Handlers.MultiController;
import AppliedIntegrations.Container.tile.MultiController.ContainerMultiControllerCore;
import AppliedIntegrations.Network.Packets.MultiController.PacketInventorySync;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
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
			// Get current container
			Container container = Minecraft.getMinecraft().player.openContainer;

			// Check if gui is gui of multi-controller core
			if (container instanceof ContainerMultiControllerCore) {
				// Cast to ContainerMultiControllerCore
				ContainerMultiControllerCore containerMCC = (ContainerMultiControllerCore) container;

				// Check if we are updating correct container, by comparing sync host with host from message
				if (containerMCC.getSyncHost().compareTo(message.host, true)) {
					// Create initial list
					List<IAEItemStack> stackList = new ArrayList<>();

					// Iterate for each stack in inventory
					for (ItemStack stack : message.inventory.slots) {
						// Add item stack to list
						stackList.add(AEItemStack.fromItemStack(stack));
					}

					// Call update receive method
					containerMCC.receiveServerData(stackList);
				}
			}
		});

		return null;
	}
}

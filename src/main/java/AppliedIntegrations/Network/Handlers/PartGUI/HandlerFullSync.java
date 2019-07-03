package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Container.part.ContainerPartEnergyIOBus;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFullSync;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerFullSync implements IMessageHandler<PacketFullSync, PacketFullSync> {
	@Override
	public PacketFullSync onMessage(PacketFullSync message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container container = Minecraft.getMinecraft().player.openContainer;
			if (container instanceof ContainerPartEnergyIOBus) {
				ContainerPartEnergyIOBus CEIOB = (ContainerPartEnergyIOBus) container;

				// Check not null
				if (CEIOB.getSyncHost() == null) {
					return;
				}

				// Compare sync hosts
				if (CEIOB.getSyncHost().compareTo(message.part, true)) {
					// Update each state
					CEIOB.updateState(message.redstoneControl, message.redstoneMode, message.filterSize);
				}
			}
		});

		return null;
	}
}

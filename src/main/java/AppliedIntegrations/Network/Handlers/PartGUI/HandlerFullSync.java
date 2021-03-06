package AppliedIntegrations.Network.Handlers.PartGUI;
import AppliedIntegrations.Container.part.IUpgradeHostContainer;
import AppliedIntegrations.Network.Packets.PartGUI.PacketFullSync;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerFullSync implements IMessageHandler<PacketFullSync, PacketFullSync> {
	@Override
	public PacketFullSync onMessage(PacketFullSync message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			Container container = Minecraft.getMinecraft().player.openContainer;
			if (container instanceof IUpgradeHostContainer) {
				IUpgradeHostContainer upgradeHostContainer = (IUpgradeHostContainer) container;

				if (upgradeHostContainer.getSyncHost() == null) {
					return;
				}

				if (upgradeHostContainer.getSyncHost().compareTo(message.part, true)) {
					upgradeHostContainer.updateState(message.redstoneControl, message.compareFuzzy, message.autoCrafting,
							message.redstoneMode, message.fuzzyMode, message.craftOnly, message.filterSize);
				}
			}
		});

		return null;
	}
}

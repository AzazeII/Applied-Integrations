package AppliedIntegrations.Network.Handlers;
import AppliedIntegrations.Container.AIContainer;
import AppliedIntegrations.Container.Sync.ITabContainer;
import AppliedIntegrations.Network.Packets.PacketTabChange;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerTabChange implements IMessageHandler<PacketTabChange, PacketTabChange> {
	@Override
	public PacketTabChange onMessage(PacketTabChange message, MessageContext ctx) {
		// Get container
		Container container = Minecraft.getMinecraft().player.openContainer;

		// Check if container is tab container
		if (container instanceof ITabContainer) {
			// Cast container
			ITabContainer tabContainer = (ITabContainer) container;

			// Check if gui is base gui
			if (container instanceof AIContainer) {
				// Cast GUI
				AIContainer baseGUI = (AIContainer) container;

				// Check not null && Check if we are updating correct filter host
				if (baseGUI.getSyncHost() != null && baseGUI.getSyncHost().compareTo(message.syncHost, true)) {
					// Update energy in container
					tabContainer.setTab(message.tabEnum);
				}
			}
		}

		return null;
	}
}

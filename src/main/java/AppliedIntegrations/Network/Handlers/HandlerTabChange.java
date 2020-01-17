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
		Container container = Minecraft.getMinecraft().player.openContainer;

		if (container instanceof ITabContainer) {
			ITabContainer tabContainer = (ITabContainer) container;

			if (container instanceof AIContainer) {
				AIContainer baseGUI = (AIContainer) container;

				if (baseGUI.getSyncHost() != null && baseGUI.getSyncHost().compareTo(message.syncHost, true)) {
					tabContainer.setTab(message.tabEnum);
				}
			}
		}

		return null;
	}
}

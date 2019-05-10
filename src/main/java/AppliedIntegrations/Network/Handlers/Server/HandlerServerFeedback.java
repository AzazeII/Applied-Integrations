package AppliedIntegrations.Network.Handlers.Server;

import AppliedIntegrations.Container.tile.Server.ContainerServerTerminal;
import AppliedIntegrations.Network.Packets.Server.PacketServerFeedback;
import AppliedIntegrations.tile.Server.TileServerCore;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerServerFeedback implements IMessageHandler<PacketServerFeedback, PacketServerFeedback> {

    public HandlerServerFeedback(){

    }

    @Override
    public PacketServerFeedback onMessage(PacketServerFeedback message, MessageContext ctx) {

        // Get current container
        Container container = Minecraft.getMinecraft().player.openContainer;

        // Check if container instanceof server terminal container
        if (container instanceof ContainerServerTerminal){
            // Cast container
            ContainerServerTerminal containerServerTerminal = (ContainerServerTerminal) container;

            // Update card
            containerServerTerminal.getCard().setTagCompound(message.tag);
        }

        return null;
    }
}

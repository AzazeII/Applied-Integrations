package AppliedIntegrations.Network.Handlers.Server;

import AppliedIntegrations.Network.Packets.Server.PacketRibSync;
import AppliedIntegrations.tile.Server.TileServerRib;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerRibSync implements IMessageHandler<PacketRibSync, PacketRibSync> {

    public HandlerRibSync(){

    }

    @Override
    public PacketRibSync onMessage(PacketRibSync message, MessageContext ctx) {

        Minecraft.getMinecraft().addScheduledTask(() -> {
            // Get rib
            TileServerRib rib = message.rib;

            // Change current activity
            rib.isActive = message.nodeActivity;
        });

        return null;
    }
}

package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketVectorSync;
import AppliedIntegrations.tile.Additions.TileMETurretFoundation;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerVectorSync implements IMessageHandler<PacketVectorSync, PacketVectorSync> {
    @Override
    public PacketVectorSync onMessage(PacketVectorSync message, MessageContext ctx) {

        // Update client sided tile
        Minecraft.getMinecraft().addScheduledTask(() -> {
            TileMETurretFoundation te = (TileMETurretFoundation)Minecraft.getMinecraft().world.getTileEntity(message.tile);
            te.renderingDirection = message.vecA;
        });

        return null;
    }
}

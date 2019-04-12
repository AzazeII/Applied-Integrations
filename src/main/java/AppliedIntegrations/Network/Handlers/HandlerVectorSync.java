package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketVectorSync;
import AppliedIntegrations.Tile.HoleStorageSystem.TileMETurretFoundation;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerVectorSync implements IMessageHandler<PacketVectorSync, PacketVectorSync> {
    @Override
    public PacketVectorSync onMessage(PacketVectorSync message, MessageContext ctx) {

        // Update client sided Tile
        Minecraft.getMinecraft().addScheduledTask(() -> {
            TileMETurretFoundation te = (TileMETurretFoundation)Minecraft.getMinecraft().world.getTileEntity(message.tile);
            te.renderingDirection = message.vecA;
        });

        return null;
    }
}

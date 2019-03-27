package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Network.Packets.PacketSingularitySync;
import AppliedIntegrations.tile.Additions.storage.TileMEPylon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerSingularitySync implements IMessageHandler<PacketSingularitySync, PacketSingularitySync> {

    public HandlerSingularitySync() {

    }

    @Override
    public PacketSingularitySync onMessage(PacketSingularitySync message, MessageContext ctx) {
        // Update client sided tile
        Minecraft.getMinecraft().addScheduledTask(() -> {
            TileMEPylon te = (TileMEPylon)Minecraft.getMinecraft().world.getTileEntity(message.pos);
            te.operatedTile = message.operatedTile;
        });

        return null;
    }
}


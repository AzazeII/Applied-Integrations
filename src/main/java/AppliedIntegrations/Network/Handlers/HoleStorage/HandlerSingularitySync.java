package AppliedIntegrations.Network.Handlers.HoleStorage;

import AppliedIntegrations.Network.Packets.HoleStorage.PacketSingularitySync;
import AppliedIntegrations.tile.HoleStorageSystem.storage.TileMEPylon;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerSingularitySync implements IMessageHandler<PacketSingularitySync, PacketSingularitySync> {

    public HandlerSingularitySync() {

    }

    @Override
    public PacketSingularitySync onMessage(PacketSingularitySync message, MessageContext ctx) {
        // Update client sided tile
        Minecraft.getMinecraft().addScheduledTask(() -> {
            TileMEPylon te = (TileMEPylon)Minecraft.getMinecraft().world.getTileEntity(message.pos);
            te.operatedTile = message.operatedTile;
            te.beamDrain = message.beamState;
            te.shouldDrain = message.shouldDrain;
        });

        return null;
    }
}


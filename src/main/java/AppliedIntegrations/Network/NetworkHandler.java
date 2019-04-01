package AppliedIntegrations.Network;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Network.Handlers.*;
import AppliedIntegrations.Network.Packets.*;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @Author Azazell
 */
public class NetworkHandler {
    private static byte packetId = 0;

    private static final SimpleNetworkWrapper Handler = NetworkRegistry.INSTANCE.newSimpleChannel(AppliedIntegrations.modid);

    public static final void registerClientPackets() {
        Handler.registerMessage(HandlerProgressBar.class, PacketProgressBar.class, packetId++, Side.CLIENT);
        Handler.registerMessage(HandlerBarChange.class, PacketBarChange.class, packetId++, Side.CLIENT);

        Handler.registerMessage(HandlerServerToClient.class, PacketServerToClient.class, packetId++, Side.CLIENT);

        Handler.registerMessage(HandlerMEServer.class, PacketMEServer.class, packetId++, Side.CLIENT);

        Handler.registerMessage(HandlerCoordinateInit.class, PacketCoordinateInit.class, packetId++, Side.CLIENT);

        Handler.registerMessage(HandlerVectorSync.class, PacketVectorSync.class, packetId++, Side.CLIENT);

        Handler.registerMessage(HandlerMassChange.class, PacketMassChange.class, packetId++, Side.CLIENT);

        Handler.registerMessage(HandlerSingularitySync.class, PacketSingularitySync.class, packetId++, Side.CLIENT);
    }

    public static final void registerServerPackets(){
        Handler.registerMessage(HandlerClientToServerFilter.class, PacketClientToServerFilter.class, packetId++, Side.SERVER);
    }

    // send packet info to player
    public static final void sendTo(IMessage message, EntityPlayerMP player) {
        NetworkHandler.Handler.sendTo(message, player);
    }

    public static final void sendToDimension(IMessage message, int dimensionId) {
        NetworkHandler.Handler.sendToDimension(message, dimensionId);
    }

    public static final void sendToAllInRange(IMessage message, NetworkRegistry.TargetPoint range) {
        NetworkHandler.Handler.sendToAllAround(message, range);
    }

    public static final void sendToServer(IMessage message) {
        NetworkHandler.Handler.sendToServer(message);
    }

    public static final void sendToAll(IMessage message){
        NetworkHandler.Handler.sendToAll(message);
    }
}

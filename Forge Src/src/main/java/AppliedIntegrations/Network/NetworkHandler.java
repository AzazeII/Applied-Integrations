package AppliedIntegrations.Network;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Network.Packets.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @Author Azazell
 */
public class NetworkHandler {
    private static byte packetId = 0;

    private static final SimpleNetworkWrapper Handler = NetworkRegistry.INSTANCE.newSimpleChannel(AppliedIntegrations.modid);

    /**
     * Call this during pre-init or loading and register all of your packets (messages) here
     */
    public static final void registerPackets() {
        NetworkHandler.registerMessage(PacketProgressBar.class, Side.CLIENT);
        NetworkHandler.registerMessage(PacketBarChange.class,Side.CLIENT);

        NetworkHandler.registerMessage(PacketClientFilter.class,Side.SERVER);
        NetworkHandler.registerMessage(PacketClientFilter.class,Side.CLIENT);

        NetworkHandler.registerMessage(PacketServerFilter.class,Side.SERVER);
        NetworkHandler.registerMessage(PacketServerFilter.class,Side.CLIENT);

        NetworkHandler.registerMessage(PacketMEServer.class, Side.CLIENT);


    }
    /**
     * Registers a message and message handler
     */
    private static final void registerMessage(Class clazz, Side side) {
        NetworkHandler.Handler.registerMessage(clazz, clazz, packetId++, side);
    }

    // send packet info to player
    public static final void sendTo(IMessage message, EntityPlayerMP player) {
        NetworkHandler.Handler.sendTo(message, player);
    }

    public static final void sendToDimension(IMessage message, int dimensionId) {
        NetworkHandler.Handler.sendToDimension(message, dimensionId);
    }

    public static final void sendToServer(IMessage message) {
        NetworkHandler.Handler.sendToServer(message);
    }
}

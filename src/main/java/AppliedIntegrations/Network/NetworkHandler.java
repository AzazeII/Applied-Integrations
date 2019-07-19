package AppliedIntegrations.Network;
import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Network.Handlers.HandlerCoordinateInit;
import AppliedIntegrations.Network.Handlers.HandlerGuiShift;
import AppliedIntegrations.Network.Handlers.HandlerPriorityChange;
import AppliedIntegrations.Network.Handlers.HoleStorage.HandlerMassChange;
import AppliedIntegrations.Network.Handlers.HoleStorage.HandlerSingularitySync;
import AppliedIntegrations.Network.Handlers.HoleStorage.HandlerVectorSync;
import AppliedIntegrations.Network.Handlers.MultiController.*;
import AppliedIntegrations.Network.Handlers.PartGUI.*;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketMassChange;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketSingularitySync;
import AppliedIntegrations.Network.Packets.HoleStorage.PacketVectorSync;
import AppliedIntegrations.Network.Packets.MultiController.*;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
import AppliedIntegrations.Network.Packets.PacketPriorityChange;
import AppliedIntegrations.Network.Packets.PartGUI.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @Author Azazell
 */
public class NetworkHandler {
	private static final SimpleNetworkWrapper handler = NetworkRegistry.INSTANCE.newSimpleChannel(AppliedIntegrations.modid);

	private static byte packetId = 0;

	public static void registerPackets() {
		// -- Server -> Client -- //
		handler.registerMessage(HandlerProgressBar.class, PacketProgressBar.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerBarChange.class, PacketBarChange.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerFullSync.class, PacketFullSync.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerServerToClientFilter.class, PacketFilterServerToClient.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerCoordinateInit.class, PacketCoordinateInit.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerVectorSync.class, PacketVectorSync.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerMassChange.class, PacketMassChange.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerSingularitySync.class, PacketSingularitySync.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerAccessModeServerToClient.class, PacketAccessModeServerToClient.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerTerminalUpdate.class, PacketTerminalUpdate.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerPriorityChange.class, PacketPriorityChange.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerRibSync.class, PacketRibSync.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerMasterSync.class, PacketMasterSync.class, packetId++, Side.CLIENT);
		handler.registerMessage(HandlerScrollServerToClient.class, PacketScrollServerToClient.class, packetId++, Side.CLIENT);

		// -- Client -> Server -- //
		handler.registerMessage(HandlerClientToServerFilter.class, PacketClientToServerFilter.class, packetId++, Side.SERVER);
		handler.registerMessage(HandlerAccessModeClientToServer.class, PacketAccessModeClientToServer.class, packetId++, Side.SERVER);
		handler.registerMessage(HandlerGuiShift.class, PacketGuiShift.class, packetId++, Side.SERVER);
		handler.registerMessage(HandlerSyncReturn.class, PacketSyncReturn.class, packetId++, Side.SERVER);
		handler.registerMessage(HandlerServerFeedback.class, PacketServerFeedback.class, packetId++, Side.SERVER);
		handler.registerMessage(HandlerContainerWidgetSync.class, PacketContainerWidgetSync.class, packetId++, Side.SERVER);
		handler.registerMessage(HandlerScrollClientToServer.class, PacketScrollClientToServer.class, packetId++, Side.SERVER);
	}

	// send packet info to player
	public static final void sendTo(IMessage message, EntityPlayerMP player) {

		NetworkHandler.handler.sendTo(message, player);
	}

	public static final void sendToDimension(IMessage message, int dimensionId) {

		NetworkHandler.handler.sendToDimension(message, dimensionId);
	}

	public static final void sendToAllInRange(IMessage message, NetworkRegistry.TargetPoint range) {

		NetworkHandler.handler.sendToAllAround(message, range);
	}

	public static final void sendToServer(IMessage message) {

		NetworkHandler.handler.sendToServer(message);
	}

	public static final void sendToAll(IMessage message) {

		NetworkHandler.handler.sendToAll(message);
	}
}

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
	private static final SimpleNetworkWrapper Handler = NetworkRegistry.INSTANCE.newSimpleChannel(AppliedIntegrations.modid);

	private static byte packetId = 0;

	public static final void registerClientPackets() {

		Handler.registerMessage(HandlerProgressBar.class, PacketProgressBar.class, packetId++, Side.CLIENT);
		Handler.registerMessage(HandlerBarChange.class, PacketBarChange.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerFullSync.class, PacketFullSync.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerServerToClient.class, PacketFilterServerToClient.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerCoordinateInit.class, PacketCoordinateInit.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerVectorSync.class, PacketVectorSync.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerMassChange.class, PacketMassChange.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerSingularitySync.class, PacketSingularitySync.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerAccessModeServerToClient.class, PacketAccessModeServerToClient.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerTerminalUpdate.class, PacketTerminalUpdate.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerPriorityChange.class, PacketPriorityChange.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerRibSync.class, PacketRibSync.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerMasterSync.class, PacketMasterSync.class, packetId++, Side.CLIENT);

		Handler.registerMessage(HandlerInventorySync.class, PacketInventorySync.class, packetId++, Side.CLIENT);
	}

	public static final void registerServerPackets() {

		Handler.registerMessage(HandlerClientToServerFilter.class, PacketClientToServerFilter.class, packetId++, Side.SERVER);

		Handler.registerMessage(HandlerAccessModeClientToServer.class, PacketAccessModeClientToServer.class, packetId++, Side.SERVER);

		Handler.registerMessage(HandlerGuiShift.class, PacketGuiShift.class, packetId++, Side.SERVER);

		Handler.registerMessage(HandlerSyncReturn.class, PacketSyncReturn.class, packetId++, Side.SERVER);

		Handler.registerMessage(HandlerServerFeedback.class, PacketServerFeedback.class, packetId++, Side.SERVER);

		Handler.registerMessage(HandlerContainerWidgetSync.class, PacketContainerWidgetSync.class, packetId++, Side.SERVER);
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

	public static final void sendToAll(IMessage message) {

		NetworkHandler.Handler.sendToAll(message);
	}
}

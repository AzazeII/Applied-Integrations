package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Network.Packets.PacketMEServer;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerMEServer implements IMessageHandler<PacketMEServer, PacketMEServer> {

    public HandlerMEServer(){

    }

    @Override
    public PacketMEServer onMessage(PacketMEServer message, MessageContext ctx) {
        Gui g = Minecraft.getMinecraft().currentScreen;
        if(g instanceof ServerPacketTracer){
            ServerPacketTracer SPT = (ServerPacketTracer)g;

            if (message.networkData.dir != AEPartLocation.INTERNAL)
                SPT.addNetwork(message.networkData);
            else
                SPT.setMaster(message.networkData);
        }
        return null;
    }
}

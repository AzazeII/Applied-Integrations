package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Gui.ServerGUI.NetworkData;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Network.AIPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.common.util.ForgeDirection;

public class PacketMEServer extends AIPacket<PacketMEServer> {

    public PacketMEServer(){}

    public PacketMEServer(NetworkData networkData) {
        Gui g = Minecraft.getMinecraft().currentScreen;
        if(g instanceof ServerPacketTracer){
            ServerPacketTracer SPT = (ServerPacketTracer)g;
            if(networkData.dir != ForgeDirection.UNKNOWN)
                SPT.addNetwork(networkData);
            else
                SPT.setMaster(networkData);
        }
    }

    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

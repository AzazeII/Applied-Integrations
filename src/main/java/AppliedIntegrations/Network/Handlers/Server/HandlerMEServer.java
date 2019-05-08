package AppliedIntegrations.Network.Handlers.Server;

import AppliedIntegrations.Gui.ServerGUI.GuiServerTerminal;
import AppliedIntegrations.Network.Packets.Server.PacketMEServer;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerMEServer implements IMessageHandler<PacketMEServer, PacketMEServer> {

    public HandlerMEServer(){

    }

    @Override
    public PacketMEServer onMessage(PacketMEServer message, MessageContext ctx) {
        Gui g = Minecraft.getMinecraft().currentScreen;
        if(g instanceof GuiServerTerminal){
            GuiServerTerminal SPT = (GuiServerTerminal)g;

            // If direction isn't internal, then just add network
            /*if (message.networkData.dir != AEPartLocation.INTERNAL)
                SPT.addNetwork(message.networkData);
            else
                SPT.addMaster(message.networkData);*/
        }
        return null;
    }
}

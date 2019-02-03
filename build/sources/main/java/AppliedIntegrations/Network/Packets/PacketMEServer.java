package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Container.Server.ContainerServerPacketTracer;
import AppliedIntegrations.Gui.ServerGUI.NetworkData;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Utils.AILog;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet needed to send data from any slave of MEServer to me server packet tracer gui.
 */
public class PacketMEServer extends AIPacket<PacketMEServer> {

    public PacketMEServer(){}

    public PacketMEServer(NetworkData networkData, int x, int y, int z, World world) {
        Gui g = Minecraft.getMinecraft().currentScreen;
        if(g instanceof ServerPacketTracer){
            ServerPacketTracer SPT = (ServerPacketTracer)g;

            if (networkData.dir != AEPartLocation.INTERNAL)
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

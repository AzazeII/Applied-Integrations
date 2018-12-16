package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Container.Server.ContainerServerPacketTracer;
import AppliedIntegrations.Gui.ServerGUI.NetworkData;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
/**
 * @Author Azazell
 */
public class PacketMEServer extends AIPacket<PacketMEServer> {

    public PacketMEServer(){}

    public PacketMEServer(NetworkData networkData, int x, int y, int z, World world) {
        Gui g = Minecraft.getMinecraft().currentScreen;
        if(g instanceof ServerPacketTracer){
            ServerPacketTracer SPT = (ServerPacketTracer)g;

            if (networkData.dir != ForgeDirection.UNKNOWN)
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

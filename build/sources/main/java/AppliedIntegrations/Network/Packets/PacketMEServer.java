package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Container.Server.ContainerServerPacketTracer;
import AppliedIntegrations.Gui.ServerGUI.NetworkData;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Utils.AILog;
import appeng.api.util.AEPartLocation;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet needed to send data from any slave of MEServer to me server packet tracer gui.
 */
public class PacketMEServer extends AIPacket {


    public NetworkData networkData;

    public PacketMEServer(){

    }

    public PacketMEServer(NetworkData networkData, int x, int y, int z, World world) {
        super(x,y,z,null,world);
        this.networkData = networkData;
    }
    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}

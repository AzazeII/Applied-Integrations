package AppliedIntegrations.Network.Packets.Server;

import AppliedIntegrations.Gui.ServerGUI.NetworkData;
import AppliedIntegrations.Network.Packets.AIPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

/**
 * @Author Azazell
 * @Side Server -> Client
 * @Usage This packet needed to send data from any slave of MEServer to ME server terminal gui.
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

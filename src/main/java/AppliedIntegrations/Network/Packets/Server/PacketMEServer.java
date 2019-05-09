package AppliedIntegrations.Network.Packets.Server;

import AppliedIntegrations.Gui.ServerGUI.SubGui.NetworkData;
import AppliedIntegrations.Network.Packets.AIPacket;
import appeng.api.util.AEPartLocation;
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
        networkData = new NetworkData(
                buf.readBoolean(),
                AEPartLocation.values()[buf.readByte()],
                buf.readInt()
        );
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(networkData.isServerNetwork);
        buf.writeByte(networkData.dir.ordinal());
        buf.writeInt(networkData.id);
    }
}

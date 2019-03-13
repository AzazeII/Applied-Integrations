package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AILog;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet needed to write feedback from gui to part, send it when your filter in gui is updated
 */
public class PacketClientFilter extends AIPacket{

    public LiquidAIEnergy energy;
    public int index;

    public AIPart clientPart;

    public PacketClientFilter(){

    }

    public PacketClientFilter(int x, int y, int z, EnumFacing side, World w, LiquidAIEnergy energy, int index) {
        super(x, y, z, side, w);
        this.energy = energy;
        this.index = index;

        this.clientPart = Utils.getPartByParams(new BlockPos(x, y, z), side, w);
    }

    // Decode serialized data
    @Override
    public void fromBytes(ByteBuf buf) {
        clientPart = getPart(buf);
        energy = getEnergy(buf);
        index = buf.readInt();
    }

    // Encode data from client to server
    @Override
    public void toBytes(ByteBuf buf) {
        setPart(buf, clientPart);
        setEnergy(energy, buf);
        buf.writeInt(index);
    }
}

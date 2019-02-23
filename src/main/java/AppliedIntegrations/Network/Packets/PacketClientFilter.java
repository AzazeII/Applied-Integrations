package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Parts.IEnergyMachine;
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
public class PacketClientFilter implements IMessage, IMessageHandler<PacketClientFilter, IMessage> {

    public LiquidAIEnergy energy;
    public int index;

    public AIPart serverPart;
    public AIPart clientPart;

    public PacketClientFilter(){}

    public PacketClientFilter(int x, int y, int z, EnumFacing side, World w, LiquidAIEnergy energy, int index) {

        this.energy = energy;
        this.index = index;

        this.clientPart = Utils.getPartByParams(new BlockPos(x, y, z), side, w);
    }

    // Decode serialized data
    @Override
    public void fromBytes(ByteBuf buf) {
        int eIndex = buf.readInt();
        if(eIndex >= 0)
            energy = LiquidAIEnergy.linkedIndexMap.get(buf.readInt());
        else
            energy = null;
        index = buf.readInt();

        serverPart = Utils.getPartByParams(new BlockPos(buf.readInt(), buf.readInt(), buf.readInt()), EnumFacing.getFront(buf.readInt()), DimensionManager.getWorld(buf.readInt()));

        try{
            if(serverPart != null) {
                ((IEnergyMachine)serverPart).updateFilter(energy, index);
            }
        }catch(NullPointerException nullptr){

        }
    }

    // Encode data from client to server
    @Override
    public void toBytes(ByteBuf buf) {
        if(energy != null)
            buf.writeInt(energy.getIndex());
        else
            buf.writeInt(-1);
        buf.writeInt(index);

        buf.writeInt(clientPart.getX());
        buf.writeInt(clientPart.getY());
        buf.writeInt(clientPart.getZ());

        buf.writeInt(clientPart.getSide().ordinal());

        buf.writeInt(clientPart.getHostTile().getWorld().provider.getDimension());

    }

    // Ignored
    @Override
    public IMessage onMessage(PacketClientFilter message, MessageContext ctx) {
        return null;
    }
}

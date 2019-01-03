package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Parts.EnergyStorageBus.PartEnergyStorage;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.Serializable;
import java.util.function.Function;

import static AppliedIntegrations.AppliedIntegrations.AI;
import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;

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

    public PacketClientFilter( int x,int y,int z,ForgeDirection side, World w, LiquidAIEnergy energy, int index) {

        this.energy = energy;
        this.index = index;

        this.clientPart = Utils.getPartByParams(x, y, z, side, w);
    }

    // Decode serialized data
    @Override
    public void fromBytes(ByteBuf buf) {
        energy = LiquidAIEnergy.linkedIndexMap.get(buf.readInt());
        index = buf.readInt();

        serverPart = Utils.getPartByParams(buf.readInt(), buf.readInt(), buf.readInt(), ForgeDirection.getOrientation(buf.readInt()), DimensionManager.getWorld(buf.readInt()));

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
        buf.writeInt(energy.getIndex());
        buf.writeInt(index);

        buf.writeInt(clientPart.getX());
        buf.writeInt(clientPart.getY());
        buf.writeInt(clientPart.getZ());

        buf.writeInt(clientPart.getSide().ordinal());

        buf.writeInt(clientPart.getHostTile().getWorldObj().provider.dimensionId);

    }

    // Ignored
    @Override
    public IMessage onMessage(PacketClientFilter message, MessageContext ctx) {
        return null;
    }
}

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Entities.IAIMultiBlock;
import AppliedIntegrations.Entities.Server.TileServerCore;
import AppliedIntegrations.Gui.ServerGUI.NetworkGui;
import AppliedIntegrations.Gui.ServerGUI.ServerPacketTracer;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Utils.AILog;
import appeng.api.networking.IGrid;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.Vector;

public class PacketMEServer<E> implements IMessage, IMessageHandler<PacketMEServer,IMessage> {

    public TileServerCore tile;

    public PacketMEServer(){}

    public PacketMEServer(TileServerCore master) {
       tile = master;
    }
    public PacketMEServer(int x, int y, int z, Vector<E> vector) {
        AILog.info("PacketMeServer constructed");
        Gui g = Minecraft.getMinecraft().currentScreen;
        if (g instanceof ServerPacketTracer) {
            ServerPacketTracer SPT = (ServerPacketTracer) g;
            if (vector.firstElement() instanceof IGrid) {

            }
        }
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        World world = DimensionManager.getWorld(byteBuf.readInt());

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            if (world == null) {
                world = Minecraft.getMinecraft().theWorld;
            }
        }
        tile = (TileServerCore)world.getTileEntity(byteBuf.readInt(),byteBuf.readInt(),byteBuf.readInt());
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {

        try{
            byteBuf.writeInt(tile.getWorldObj().provider.dimensionId);
        }catch (NullPointerException e){
            byteBuf.writeInt(DimensionManager.getNextFreeDimId());
        }

        byteBuf.writeInt(tile.xCoord);
        byteBuf.writeInt(tile.yCoord);
        byteBuf.writeInt(tile.zCoord);
    }

    @Override
    public IMessage onMessage(PacketMEServer message, MessageContext ctx) {
        AILog.info("PacketMeServer constructed");
        Gui g = Minecraft.getMinecraft().currentScreen;
        if(g instanceof ServerPacketTracer){
            ServerPacketTracer SPT = (ServerPacketTracer)g;
            if(tile != null)
                SPT.ServerMaster = tile;
        }
        return null;
    }
}

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Gui.PartGui;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 *
 * @Usage Send this packet, whenever you want to mark gui as "Gui of THIS machine", ex:
 * you want to send data to PartEnergyStorage gui, then you need to mark gui as gui of that part, to mark gui just send this packet.
 */
public class PacketCoordinateInit extends AIPacket {


    public PacketCoordinateInit(int x, int y, int z, World w){
        super(x,y,z,null,w);
    }

    public PacketCoordinateInit(int x, int y, int z, World w, EnumFacing dir){
        super(x,y,z,dir,w);
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}

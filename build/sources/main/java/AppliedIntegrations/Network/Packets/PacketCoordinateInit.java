package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Gui.PartGui;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Utils.AILog;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static AppliedIntegrations.AppliedIntegrations.getLogicalSide;

/**
 * @Author Azazell
 *
 * @Usage Send this packet, whenever you want to mark gui as "Gui of THIS machine", ex:
 * you want to send data to PartEnergyStorage gui, then you need to mark gui as gui of that part, to mark gui just send this packet.
 */
public class PacketCoordinateInit extends AIPacket {

    public final boolean isOwnerPart;

    public AIPart part;

    public PacketCoordinateInit(){
        isOwnerPart = false;
    }

    public PacketCoordinateInit(int x, int y, int z, World w){
        super(x,y,z,null,w);
        isOwnerPart = false;
    }

    public PacketCoordinateInit(AIPart part){
        super(part.getX(), part.getY(), part.getZ(), part.getSide().getFacing(), part.getHostTile().getWorld());
        isOwnerPart = true;

        this.part = part;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        part = getPart(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        setPart(buf, part);
    }
}

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.TileEnergyInterface;
import appeng.api.util.AEPartLocation;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet is only needed for updating energy bar in Energy interface, when you need to update current energy value, you can send this packet
 */
public class PacketProgressBar extends AIPacket {

    public PartEnergyInterface sender;

    public PacketProgressBar(){

    }

    public PacketProgressBar(PartEnergyInterface sender){
        super(sender.getX(), sender.getY(), sender.getZ(), sender.getSide().getFacing(), sender.getHostTile().getWorld());
        this.sender = sender;
    }

    public PacketProgressBar(TileEnergyInterface sender) {
        super(sender.x(), sender.y(), sender.z(), null, sender.getWorld());

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        sender = (PartEnergyInterface)getPart(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        setPart(buf, sender);
    }
}

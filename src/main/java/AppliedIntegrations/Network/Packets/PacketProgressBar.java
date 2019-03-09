package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
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

    public PacketProgressBar(PartEnergyInterface sender, int x, int y, int z, EnumFacing side, World w){
        this.sender = sender;
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}

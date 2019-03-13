package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Gui.IFilterGUI;
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
 * @Usage This packet needed to update GUI filter, send it on gui launch
 */
public class PacketServerFilter extends AIPacket {

    public LiquidAIEnergy energy;
    public int index;

    public PacketServerFilter(){

    }

    public PacketServerFilter(LiquidAIEnergy energy, int index, int x, int y, int z, EnumFacing s, World w){
        super(x,y,z,s,w);
        this.energy = energy;
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
       this.energy = getEnergy(buf);
       this.index = buf.readInt();
    }


    @Override
    public void toBytes(ByteBuf buf) {
        setEnergy(energy, buf);
        buf.writeInt(index);
    }
}

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet needed only for syncing energy interface with it's gui. Just send this packet, and it will update energy type of gui bar.
 */
public class PacketBarChange extends AIPacket {
    public LiquidAIEnergy energy;

    public PacketBarChange(){

    }

    public PacketBarChange(LiquidAIEnergy energy, int x, int y, int z, EnumFacing side, World w){
        super(x, y, z, side, w);
        this.energy = energy;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.energy = LiquidAIEnergy.linkedIndexMap.get(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.energy.getIndex());
    }
}

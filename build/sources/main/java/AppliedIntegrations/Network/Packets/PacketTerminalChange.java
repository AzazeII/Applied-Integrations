package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.IAEEnergyStack;
import appeng.api.storage.data.IItemList;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @Author Azazell
 * @Usage This packet needed to send all data of ME Energy Terminal to gui.
 */
public class PacketTerminalChange extends AIPacket {
    public PacketTerminalChange(){}

    public NBTTagCompound data;
    public IItemList<IAEEnergyStack> List;

    public PacketTerminalChange(IItemList<IAEEnergyStack> list){
        this.data = new NBTTagCompound();
        this.List = list;
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}

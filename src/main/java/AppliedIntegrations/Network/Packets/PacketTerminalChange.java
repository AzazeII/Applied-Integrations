package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.Gui.GuiEnergyTerminalDuality;
import AppliedIntegrations.Network.AIPacket;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet needed to send all data of ME Energy Terminal to gui.
 */
public class PacketTerminalChange extends AIPacket<PacketTerminalChange> {
    public PacketTerminalChange(){}

    public NBTTagCompound data;
    public IItemList<IAEFluidStack> List;

    public PacketTerminalChange(IItemList<IAEEnergyStack> list){
        this.data = new NBTTagCompound();
        this.List = list;

        Gui gui = Minecraft.getMinecraft().currentScreen;
        if(gui instanceof GuiEnergyTerminalDuality){
            GuiEnergyTerminalDuality GETD = (GuiEnergyTerminalDuality)gui;
            GETD.List = list;
        }
    }


    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

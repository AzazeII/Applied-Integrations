package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Network.AIPacket;
import appeng.util.Platform;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class PacketGuiChange extends AIPacket<PacketGuiChange> {

    public PacketGuiChange(){

    }

    public PacketGuiChange(Gui g, int x,int y,int z, EntityPlayer p){
        p.openGui(AppliedIntegrations.instance,9,p.worldObj,x,y,z);
    }

    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

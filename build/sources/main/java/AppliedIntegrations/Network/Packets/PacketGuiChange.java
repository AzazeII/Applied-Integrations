package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Network.AIPacket;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet changes current player's gui
 */
public class PacketGuiChange extends AIPacket<PacketGuiChange> {

    public PacketGuiChange(){

    }

    public PacketGuiChange(Gui g, int x,int y,int z, EntityPlayer p){
        p.openGui(AppliedIntegrations.instance,9,p.world,x,y,z);
    }

    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

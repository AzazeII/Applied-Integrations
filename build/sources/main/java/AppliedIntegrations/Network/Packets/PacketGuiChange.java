package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.AppliedIntegrations;
import appeng.tile.storage.TileChest;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet changes current player's gui
 */
public class PacketGuiChange extends AIPacket {


    public EntityPlayer p;

    public PacketGuiChange(Gui g, int x, int y, int z, EntityPlayer p){
        super(x,y,z,null,null);
        this.p = p;
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}

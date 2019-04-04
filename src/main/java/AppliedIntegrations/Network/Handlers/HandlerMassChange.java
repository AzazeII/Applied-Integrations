package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.API.ISingularity;
import AppliedIntegrations.Network.Packets.PacketMassChange;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerMassChange implements IMessageHandler<PacketMassChange, PacketMassChange> {

    public HandlerMassChange() {

    }

    @Override
    public PacketMassChange onMessage(PacketMassChange message, MessageContext ctx) {
        // Update client sided tile
        Minecraft.getMinecraft().addScheduledTask(() -> {
            ISingularity te = (ISingularity)Minecraft.getMinecraft().world.getTileEntity(message.pos);
            te.setMassFromServer(message.singularity.getMass());
        });

        return null;
    }
}

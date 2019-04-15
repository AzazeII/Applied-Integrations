package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Gui.AIGuiHandler;
import AppliedIntegrations.Network.Packets.PacketGuiShift;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerGuiShift implements IMessageHandler<PacketGuiShift, PacketGuiShift> {
    @Override
    public PacketGuiShift onMessage(PacketGuiShift message, MessageContext ctx) {

        // Open new gui
        AIGuiHandler.open(
                // Requested gui
                message.gui,

                // Player, who requested gui shift
                message.player,

                // Relative part side to cable connection
                message.part.getSide(),

                // Part position in world
                message.part.getHostTile().getPos());

        return null;
    }
}

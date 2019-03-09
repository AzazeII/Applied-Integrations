package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Gui.PartGui;
import AppliedIntegrations.Network.Packets.PacketCoordinateInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerCoordinateInit implements IMessageHandler<PacketCoordinateInit, PacketCoordinateInit> {

    public HandlerCoordinateInit(){

    }

    @Override
    public PacketCoordinateInit onMessage(PacketCoordinateInit message, MessageContext ctx) {
        Gui g = Minecraft.getMinecraft().currentScreen;
        if (g instanceof PartGui) {
            PartGui partGui = (PartGui) g;

            partGui.setX(message.x);
            partGui.setY(message.y);
            partGui.setZ(message.z);

            partGui.setWorld(message.w);
            partGui.setSide(message.side);
        }
        return null;
    }
}

package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Gui.AIBaseGui;
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
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Gui g = Minecraft.getMinecraft().currentScreen;

            if (g instanceof AIBaseGui) {
                AIBaseGui partGui = (AIBaseGui) g;

                partGui.setSyncHost(message.part);
            }
        });
        return null;
    }
}

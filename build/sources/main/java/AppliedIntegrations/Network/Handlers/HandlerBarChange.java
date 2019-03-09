package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Network.Packets.PacketBarChange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerBarChange implements IMessageHandler<PacketBarChange, PacketBarChange> {

    public HandlerBarChange(){

    }

    @Override
    public PacketBarChange onMessage(PacketBarChange message, MessageContext ctx) {
        Gui gui = Minecraft.getMinecraft().currentScreen;
        if(gui instanceof GuiEnergyInterface){
            GuiEnergyInterface GEI = (GuiEnergyInterface)gui;
            // Check if we are updating correct GUI
            if(GEI.getX() == message.x && GEI.getY() == message.y && GEI.getZ() == message.z && GEI.getSide() ==
                    message.side && GEI.getWorld() == message.w)
                GEI.LinkedMetric = message.energy;
        }
        return null;
    }
}

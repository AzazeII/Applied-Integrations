package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Gui.Part.GuiEnergyTerminalDuality;
import AppliedIntegrations.Network.Packets.PacketTerminalChange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerTerminalChange implements IMessageHandler<PacketTerminalChange, PacketTerminalChange> {

    public HandlerTerminalChange(){

    }

    @Override
    public PacketTerminalChange onMessage(PacketTerminalChange message, MessageContext ctx) {
        Gui gui = Minecraft.getMinecraft().currentScreen;
        if(gui instanceof GuiEnergyTerminalDuality){
            GuiEnergyTerminalDuality GETD = (GuiEnergyTerminalDuality)gui;
            GETD.list = message.List;
        }
        return null;
    }
}

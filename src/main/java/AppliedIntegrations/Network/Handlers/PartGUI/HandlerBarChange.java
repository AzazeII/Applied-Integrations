package AppliedIntegrations.Network.Handlers.PartGUI;

import AppliedIntegrations.Gui.Part.GuiEnergyInterface;
import AppliedIntegrations.Network.Packets.PartGUI.PacketBarChange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerBarChange implements IMessageHandler<PacketBarChange, PacketBarChange> {

    public HandlerBarChange(){

    }

    @Override
    public PacketBarChange onMessage(PacketBarChange message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Gui gui = Minecraft.getMinecraft().currentScreen;
            if (gui instanceof GuiEnergyInterface) {
                GuiEnergyInterface GEI = (GuiEnergyInterface) gui;
                // Check if we are updating correct GUI
                if (GEI.getSyncHost().compareTo(message.host, false))
                    GEI.linkedMetric = message.energy;
            }
        });
        return null;
    }
}

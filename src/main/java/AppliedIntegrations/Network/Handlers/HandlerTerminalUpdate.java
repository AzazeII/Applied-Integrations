package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Gui.Part.GuiEnergyTerminalDuality;
import AppliedIntegrations.Network.Packets.PacketTerminalUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerTerminalUpdate implements IMessageHandler<PacketTerminalUpdate, PacketTerminalUpdate> {

    public HandlerTerminalUpdate(){

    }

    @Override
    public PacketTerminalUpdate onMessage(PacketTerminalUpdate message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Gui gui = Minecraft.getMinecraft().currentScreen;
            if (gui instanceof GuiEnergyTerminalDuality) {
                // Check if we are updating correct GUI
                if ((((GuiEnergyTerminalDuality) gui).getSyncHost().compareTo(message.part, true))) {
                    ((GuiEnergyTerminalDuality) gui).updateList(message.list);
                }
            }
        });

        return null;
    }
}

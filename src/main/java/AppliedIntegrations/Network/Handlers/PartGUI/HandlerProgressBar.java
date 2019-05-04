package AppliedIntegrations.Network.Handlers.PartGUI;

import AppliedIntegrations.Gui.Part.GuiEnergyInterface;
import AppliedIntegrations.Network.Packets.PartGUI.PacketProgressBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerProgressBar implements IMessageHandler<PacketProgressBar, PacketProgressBar> {

    public HandlerProgressBar(){

    }

    @Override
    public PacketProgressBar onMessage(PacketProgressBar message, MessageContext ctx) {

        Minecraft.getMinecraft().addScheduledTask(() -> {
            Gui g = Minecraft.getMinecraft().currentScreen;
            if(g instanceof GuiEnergyInterface){
                GuiEnergyInterface GEI = (GuiEnergyInterface)g;

                // Check if we are updating correct GUI
                if(GEI.getSyncHost().equals(message.sender)) {
                    GEI.storage = (int) message.sender.getEnergyStorage(message.energy, message.energySide).getStored();
                }
            }
        });
        return null;
    }
}

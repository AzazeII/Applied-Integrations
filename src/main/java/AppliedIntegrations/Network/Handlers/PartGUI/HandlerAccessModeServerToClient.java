package AppliedIntegrations.Network.Handlers.PartGUI;

import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.Part.GuiEnergyStoragePart;
import AppliedIntegrations.Network.Packets.PartGUI.PacketAccessModeServerToClient;
import AppliedIntegrations.Network.Packets.PartGUI.PacketAccessModeClientToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 */
public class HandlerAccessModeServerToClient implements IMessageHandler<PacketAccessModeServerToClient, PacketAccessModeClientToServer> {

    @Override
    public PacketAccessModeClientToServer onMessage(PacketAccessModeServerToClient message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Gui gui = Minecraft.getMinecraft().currentScreen;
            if (gui instanceof GuiEnergyStoragePart) {
                // Check if we are updating correct GUI
                if (((AIBaseGui) gui).getSyncHost().compareTo(message.partEnergyStorage, true)) {
                    ((GuiEnergyStoragePart) gui).accessMode.set(message.access);
                }
            }
        });

        return null;
    }
}

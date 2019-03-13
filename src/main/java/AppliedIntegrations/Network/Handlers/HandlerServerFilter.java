package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Gui.PartGui;
import AppliedIntegrations.Network.Packets.PacketServerFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerServerFilter implements IMessageHandler<PacketServerFilter, PacketServerFilter> {

    public HandlerServerFilter(){

    }

    @Override
    public PacketServerFilter onMessage(PacketServerFilter message, MessageContext ctx) {
        Gui gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof IFilterGUI) {
            if (gui instanceof PartGui) {
                PartGui IPG = (PartGui) gui;

                // Check if we are updating correct GUI
                if (IPG.getX() == message.x &&
                    IPG.getY() == message.y &&
                    IPG.getSide() == message.side &&
                    IPG.getWorld() == message.w &&
                    IPG.getZ() == message.z) {
                    ((IFilterGUI) gui).updateEnergy(message.energy, message.index);
                }
            }
        }
        return null;
    }
}

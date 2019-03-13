package AppliedIntegrations.Network.Handlers;

import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Network.Packets.PacketProgressBar;
import AppliedIntegrations.Utils.AILog;
import appeng.api.util.AEPartLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
                if(GEI.getX() == message.x && GEI.getY() == message.y && GEI.getZ() == message.z && GEI.getSide() ==
                        message.side &&
                        GEI.getWorld() == message.w) {
                    GEI.storage = (int) message.sender.getEnergyStorage(message.sender.bar, AEPartLocation.INTERNAL).getStored();
                }
            }
        });
        return null;
    }
}

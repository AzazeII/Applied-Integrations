package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet is only needed for updating energy bar in Energy interface, when you need to update current energy value, you can send this packet
 */
public class PacketProgressBar extends AIPacket<PacketProgressBar> {

    public PartEnergyInterface sender;

    public PacketProgressBar(){
        // AutoMate, added for minecraft base handling
    }

    public PacketProgressBar(PartEnergyInterface sender, int x, int y, int z, EnumFacing side, World w){
        this.sender = sender;

        Gui g = Minecraft.getMinecraft().currentScreen;
        if(g instanceof GuiEnergyInterface){
            GuiEnergyInterface GEI = (GuiEnergyInterface)g;

            // Check if we are updating correct GUI
            if(GEI.getX() == x && GEI.getY() == y && GEI.getZ() == z && GEI.getSide() == side && GEI.getWorld() == w) {
                GEI.storage = (int) sender.getEnergyStorage(sender.bar).getStored();
            }
        }
    }
    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Container.ContainerEnergyInterface;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Parts.EnergyInterface.PartEnergyInterface;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
/**
 * @Author Azazell
 * @Usage This packet is only needed for updating energy bar in Energy interface, when you need to update current energy value, you can send this packet
 */
public class PacketProgressBar extends AIPacket<PacketProgressBar> {

    public PartEnergyInterface sender;

    public PacketProgressBar(){
        // AutoMate, added for minecraft base handling
    }

    public PacketProgressBar(PartEnergyInterface sender,int x,int y,int z,ForgeDirection side, World w){
        this.sender = sender;

        Gui g = Minecraft.getMinecraft().currentScreen;
        if(g instanceof GuiEnergyInterface){
            GuiEnergyInterface GEI = (GuiEnergyInterface)g;

            // Check if we are updating correct GUI
            if(GEI.getX() == x && GEI.getY() == y && GEI.getZ() == z && GEI.getSide() == side && GEI.getWorld() == w) {
                GEI.storage = sender.getEnergyStorage(sender.bar).getEnergyStored();
            }
        }
    }
    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Gui.PartGui;
import AppliedIntegrations.Network.AIPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @Author Azazell
 * @Usage This packet needed to update GUI filter, send it on gui launch
 */
public class PacketServerFilter extends AIPacket<PacketServerFilter> {

    public PacketServerFilter(){}

    public PacketServerFilter(LiquidAIEnergy energy, int index, int x, int y, int z, EnumFacing s, World w){
        Gui gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof IFilterGUI) {
            if (gui instanceof PartGui) {
                PartGui IPG = (PartGui) gui;

                // Check if we are updating correct GUI
                if (IPG.getX() == x && IPG.getY() == y && IPG.getSide() == s && IPG.getWorld() == w && IPG.getZ() == z) {
                    ((IFilterGUI) gui).updateEnergies(energy, index);
                }
            }
        }
    }

    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

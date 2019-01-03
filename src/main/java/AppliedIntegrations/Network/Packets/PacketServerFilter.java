package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Gui.IPartGui;
import AppliedIntegrations.Gui.PartGui;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Utils.AILog;
import appeng.api.config.SecurityPermissions;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @Author Azazell
 * @Usage This packet needed to update GUI filter, send it on gui launch
 */
public class PacketServerFilter extends AIPacket<PacketServerFilter> {

    public PacketServerFilter(){}

    public PacketServerFilter(LiquidAIEnergy energy, int index, int x, int y, int z, ForgeDirection s, World w){
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

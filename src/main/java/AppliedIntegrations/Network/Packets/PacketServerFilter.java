package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Gui.AIBaseGui;
import AppliedIntegrations.Gui.IFilterGUI;
import AppliedIntegrations.Gui.IPartGui;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import static AppliedIntegrations.API.LiquidAIEnergy.RF;
/**
 * @Author Azazell
 */
public class PacketServerFilter extends AIPacket<PacketServerFilter> {
    public LiquidAIEnergy energy;

    public int index;

    public PacketServerFilter(){}

    public PacketServerFilter(LiquidAIEnergy energy, int index, int x, int y, int z, ForgeDirection s, World w){
        this.energy = energy;

        this.index = index;

        Gui gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof IFilterGUI) {
            if(gui instanceof IPartGui){
                IPartGui IPG = (IPartGui)gui;
                // Check if we are updating correct GUI
                if(IPG.getX() == x && IPG.getY() == y && IPG.getSide() == s && IPG.getWorld() == w && IPG.getZ() == z) {
                        ((IFilterGUI) gui).updateEnergies(energy, index);
                }
            }
        }
    }
    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        //Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Handled Server-side filter change"));

        return null;
    }
}

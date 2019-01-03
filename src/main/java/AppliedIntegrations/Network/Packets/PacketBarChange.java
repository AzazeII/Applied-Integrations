package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Gui.GuiEnergyInterface;
import AppliedIntegrations.Network.AIPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @Author Azazell
 * @Usage This packet needed only for syncing energy interface with it's gui. Just send this packet, and it will update energy type of gui bar.
 */
public class PacketBarChange extends AIPacket<PacketBarChange> {
    public NBTTagCompound data;
    public PacketBarChange(){}
    public PacketBarChange(LiquidAIEnergy energy, int x, int y, int z, ForgeDirection side, World w){
        this.data = new NBTTagCompound();
        data.setInteger("bar",energy.getIndex());

        Gui gui = Minecraft.getMinecraft().currentScreen;
        if(gui instanceof GuiEnergyInterface){
            GuiEnergyInterface GEI = (GuiEnergyInterface)gui;
            // Check if we are updating correct GUI
            if(GEI.getX() == x && GEI.getY() == y && GEI.getZ() == z && GEI.getSide() == side && GEI.getWorld() == w)
                GEI.LinkedMetric = energy;
        }

    }


    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

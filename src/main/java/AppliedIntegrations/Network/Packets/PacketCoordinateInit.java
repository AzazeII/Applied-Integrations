package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.Gui.PartGui;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class PacketCoordinateInit extends AIPacket<PacketCoordinateInit> {

    public PacketCoordinateInit(){}

    public PacketCoordinateInit(int x, int y, int z, World w){

    }

    public PacketCoordinateInit(int x, int y, int z, World w, ForgeDirection dir){
        Gui g = Minecraft.getMinecraft().currentScreen;
         if (g instanceof PartGui) {
                PartGui partGui = (PartGui) g;

                partGui.setX(x);
                partGui.setY(y);
                partGui.setZ(z);

                partGui.setWorld(w);
                partGui.setSide(dir);

         }
    }

    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Parts.IEnergyMachine;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @Author Azazell
 */
// Sends data from client to server (gui to part)
public class PacketClientFilter extends AIPacket<PacketClientFilter> {

    public LiquidAIEnergy energy;
    public int index;

    public AIPart part;

    public PacketClientFilter(){}

    // Only neutral point between client, and server
    public PacketClientFilter( int x,int y,int z,ForgeDirection side, World w, LiquidAIEnergy energy, int index) {
        this.energy = energy;
        this.index = index;

        this.part = Utils.getPartByParams(x,y,z,side,w);

        if(part != null) {
            ((IEnergyMachine)part).updateFilter(energy, 0);
        }
    }
    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

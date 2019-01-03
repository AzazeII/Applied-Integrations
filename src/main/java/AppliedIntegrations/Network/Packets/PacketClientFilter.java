package AppliedIntegrations.Network.Packets;

import AppliedIntegrations.API.LiquidAIEnergy;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.API.Utils;
import AppliedIntegrations.Network.AIPacket;
import AppliedIntegrations.Parts.EnergyStorageBus.PartEnergyStorage;
import AppliedIntegrations.Parts.IEnergyMachine;
import AppliedIntegrations.Utils.AILog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.function.Function;

/**
 * @Author Azazell
 * @Usage This packet needed to write feedback from gui to part, send it when your filter in gui is updated
 */
public class PacketClientFilter extends AIPacket<PacketClientFilter> {

    public LiquidAIEnergy energy;
    public int index;

    public AIPart part;

    public PacketClientFilter(){}

    public PacketClientFilter( int x,int y,int z,ForgeDirection side, World w, LiquidAIEnergy energy, int index) {

        this.energy = energy;
        this.index = index;

        this.part = Utils.getPartByParams(x,y,z,side,w);

        AILog.debugObject(this, false);

        if(part != null) {
            ((IEnergyMachine)part).updateFilter(energy, index);
        }
    }
    @Override
    public IMessage HandleMessage(MessageContext ctx) {
        return null;
    }
}

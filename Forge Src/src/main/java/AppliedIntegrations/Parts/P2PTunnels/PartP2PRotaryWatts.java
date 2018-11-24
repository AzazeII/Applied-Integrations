package AppliedIntegrations.Parts.P2PTunnels;

import AppliedIntegrations.API.IEnergyDuality;
import AppliedIntegrations.API.Parts.AIPart;
import AppliedIntegrations.Parts.AIP2PTunnel;
import AppliedIntegrations.Parts.PartEnum;
import Reika.RotaryCraft.API.Interfaces.Transducerable;
import Reika.RotaryCraft.API.Power.AdvancedShaftPowerReceiver;
import appeng.api.config.SecurityPermissions;
import appeng.parts.p2p.PartP2PTunnel;
import cpw.mods.fml.common.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

@Optional.InterfaceList(value = {
@Optional.Interface(iface = "Reika.RotaryCraft.API.Interfaces.Transducerable",modid = "RotaryCraft",striprefs = true),
@Optional.Interface(iface = "Reika.RotaryCraft.API.Power.AdvancedShaftPowerReceiver",modid = "RotaryCraft",striprefs = true)}
)
public class PartP2PRotaryWatts extends AIP2PTunnel implements AdvancedShaftPowerReceiver,Transducerable {


    public PartP2PRotaryWatts() {
        super(PartEnum.P2PTunnelWatts);
    }

    @Override
    public ArrayList<String> getMessages(World world, int i, int i1, int i2, int i3) {
        return null;
    }

    @Override
    public boolean addPower(int i, int i1, long l, ForgeDirection forgeDirection) {
        return false;
    }

    @Override
    public boolean canReadFrom(ForgeDirection forgeDirection) {
        return forgeDirection==this.getSide().getOpposite();
    }

    @Override
    public boolean isReceiving() {
        return true;
    }

    @Override
    public int getMinTorque(int i) {
        return 0;
    }

    @Override
    public int getOmega() {
        return 0;
    }

    @Override
    public int getTorque() {
        return 0;
    }

    @Override
    public long getPower() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getIORenderAlpha() {
        return 0;
    }

    @Override
    public void setIORenderAlpha(int i) {

    }
}

package AppliedIntegrations.Parts.P2PTunnels;

import AppliedIntegrations.API.IEnergyDuality;
import AppliedIntegrations.Parts.AIP2PTunnel;
import AppliedIntegrations.Parts.PartEnum;
import appeng.api.config.SecurityPermissions;
import cpw.mods.fml.common.Optional;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraftforge.common.util.ForgeDirection;

@Optional.InterfaceList(value = {
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true)
})
public class PartP2PJoules extends AIP2PTunnel implements IStrictEnergyAcceptor {
    private double power;

    public PartP2PJoules() {
        super(PartEnum.P2PTunnelJoules);
    }

    @Override
    public double transferEnergyToAcceptor(ForgeDirection side, double amount) {
        return this.power+=amount;
    }

    @Override
    public boolean canReceiveEnergy(ForgeDirection side) {
        return side==this.getSide().getOpposite();
    }

    @Override
    public double getEnergy() {
        return this.power;
    }

    @Override
    public void setEnergy(double energy) {
        this.power=energy;
    }

    @Override
    public double getMaxEnergy() {
        return 10000;
    }
}

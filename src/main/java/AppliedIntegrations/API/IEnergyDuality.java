package AppliedIntegrations.API;


import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergySource;
import ic2.api.energy.tile.IEnergySink;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList(value = { // ()____()
        @Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IKineticSource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IHeatSource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true)}
)
public interface IEnergyDuality extends IStrictEnergyAcceptor, IEnergySink, IEnergySource, IAEPowerStorage {

    @Override
    default double injectAEPower(double amt, Actionable mode) {
        return 0;
    }

    @Override
    default double getAEMaxPower() {
        return 0;
    }

    @Override
    default double getAECurrentPower() {
        return 0;
    }

    @Override
    default boolean isAEPublicPowerStorage() {
        return false;
    }

    @Override
    default AccessRestriction getPowerFlow() {
        return null;
    }

    @Override
    default double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
        return 0;
    }

    /**
     * MekansimApi|Energy
     */
    @Override
    default boolean canReceiveEnergy(EnumFacing side) {
        return true;
    }

    /**
     * IC2 API
     */
    @Override
    default double injectEnergy(EnumFacing enumFacing, double v, double v1) {
        return 0;
    }

    @Override
    default double acceptEnergy(EnumFacing enumFacing, double v, boolean b) {
        return 0;
    }

    @Override
    default int getSinkTier() {
        return 4;
    }
}

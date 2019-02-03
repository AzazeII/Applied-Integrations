package AppliedIntegrations.API;


import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergySource;
import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import ic2.api.energy.tile.IEnergySink;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList(value = { // ()____()
        @Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IKineticSource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IHeatSource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "cofh.redstoneflux.api.EnergyStorage",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyHandler",modid = "CoFHAPI",striprefs = true)}

)
public interface IEnergyDuality extends IEnergyReceiver, IEnergyProvider, IStrictEnergyAcceptor, IEnergySink, IEnergyHandler, IEnergySource, IAEPowerStorage {

}

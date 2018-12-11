package AppliedIntegrations.API;

import Reika.RotaryCraft.API.Interfaces.Transducerable;
import Reika.RotaryCraft.API.Power.AdvancedShaftPowerReceiver;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.integration.IntegrationType;
import appeng.transformer.annotations.Integration;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IHeatSource;
import ic2.api.energy.tile.IKineticSource;
import mekanism.api.energy.IStrictEnergyAcceptor;

import javax.swing.text.html.Option;

@Optional.InterfaceList(value = { // ()____()
        @Optional.Interface(iface = "ic2.api.energy.event.EnergyTileLoadEvent",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IKineticSource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.energy.tile.IHeatSource",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "cofh.api.energy.EnergyStorage",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyHandler",modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "Reika.RotaryCraft.API.Interfaces.Transducerable",modid = "RotaryCraft",striprefs = true),
        @Optional.Interface(iface = "Reika.RotaryCraft.API.Power.AdvancedShaftPowerReceiver",modid = "RotaryCraft",striprefs = true)}

)
public interface IEnergyDuality extends IEnergyReceiver, IStrictEnergyAcceptor, IEnergySink,IEnergyHandler,IEnergySource,AdvancedShaftPowerReceiver, Transducerable,IAEPowerStorage {

}

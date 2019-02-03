package AppliedIntegrations.API;


import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;

public interface AdjacentEnergyReceiver extends IEnergyReceiver {
   EnergyStorage getEnergyStorage();
}

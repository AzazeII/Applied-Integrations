package AppliedIntegrations.API;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.minecraftforge.common.util.ForgeDirection;

public interface AdjacentEnergyReceiver extends IEnergyReceiver {
   EnergyStorage getEnergyStorage();
}

package AppliedIntegrations.API.Storage;

import AppliedIntegrations.API.EnergyStack;
import AppliedIntegrations.API.LiquidAIEnergy;
import appeng.api.storage.data.IAEStack;

public interface IAEEnergyStack extends IAEStack<IAEEnergyStack> {
    LiquidAIEnergy getEnergy();
    EnergyStack getStack();
}

package AppliedIntegrations.API.Storage;

import appeng.api.storage.data.IAEStack;

public interface IAEEnergyStack extends IAEStack<IAEEnergyStack> {
    LiquidAIEnergy getEnergy();
    EnergyStack getStack();
}

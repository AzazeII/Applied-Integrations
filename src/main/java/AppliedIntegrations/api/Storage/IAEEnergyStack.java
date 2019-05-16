package AppliedIntegrations.api.Storage;

import appeng.api.storage.data.IAEStack;

/**
 * @Author Azazell
 */
public interface IAEEnergyStack extends IAEStack<IAEEnergyStack> {
	LiquidAIEnergy getEnergy();

	EnergyStack getStack();
}

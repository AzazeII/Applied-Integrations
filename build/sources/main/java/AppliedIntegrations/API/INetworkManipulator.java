package AppliedIntegrations.API;

import AppliedIntegrations.API.Storage.EnergyStack;
import appeng.api.config.Actionable;

public interface INetworkManipulator {
    int InjectEnergy(EnergyStack resource, Actionable actionable);
    int ExtractEnergy(EnergyStack resource, Actionable actionable);
}

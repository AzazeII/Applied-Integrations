package AppliedIntegrations.API;

import AppliedIntegrations.API.Storage.EnergyStack;
import appeng.api.config.Actionable;

public interface INetworkManipulator {
    /**
     * @param resource
     * 	Resource to be injected
     * @param actionable
     * 	Simulate or modulate?
     * @return
     *  amount injected
     */
    int InjectEnergy(EnergyStack resource, Actionable actionable);

    /**
     * @param resource
     * 	Resource to be extracted
     * @param actionable
     * 	Simulate or modulate?
     * @return
     *  amount extracted
     */
    int ExtractEnergy(EnergyStack resource, Actionable actionable);
}

package AppliedIntegrations.api;


import AppliedIntegrations.api.Storage.EnergyStack;
import appeng.api.config.Actionable;
import appeng.me.GridAccessException;

/**
 * @Author Azazell
 */
public interface INetworkManipulator {
	/**
	 * @param resource   Resource to be injected
	 * @param actionable Simulate or modulate?
	 * @return amount injected
	 */
	int injectEnergy(EnergyStack resource, Actionable actionable) throws GridAccessException;

	/**
	 * @param resource   Resource to be extracted
	 * @param actionable Simulate or modulate?
	 * @return amount extracted
	 */
	int extractEnergy(EnergyStack resource, Actionable actionable) throws GridAccessException;
}

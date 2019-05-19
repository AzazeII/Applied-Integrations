package AppliedIntegrations.api.Botania;


import AppliedIntegrations.api.IEnergyInterface;
import appeng.api.config.Actionable;
import appeng.me.GridAccessException;

/**
 * @Author Azazell
 */
public interface IManaInterface extends IEnergyInterface {
	/**
	 * Extract given mana amount from storage cache of grid
	 * @param resource amount to extract
	 * @param actionable Modulate or Simulate?
	 * @return Amount extracted
	 * @throws GridAccessException If host can't access grid
	 */
	int extractMana(int resource, Actionable actionable) throws GridAccessException;

	/**
	 * Inject given mana amount to storage cache of grid
	 * @param resource amount to inject
	 * @param actionable Modulate or simulate?
	 * @return Amount injected
	 * @throws GridAccessException If host can't access grid
	 */
	int injectMana(int resource, Actionable actionable) throws GridAccessException;

	/**
	 * @return Internal mana storage in host
	 */
	int getManaStored();

	/**
	 * @param i Add mana to current storage
	 */
	void modifyManaStorage(int i);
}

package AppliedIntegrations.api;

import appeng.api.config.Actionable;

/**
 * @Author Azazell
 */
public interface IInterfaceStorageDuality<TYPE extends Number> {
	/**
	 * Increment given energy amount to currently stored amount
	 * @param i Energy value
	 */
	void modifyEnergyStored(int i);

	/**
	 * @return Native number class of this storage
	 */
	Class<TYPE> getTypeClass();

	/**
	 * @return Amount stored in native value
	 */
	TYPE getStored();

	/**
	 * @return Max stored amount
	 */
	TYPE getMaxStored();

	/**
	 * Push energy into this storage
	 * @param value Amount to receive
	 * @param action Simulate or modulate?
	 * @return Amount received
	 */
	TYPE receive(TYPE value, Actionable action);

	/**
	 * Pull energy from this storage
	 * @param value Amount to extract
	 * @param action Simulate or modulate?
	 * @return Amount extracted
	 */
	TYPE extract(TYPE value, Actionable action);

	/**
	 * Convert given value to native
	 * @param val Initial value
	 * @return Native value for this storage
	 */
	TYPE toNativeValue(Number val);
}

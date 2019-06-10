package AppliedIntegrations.api;


/**
 * @Author Azazell
 */
public interface IInterfaceStorageDuality<TYPE extends Number> {
	void modifyEnergyStored(int i);

	Class<TYPE> getTypeClass();

	TYPE getStored();

	TYPE getMaxStored();

	TYPE receive(TYPE value, boolean simulate);

	TYPE extract(TYPE value, boolean simulate);
}

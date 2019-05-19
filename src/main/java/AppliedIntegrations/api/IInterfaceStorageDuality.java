package AppliedIntegrations.api;


/**
 * @Author Azazell
 */
public interface IInterfaceStorageDuality<TYPE> {

	void modifyEnergyStored(int i);

	Class<TYPE> getTypeClass();

	// Number, because it can be easily overridden to any class extends Number
	TYPE getStored();

	TYPE getMaxStored();

	TYPE receive(TYPE value, boolean simulate);

	TYPE extract(TYPE value, boolean simulate);
}

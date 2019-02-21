package AppliedIntegrations.API;

/**
 *  Marking Interface
 */
public interface IInterfaceStorageDuality {

    void modifyEnergyStored(int i);

    double getStored();
    double getMaxStored();

    double receive(double value, boolean simulate);
    double extract(double value, boolean simulate);
}

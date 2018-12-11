package AppliedIntegrations.API;
/**
 * @Author Azazell
 */
public interface IHandlerEnergyStorage {

	boolean isFormatted();

	int totalBytes();

	int totalTypes();

	int usedBytes();

	int usedTypes();

}

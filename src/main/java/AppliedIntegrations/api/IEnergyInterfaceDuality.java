package AppliedIntegrations.api;


import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.util.AEPartLocation;
import appeng.me.GridAccessException;

/**
 * @Author Azazell
 */
public interface IEnergyInterfaceDuality {
	/**
	 * @return max energy transfer limit on this {@param side}
	 */
	double getMaxTransfer(AEPartLocation side);

	/**
	 * @return energy in filter on the {@param side}
	 */
	LiquidAIEnergy getFilteredEnergy(AEPartLocation side);

	/**
	 * @return StorageDuality for {@param energy} on {@param side}. StorageDuality contains all data of specific energy storage
	 */
	IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side);

	/**
	 * Both {@link IEnergyInterfaceDuality#doInjectDualityWork} and {@link IEnergyInterfaceDuality#doExtractDualityWork} must be used in tick
	 * handler of interface which uses duality. These methods are trying to inject/extract energy to/from network from/to energy storages on all sides.
	 */
	void doInjectDualityWork(Actionable mode) throws NullNodeConnectionException, GridAccessException;
	void doExtractDualityWork(Actionable mode) throws NullNodeConnectionException, GridAccessException;
}

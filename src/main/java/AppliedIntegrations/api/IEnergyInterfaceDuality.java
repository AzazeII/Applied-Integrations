package AppliedIntegrations.api;

import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.util.AEPartLocation;

/**
 * @Author Azazell
 */
public interface IEnergyInterfaceDuality {

    double getMaxTransfer(AEPartLocation side);
    LiquidAIEnergy getFilteredEnergy(AEPartLocation side);

    IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side);
    void doInjectDualityWork(Actionable mode) throws NullNodeConnectionException;
    void doExtractDualityWork(Actionable mode) throws NullNodeConnectionException;
}

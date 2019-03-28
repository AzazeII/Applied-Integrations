package AppliedIntegrations.API;

import AppliedIntegrations.API.Capabilities.IInterfaceStorageDuality;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.util.AEPartLocation;

/**
 * @Author Azazell
 */
public interface IInterfaceDuality {

    double getMaxTransfer(AEPartLocation side);
    LiquidAIEnergy getFilteredEnergy(AEPartLocation side);

    IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side);
    void DoInjectDualityWork(Actionable mode) throws NullNodeConnectionException;
    void DoExtractDualityWork(Actionable mode) throws NullNodeConnectionException;
}

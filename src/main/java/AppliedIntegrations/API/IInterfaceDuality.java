package AppliedIntegrations.API;

import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.util.AEPartLocation;
import net.minecraftforge.common.capabilities.Capability;

public interface IInterfaceDuality {

    double getMaxTransfer(AEPartLocation side);
    LiquidAIEnergy getFilteredEnergy(AEPartLocation side);

    IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side);
    void DoInjectDualityWork(Actionable mode) throws NullNodeConnectionException;
    void DoExtractDualityWork(Actionable mode) throws NullNodeConnectionException;
}

package AppliedIntegrations.API;

import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.networking.ticking.TickRateModulation;

public interface IInterfaceDuality {

    public void DoInjectDualityWork(Actionable mode) throws NullNodeConnectionException;
    public void DoExtractDualityWork(Actionable mode) throws NullNodeConnectionException;
}

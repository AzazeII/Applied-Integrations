package AppliedIntegrations.API;

import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;

/**
 * @Author Azazell
 */
public interface IEnergyMonitorable {

public IMEMonitor<IAEFluidStack> getEnergyInventory();
}

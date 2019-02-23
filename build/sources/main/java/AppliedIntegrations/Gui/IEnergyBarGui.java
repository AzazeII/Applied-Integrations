package AppliedIntegrations.Gui;

import AppliedIntegrations.API.Storage.LiquidAIEnergy;

public interface IEnergyBarGui extends IWidgetHost{
    public void UpdateStorage(int storedEnergy, LiquidAIEnergy Switch);
}

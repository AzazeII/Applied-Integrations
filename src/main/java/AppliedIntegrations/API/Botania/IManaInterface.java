package AppliedIntegrations.API.Botania;

import AppliedIntegrations.API.IEnergyInterface;
import appeng.api.config.Actionable;

public interface IManaInterface extends IEnergyInterface {
    int ExtractMana(int resource, Actionable actionable);
    int InjectMana(int resource, Actionable actionable);

    int getManaStored();

    void modifyManaStorage(int i);
}

package AppliedIntegrations.api.Botania;


import AppliedIntegrations.api.IEnergyInterface;
import appeng.api.config.Actionable;
import appeng.me.GridAccessException;

/**
 * @Author Azazell
 */
public interface IManaInterface extends IEnergyInterface {
	int ExtractMana(int resource, Actionable actionable) throws GridAccessException;

	int InjectMana(int resource, Actionable actionable) throws GridAccessException;

	int getManaStored();

	void modifyManaStorage(int i);
}

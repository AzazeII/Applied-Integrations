package AppliedIntegrations.Helpers;
import AppliedIntegrations.api.Botania.IManaInterface;
import AppliedIntegrations.api.IEnergyInterfaceDuality;
import AppliedIntegrations.api.IInterfaceStorageDuality;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.Actionable;
import appeng.api.util.AEPartLocation;
import appeng.me.GridAccessException;

import static appeng.api.config.Actionable.MODULATE;

/**
 * @Author Azazell
 */
public class ManaInterfaceDuality implements IEnergyInterfaceDuality {

	private IManaInterface owner;

	public ManaInterfaceDuality(IManaInterface manaInterface) {
		owner = manaInterface;
	}

	@Override
	public double getMaxTransfer(AEPartLocation side) {
		return 100; // Only 100 max transfer, as mana is rich "material"
	}

	@Override
	public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {

		return null;
	}

	@Override
	public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {

		return null;
	}

	@Override
	public void doInjectDualityWork(Actionable mode) throws GridAccessException {
		int ValuedReceive = (int) Math.min(owner.getManaStored(), getMaxTransfer(null));

		if (owner.injectMana(ValuedReceive, Actionable.SIMULATE) - getMaxTransfer(null) == 0) {
			int injectedAmount = owner.injectMana(ValuedReceive, MODULATE);
			owner.modifyManaStorage(-injectedAmount);
		}
	}

	@Override
	public void doExtractDualityWork(Actionable mode) throws GridAccessException {
		int ValuedExtract = (int) Math.min(owner.getManaStored(), getMaxTransfer(null));
		if (owner.injectMana(ValuedExtract, Actionable.SIMULATE) - getMaxTransfer(null) == 0) {
			int extractedAmount = owner.extractMana(ValuedExtract, MODULATE);
			owner.modifyManaStorage(extractedAmount);
		}
	}
}

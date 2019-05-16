package AppliedIntegrations.api;

import net.darkhax.tesla.api.implementation.BaseTeslaContainer;

/**
 * @Author Azazell
 */
public class TeslaInterfaceStorageDuality extends BaseTeslaContainer implements IInterfaceStorageDuality<Long> {

	public TeslaInterfaceStorageDuality(IEnergyInterface owner, Long capacity, Long maxTransfer) {

	}

	@Override
	public void modifyEnergyStored(int i) {
		super.givePower(i, false);
		if (getStored() > getMaxStored()) {
			takePower(getStored() - getMaxStored(), false);
		}
		if (getStored() < 0) {
			givePower(0 - getStored(), false);
		}
	}

	@Override
	public Class<Long> getTypeClass() {
		return Long.class;
	}

	@Override
	public Long getStored() {
		return super.getStoredPower();
	}

	@Override
	public Long getMaxStored() {
		return super.getCapacity();
	}

	@Override
	public Long receive(Long value, boolean simulate) {
		return super.givePower(value, simulate);
	}

	@Override
	public Long extract(Long value, boolean simulate) {
		return super.takePower(value, simulate);
	}
}

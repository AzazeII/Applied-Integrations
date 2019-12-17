package AppliedIntegrations.api;


import AppliedIntegrations.grid.Implementation.AIEnergy;
import appeng.api.config.Actionable;
import appeng.api.util.AEPartLocation;
import net.darkhax.tesla.api.implementation.BaseTeslaContainer;

/**
 * @Author Azazell
 */
public class TeslaInterfaceStorageDuality extends BaseTeslaContainer implements IInterfaceStorageDuality<Long> {
	private final IEnergyInterface owner;
	private final AEPartLocation side;

	public TeslaInterfaceStorageDuality(IEnergyInterface owner, AEPartLocation side, Long capacity, Long maxTransfer) {
		super(capacity, maxTransfer, maxTransfer);
		this.owner = owner;
		this.side = side;
	}

	@Override
	public void modifyEnergyStored(int i) {
		super.givePower(i, false);
		this.owner.setLastInjectedEnergy(side, AIEnergy.TESLA);
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
	public Long receive(Long value, Actionable action) {
		this.owner.setLastInjectedEnergy(side, AIEnergy.TESLA);
		return super.givePower(value, action == Actionable.SIMULATE);
	}

	@Override
	public Long extract(Long value, Actionable action) {
		return super.takePower(value, action == Actionable.SIMULATE);
	}

	@Override
	public Long toNativeValue(Number val) {
		return val.longValue();
	}
}

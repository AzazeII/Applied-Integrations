package AppliedIntegrations.api;


import appeng.api.config.Actionable;
import appeng.api.util.AEPartLocation;
import ic2.api.energy.prefab.BasicSinkSource;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import net.minecraft.util.EnumFacing;

import static AppliedIntegrations.grid.Implementation.AIEnergy.EU;

/**
 * @Author Azazell
 */
public class InterfaceSinkSource extends BasicSinkSource implements IInterfaceStorageDuality<Double> {
	private final IEnergyInterface owner;
	private final AEPartLocation side;

	public InterfaceSinkSource(IEnergyInterface owner, AEPartLocation side, double capacity, int sinkTier, int sourceTier) {
		super(owner.getHostWorld(), owner.getHostPos(), capacity, sinkTier, sourceTier);
		this.owner = owner;
		this.side = side;
	}

	@Override
	public boolean acceptsEnergyFrom(IEnergyEmitter iEnergyEmitter, EnumFacing enumFacing) {
		return true;
	}

	@Override
	public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
		return true;
	}

	@Override
	public void modifyEnergyStored(int i) {
		this.owner.setLastInjectedEnergy(side, EU);
		if (energyStored + i < getMaxStored()) {
			this.energyStored = i;
		} else {
			this.energyStored = getMaxStored();
		}
	}

	@Override
	public Class<Double> getTypeClass() {
		return Double.class;
	}

	@Override
	public Double getStored() {
		return getEnergyStored();
	}

	@Override
	public Double getMaxStored() {
		return getCapacity();
	}

	@Override
	public Double receive(Double value, Actionable action) {
		this.owner.setLastInjectedEnergy(side, EU);
		return injectEnergy(null, value, 4);
	}

	@Override
	public Double extract(Double value, Actionable action) {
		double storedBefore = getEnergyStored();
		drawEnergy(value);
		double storedAfter = getEnergyStored();

		return storedBefore - storedAfter;
	}

	@Override
	public Double toNativeValue(Number val) {
		return val.doubleValue();
	}
}

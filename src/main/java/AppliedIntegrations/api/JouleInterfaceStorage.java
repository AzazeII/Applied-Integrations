package AppliedIntegrations.api;

import appeng.api.config.Actionable;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyOutputter;
import mekanism.api.energy.IStrictEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "mekanism", striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyOutputter", modid = "mekanism", striprefs = true),
		@Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage", modid = "mekanism", striprefs = true)})
/**
 * @Author Azazell
 */
public class JouleInterfaceStorage implements IInterfaceStorageDuality<Double>, INBTStorage, IStrictEnergyStorage, IStrictEnergyOutputter, IStrictEnergyAcceptor {

	private final double capacity;

	private IEnergyInterface energyInterface;

	private double storage;

	public JouleInterfaceStorage(IEnergyInterface iEnergyInterface, int capacity) {

		this.energyInterface = iEnergyInterface;
		this.capacity = capacity;
	}

	@Override
	public void modifyEnergyStored(int i) {

		if (storage + i < capacity) {
			storage += i;
		}
	}

	@Override
	public Class<Double> getTypeClass() {

		return Double.class;
	}

	@Override
	public Double getStored() {

		return storage;
	}

	@Override
	public Double getMaxStored() {

		return capacity;
	}

	@Override
	public Double receive(Double value, Actionable action) {
		double energyReceived = Math.min(capacity - storage, value);

		if (action == Actionable.MODULATE) {
			storage += energyReceived;
		}

		return energyReceived;
	}

	@Override
	public Double extract(Double value, Actionable action) {
		double energyExtracted = Math.min(storage, value);

		if (action == Actionable.MODULATE) {
			storage -= energyExtracted;
		}

		return energyExtracted;
	}

	@Override
	public Double toNativeValue(Number val) {
		return val.doubleValue();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		storage = tag.getDouble("#Joules");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setDouble("#Joules", storage);
	}

	@Override
	public double acceptEnergy(EnumFacing enumFacing, double v, boolean b) {
		return receive(v, b ? Actionable.SIMULATE : Actionable.MODULATE);
	}

	@Override
	public boolean canReceiveEnergy(EnumFacing enumFacing) {

		return true;
	}

	@Override
	public double pullEnergy(EnumFacing enumFacing, double v, boolean b) {
		return extract(v, b ? Actionable.SIMULATE : Actionable.MODULATE);
	}

	@Override
	public boolean canOutputEnergy(EnumFacing enumFacing) {

		return true;
	}

	@Override
	public double getEnergy() {

		return getStored();
	}

	@Override
	public void setEnergy(double v) {

		storage = Math.min(v, capacity);
	}

	@Override
	public double getMaxEnergy() {

		return getMaxStored();
	}
}

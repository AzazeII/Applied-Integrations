package AppliedIntegrations.api;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

/**
 * @Author Azazell
 * Implementation of IEnergyStorage, for better supporting FE Energy System with energy interface
 */
public class EnergyInterfaceStorage extends EnergyStorage implements IInterfaceStorageDuality<Integer>, InbtStorage {

	private IEnergyInterface energyInterface;

	public EnergyInterfaceStorage(IEnergyInterface iEnergyInterface, int capacity, int maxTransfer) {

		super(capacity, maxTransfer);
		this.energyInterface = iEnergyInterface;
	}

	/**
	 * Implementation of original cofh\api EnergyStorage method
	 *
	 * @param energy
	 */
	public void modifyEnergyStored(int energy) {

		this.energy += energy;

		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
	}

	@Override
	public Class<Integer> getTypeClass() {

		return Integer.class;
	}

	@Override
	public Integer getStored() {

		return getEnergyStored();
	}

	@Override
	public Integer getMaxStored() {

		return getMaxEnergyStored();
	}

	@Override
	public Integer receive(Integer value, boolean simulate) {

		return receiveEnergy((int) value, simulate);
	}

	@Override
	public Integer extract(Integer value, boolean simulate) {

		return extractEnergy((int) value, simulate);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {

		tag.setInteger("#ENERGY_TAG", getEnergyStored());
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {

		this.setEnergyStored(tag.getInteger("#ENERGY_TAG"));
	}

	/**
	 * Implementation of original cofh\api EnergyStorage method
	 *
	 * @param energy
	 */
	public void setEnergyStored(int energy) {

		this.energy = energy;

		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
	}
}

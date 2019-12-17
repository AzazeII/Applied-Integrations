package AppliedIntegrations.api;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import appeng.api.config.Actionable;
import appeng.api.util.AEPartLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

/**
 * @Author Azazell
 * Implementation of IEnergyStorage, for better supporting FE Energy System with energy interface
 */
public class EnergyInterfaceStorage extends EnergyStorage implements IInterfaceStorageDuality<Integer>, INBTStorage {
	private final IEnergyInterface owner;
	private final AEPartLocation side;
	private final LiquidAIEnergy energyType;

	public EnergyInterfaceStorage(IEnergyInterface iEnergyInterface, AEPartLocation side, LiquidAIEnergy energy, int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
		this.owner = iEnergyInterface;
		this.side = side;
		this.energyType = energy;
	}

	/**
	 * Implementation of original cofh|API EnergyStorage method
	 */
	public void modifyEnergyStored(int energy) {
		this.owner.setLastInjectedEnergy(side, energyType);
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
	public Integer receive(Integer value, Actionable action) {
		this.owner.setLastInjectedEnergy(side, energyType);
		return receiveEnergy(value, action == Actionable.SIMULATE);
	}

	@Override
	public Integer extract(Integer value, Actionable action) {
		return extractEnergy(value, action == Actionable.SIMULATE);
	}

	@Override
	public Integer toNativeValue(Number val) {
		return val.intValue();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		this.setEnergyStored(tag.getInteger("#ENERGY_TAG"));
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("#ENERGY_TAG", getEnergyStored());
	}

	/**
	 * Implementation of original cofh\api EnergyStorage method
	 */
	public void setEnergyStored(int energy) {
		this.owner.setLastInjectedEnergy(side, energyType);
		this.energy = energy;

		if (this.energy > capacity) {
			this.energy = capacity;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
	}
}

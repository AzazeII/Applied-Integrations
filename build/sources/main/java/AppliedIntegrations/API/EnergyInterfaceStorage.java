package AppliedIntegrations.API;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

/**
 * Implementation of IEnergyStorage, for better supporting FE Energy System with energy interface
 */
public class EnergyInterfaceStorage extends EnergyStorage implements IInterfaceStorageDuality, InbtStorage {

    private IEnergyInterface energyInterface;
    public EnergyInterfaceStorage(IEnergyInterface iEnergyInterface, int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
        this.energyInterface = iEnergyInterface;
    }

    // TODO: 2019-02-19  Move commented methods to new IEnergyStorage implementation, "InterfaceEnergyStorage"
	/*@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {

		boolean shouldUpdate = false;
			if(this.FilteredEnergy == null || FilteredEnergy != RF || FilteredEnergy != J){
				if(getFacingTile() instanceof IStrictEnergyAcceptor && FilteredEnergy != J){
					int r = this.getEnergyStorage(J).receiveEnergy(maxReceive,simulate);
					return r;
				}else if(getFacingTile() instanceof IEnergyReceiver && FilteredEnergy != RF){
					int r = this.getEnergyStorage(RF).receiveEnergy(maxReceive,simulate);
					return r;
				}
			}
			return 0;
	}
	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return getEnergyStorage(RF).getEnergyStored();
	}
	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return getEnergyStorage(RF).getMaxEnergyStored();
	}*/

    /**
     * Implementation of original cofh\API EnergyStorage method
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

    /**
     * Implementation of original cofh\API EnergyStorage method
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
    public void readFromNBT(NBTTagCompound tag) {
        tag.setInteger("#ENERGY_TAG", getEnergyStored());
    }
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        this.setEnergyStored(tag.getInteger("#ENERGY_TAG"));
    }

    @Override
    public double getStored() {
        return getEnergyStored();
    }

    @Override
    public double getMaxStored() {
        return getMaxEnergyStored();
    }

    @Override
    public double receive(double value, boolean simulate) {
        return receiveEnergy((int)value, simulate);
    }

    @Override
    public double extract(double value, boolean simulate) {
        return extractEnergy((int)value, simulate);
    }
}

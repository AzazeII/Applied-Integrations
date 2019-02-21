package AppliedIntegrations.API;

import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyOutputter;
import mekanism.api.energy.IStrictEnergyStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList(value = {
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyAcceptor", modid = "mekanism", striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyOutputter", modid = "mekanism", striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IStrictEnergyStorage", modid = "mekanism", striprefs = true)
})
public class JouleInterfaceStorage implements IInterfaceStorageDuality, InbtStorage, IStrictEnergyStorage, IStrictEnergyOutputter, IStrictEnergyAcceptor {

    private IEnergyInterface energyInterface;
    private double storage;
    private final double capacity;

    public JouleInterfaceStorage(IEnergyInterface iEnergyInterface, int capacity) {
        this.energyInterface = iEnergyInterface;
        this.capacity = capacity;
    }

    @Override
    public void modifyEnergyStored(int i) {
        if(storage+i < capacity)
            storage+=i;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        tag.setDouble("#Joules", storage);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        storage = tag.getDouble("#Joules");
    }

    @Override
    public double getStored() {
        return storage;
    }

    @Override
    public double getMaxStored() {
        return capacity;
    }

    @Override
    public double receive(double value, boolean simulate) {
        double energyReceived = Math.min(capacity - storage, value);
        if (!simulate)
            storage += energyReceived;
        return energyReceived;
    }

    @Override
    public double extract(double value, boolean simulate) {
        double energyExtracted = Math.min(storage, value);
        if (!simulate)
            storage -= energyExtracted;
        return energyExtracted;
    }

    @Override
    public double acceptEnergy(EnumFacing enumFacing, double v, boolean b) {
        return receive(v, b);
    }

    @Override
    public boolean canReceiveEnergy(EnumFacing enumFacing) {
        return true;
    }

    @Override
    public double pullEnergy(EnumFacing enumFacing, double v, boolean b) {
        return extract(v, b);
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

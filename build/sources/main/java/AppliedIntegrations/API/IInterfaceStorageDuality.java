package AppliedIntegrations.API;

import net.minecraft.nbt.NBTTagCompound;

/**
 *  Marking Interface
 */
public interface IInterfaceStorageDuality {

    void modifyEnergyStored(int i);

    void readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);

    double getStored();
    double getMaxStored();

    double receive(double value, boolean simulate);
    double extract(double value, boolean simulate);
}

package AppliedIntegrations.API;

import net.minecraft.nbt.NBTTagCompound;

/**
 *  Marking Interface
 */
public interface IInterfaceStorageDuality {
    int getEnergyStored();
    int getMaxEnergyStored();

    int receiveEnergy(int amount, boolean b);

    void modifyEnergyStored(int i);

    void readFromNBT(NBTTagCompound tag);

    void writeToNBT(NBTTagCompound tag);
}
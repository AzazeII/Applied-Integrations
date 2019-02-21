package AppliedIntegrations.API;

import net.minecraft.nbt.NBTTagCompound;

public interface InbtStorage {
    void readFromNBT(NBTTagCompound tag);
    void writeToNBT(NBTTagCompound tag);
}

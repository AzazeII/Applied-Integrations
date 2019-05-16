package AppliedIntegrations.api;


import net.minecraft.nbt.NBTTagCompound;

/**
 * @Author Azazell
 */
public interface InbtStorage {
	void readFromNBT(NBTTagCompound tag);

	void writeToNBT(NBTTagCompound tag);
}

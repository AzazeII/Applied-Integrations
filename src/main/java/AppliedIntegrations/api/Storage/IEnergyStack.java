package AppliedIntegrations.api.Storage;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public interface IEnergyStack {
	/**
	 * Creates a copy of this stack and returns it.
	 * @return Copy of the stack.
	 */
	@Nonnull
	IEnergyStack copy();

	/**
	 * @return the energy that is stored.
	 */
	@Nullable
	LiquidAIEnergy getEnergy();

	/**
	 * Sets the energy for the stack.
	 */
	void setEnergy(@Nullable LiquidAIEnergy energy);

	String getEnergyName();

	/**
	 * @return the stack size.
	 */
	long getStackSize();

	/**
	 * Sets the size of the stack.
	 * */
	void setStackSize(long size);

	/**
	 * @return true if the size is not positive.
	 */
	boolean isEmpty();

	/**
	 * Sets the values of this stack to match the passed stack.<br>
	 * If the stack is null, all values are reset.
	 **/
	void setAll(@Nullable IEnergyStack stack);

	/**
	 * Writes this energy stack to the specified NBT tag
	 *
	 * @param data The tag to write to
	 * @return The nbt tag passed in.
	 */
	@Nonnull
	NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data);
}

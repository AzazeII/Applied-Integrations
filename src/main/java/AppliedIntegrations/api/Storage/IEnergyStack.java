package AppliedIntegrations.api.Storage;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public interface IEnergyStack {
	/**
	 * Changes the stack size by the delta amount and returns the new stack size.
	 *
	 * @param delta
	 * @return Adjusted stack size.
	 */
	long adjustStackSize(long delta);

	/**
	 * Creates a copy of this stack and returns it.
	 *
	 * @return Copy of the stack.
	 */
	@Nonnull
	IEnergyStack copy();

	/**
	 * Returns the energy that is stored.
	 *
	 * @return
	 */
	@Nullable
	LiquidAIEnergy getEnergy();

	/**
	 * Sets the energy for the stack.
	 *
	 * @param energy
	 */
	void setEnergy(@Nullable LiquidAIEnergy energy);

	String getEnergyName();

	/**
	 * Gets the display name of the energy for the player.
	 *
	 * @param player
	 * @return
	 */
	@Nonnull
	String getEnergyName(@Nullable EntityPlayer player);

	/**
	 * The chat color associated with this energy.
	 */
	@Nonnull
	String getChatColor();

	/**
	 * Returns the stack size.
	 *
	 * @return
	 */
	long getStackSize();

	/**
	 * Sets the size of the stack.
	 *
	 * @param size
	 */
	void setStackSize(long size);

	/**
	 * Returns true if the stack has a non-null energy set.
	 *
	 * @return
	 */
	boolean hasEnergy();

	/**
	 * Returns true if the size is not positive.
	 *
	 * @return
	 */
	boolean isEmpty();

	/**
	 * Sets this stack to the data in the stream.
	 *
	 * @param stream
	 */
	void readFromStream(@Nonnull ByteBuf stream);

	/**
	 * Sets everything.
	 *
	 * @param energy
	 * @param size
	 */
	void setAll(@Nullable LiquidAIEnergy energy, long size);

	/**
	 * Sets the values of this stack to match the passed stack.<br>
	 * If the stack is null, all values are reset.
	 *
	 * @param stack
	 */
	void setAll(@Nullable IEnergyStack stack);

	/**
	 * Writes this energy stack to the specified NBT tag
	 *
	 * @param data The tag to write to
	 * @return The nbt tag passed in.
	 */
	@Nonnull
	NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data);

	/**
	 * Writes the stack to a bytebuf stream.
	 *
	 * @param stream
	 */
	void writeToStream(@Nonnull ByteBuf stream);

}

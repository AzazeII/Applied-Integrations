package AppliedIntegrations.api.Storage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class EnergyStack implements IEnergyStack {
	public long amount;

	private LiquidAIEnergy energy;

	public EnergyStack(LiquidAIEnergy energy, long amount) {
		this.energy = energy;
		this.amount = amount;
	}

	private EnergyStack(EnergyStack old) {
		this.energy = old.getEnergy();
		this.amount = old.getAmount();
	}

	public long getAmount() {
		return this.amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public EnergyStack() {

	}

	public static EnergyStack readFromNBT(NBTTagCompound tag) {
		if (tag != null && !tag.hasNoTags()) {
			EnergyStack stack = new EnergyStack();
			stack.read(tag);
			return stack.getEnergy() != null && stack.getAmount() > 0 ? stack : null;
		}
		return null;
	}

	public void read(NBTTagCompound tag) {

		this.energy = LiquidAIEnergy.getEnergy(tag.getString("Energy"));
		this.amount = tag.getLong("Amount");
	}

	@Override
	public long adjustStackSize(long delta) {
		return 0;
	}

	public EnergyStack copy() {
		return new EnergyStack(this);
	}

	public LiquidAIEnergy getEnergy() {
		return energy;
	}

	@Override
	public void setEnergy(@Nullable LiquidAIEnergy energy) {
		this.energy = energy;
	}

	@Override
	public String getEnergyName() {
		if (energy != null) {
			return energy.getEnergyName();
		}
		return null;
	}

	@Nonnull
	@Override
	public String getEnergyName(@Nullable EntityPlayer player) {
		return getEnergyName();
	}

	@Nonnull
	@Override
	public String getChatColor() {
		return "red";
	}

	@Override
	public long getStackSize() {
		return amount;
	}

	@Override
	public void setStackSize(long size) {
		amount = size;
	}

	@Override
	public boolean hasEnergy() {
		return amount > 0 && energy != null;
	}

	@Override
	public boolean isEmpty() {
		return amount == 0 || energy == null;
	}

	@Override
	public void readFromStream(@Nonnull ByteBuf stream) {

	}

	@Override
	public void setAll(@Nullable LiquidAIEnergy energy, long size) {

		this.energy = energy;
		this.amount = size;
	}

	@Override
	public void setAll(@Nullable IEnergyStack stack) {

		if (stack == null) {
			energy = null;
			amount = 0;
			return;
		}
		this.energy = stack.getEnergy();
		this.amount = stack.getStackSize();
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {

		return write(data);
	}

	@Override
	public void writeToStream(@Nonnull ByteBuf stream) {

		stream.writeLong(amount);
		stream.writeInt(energy.getIndex());
	}

	public NBTTagCompound write(NBTTagCompound tag) {

		tag.setString("Energy", this.getEnergyTag());
		tag.setLong("Amount", this.getAmount());
		return tag;
	}

	public String getEnergyTag() {

		if (energy != null) {
			return this.energy.getTag();
		}
		return null;
	}
}
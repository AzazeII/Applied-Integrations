package AppliedIntegrations.API;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/**
 * @Author Azazell
 */
public class EnergyStack implements IEnergyStack{
	private String energy;
	private long amount;

	public EnergyStack(LiquidAIEnergy energy, long amount) {
		this(energy != null ? energy.getTag() : "", amount);
	}

	public EnergyStack(String energy, long amount) {
		if (energy == null || energy.isEmpty())
			throw new IllegalArgumentException("Energy cannot be null");
		this.energy = energy;
		this.amount = amount;
	}

	private EnergyStack(EnergyStack old) {
		this.energy = old.getEnergyTag();
		this.amount = old.getAmount();
	}

	public EnergyStack() {

	}

	public static IEnergyStack loadEnergyStackFromNBT(NBTTagCompound compoundTag) {
		return null;
	}

	public String getEnergyTag() {
		return this.energy;
	}

	public LiquidAIEnergy getEnergy() {
		return LiquidAIEnergy.getEnergy(this.getEnergyTag());
	}

	@Nonnull
	@Override
	public String getEnergyName() {
		return null;
	}

	@Nonnull
	@Override
	public String getEnergyName(@Nullable EntityPlayer player) {
		return null;
	}

	@Nonnull
	@Override
	public String getChatColor() {
		return null;
	}

	@Override
	public long getStackSize() {
		return amount;
	}

	@Override
	public boolean hasEnergy() {
		return amount > 0;
	}

	@Override
	public boolean isEmpty() {
		return amount > 0;
	}

	@Override
	public void readFromStream(@Nonnull ByteBuf stream) {

	}

	@Override
	public void setAll(@Nullable LiquidAIEnergy energy, long size) {

	}

	@Override
	public void setAll(@Nullable IEnergyStack stack) { }

	@Override
	public void setEnergy(@Nullable LiquidAIEnergy energy) {

	}

	@Override
	public void setStackSize(long size) {

	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
		return null;
	}

	@Override
	public void writeToStream(@Nonnull ByteBuf stream) {

	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getAmount() {
		return this.amount;
	}

	public NBTTagCompound write(NBTTagCompound tag) {
		tag.setString("Energy", this.getEnergyTag());
		tag.setLong("Amount", this.getAmount());
		return tag;
	}

	public void read(NBTTagCompound tag) {
		this.energy = tag.getString("Energy");
		this.amount = tag.getLong("Amount");
	}

	@Override
	public long adjustStackSize(long delta) {
		return 0;
	}

	public EnergyStack copy() {
		return new EnergyStack(this);
	}

	public static EnergyStack readFromNBT(NBTTagCompound tag) {
		if (tag != null && !tag.hasNoTags()) {
			EnergyStack stack = new EnergyStack();
			stack.read(tag);
			return stack.getEnergy() != null && stack.getAmount() > 0 ? stack : null;
		}
		return null;
	}
}
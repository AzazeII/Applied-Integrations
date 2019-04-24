package AppliedIntegrations.grid;

import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * @Author Azazell
 */
public class AEEnergyStack implements IAEEnergyStack, Comparable<IAEEnergyStack> {
    private LiquidAIEnergy Energy;
    private long stackSize;
    private long countRequestable;
    private boolean isCraftable;
    private int hash;

    private AEEnergyStack(LiquidAIEnergy Energy, long amount) {
        this.Energy = Energy;
        if (this.Energy == null) {
            throw new IllegalArgumentException("Energy is null");
        }
        this.setStackSize(amount);
        this.setCraftable(false);
        this.setCountRequestable(0);
        this.hash = this.Energy.hashCode();
    }

    private AEEnergyStack(AEEnergyStack stack) {
        this.Energy = stack.getEnergy();
        if (this.Energy == null)
            throw new IllegalArgumentException("Energy is null");
        this.setStackSize(stack.getStackSize());
        this.setCraftable(false);
        this.setCountRequestable(0);
        this.hash = stack.hash;
    }

    public static AEEnergyStack fromStack(EnergyStack stack) {
        if (stack == null)
            return null;
        return new AEEnergyStack(stack.getEnergy(), stack.getAmount());
    }

    public static IAEEnergyStack fromNBT(NBTTagCompound t) {
        EnergyStack stack = EnergyStack.readFromNBT(t);
        if (stack == null)
            return null;
        AEEnergyStack ae = AEEnergyStack.fromStack(stack);
        ae.setStackSize(t.getLong("EnergyAmount"));
        ae.setCountRequestable(t.getLong("Req"));
        ae.setCraftable(t.getBoolean("Craft"));
        return new AEEnergyStack(stack.getEnergy(), stack.getAmount());
    }

    public static IAEEnergyStack fromPacket(ByteBuf buf) {
        return AEEnergyStack.fromNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public long getStackSize() {
        return this.stackSize;
    }

    @Override
    public IAEEnergyStack setStackSize(long l) {
        this.stackSize = l;
        return this;
    }

    @Override
    public long getCountRequestable() {
        return this.countRequestable;
    }

    @Override
    public IAEEnergyStack setCountRequestable(long l) {
        this.countRequestable = l;
        return this;
    }

    @Override
    public boolean isCraftable() {
        return this.isCraftable;
    }

    @Override
    public IAEEnergyStack setCraftable(boolean b) {
        this.isCraftable = b;
        return this;
    }

    @Override
    public IAEEnergyStack reset() {
        this.setStackSize(0);
        this.setCountRequestable(0);
        this.setCraftable(false);
        return this;
    }

    @Override
    public boolean isMeaningful() {
        return (this.getEnergy() != null && this.getStackSize() != 0) || this.countRequestable > 0 || this.isCraftable;
    }

    @Override
    public void incStackSize(long l) {
        this.setStackSize(this.getStackSize() + l);
    }

    @Override
    public void decStackSize(long l) {
        this.setStackSize(this.getStackSize() - l);
    }

    @Override
    public void incCountRequestable(long l) {
        this.setCountRequestable(this.getCountRequestable() + l);
    }

    @Override
    public void decCountRequestable(long l) {
        this.setCountRequestable(this.getCountRequestable() - l);
    }

    @Override
    public LiquidAIEnergy getEnergy() {
        return this.Energy;
    }

    @Override
    public EnergyStack getStack() {
        return new EnergyStack(this.getEnergy(), (int) Math.min(Integer.MAX_VALUE, this.stackSize));
    }

    @Override
    public void add(IAEEnergyStack option) {
        if (option == null) return;
        this.incStackSize(option.getStackSize());
        this.setCountRequestable(this.getCountRequestable() + option.getCountRequestable());
        this.setCraftable(this.isCraftable() || option.isCraftable());
    }

    @Override
    public void writeToNBT(NBTTagCompound t) {
        t.setString("Energy", this.getEnergy().getTag());
        t.setByte("Count", (byte) 0);
        t.setLong("Amount", this.getStackSize());
        t.setLong("Req", this.getCountRequestable());
        t.setBoolean("Craft", this.isCraftable());
    }

    @Override
    public void writeToPacket(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public IAEEnergyStack copy() {
        return new AEEnergyStack(this);
    }

    @Override
    public IAEEnergyStack empty() {
        IAEEnergyStack copy = this.copy();
        copy.reset();
        return copy;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public boolean isFluid() {
        return false;
    }

    @Override
    public IStorageChannel<IAEEnergyStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class);
    }

    @Override
    public ItemStack asItemStackRepresentation() {
        return null;
    }

    @Override
    public boolean fuzzyComparison(IAEEnergyStack other, FuzzyMode mode) {
        return this.Energy == other.getEnergy();
    }

    @Override
    public int compareTo(IAEEnergyStack o) {
        int diff = this.hashCode() - o.hashCode();
        return Integer.compare(diff, 0);
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AEEnergyStack) {
            return ((AEEnergyStack) obj).getEnergy().getTag().equalsIgnoreCase(this.getEnergy().getTag());
        }
        if (obj instanceof EnergyStack) {
            return ((EnergyStack) obj).getEnergy().getTag().equalsIgnoreCase(this.getEnergy().getTag());
        }
        return false;
    }
}

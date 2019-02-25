package AppliedIntegrations.grid.Mana;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaChannel;
import appeng.api.AEApi;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.IStorageChannel;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class AEManaStack implements IAEManaStack, Comparable<IAEManaStack> {
    private int stackSize;

    public AEManaStack(int amount) {
        this.setStackSize(amount);
    }

    public AEManaStack(IAEManaStack stack) {
        this.setStackSize(stack.getStackSize());
    }

    public static IAEManaStack fromNBT(NBTTagCompound t) {
        return new AEManaStack(t.getInteger("#Amount"));
    }

    public static IAEManaStack fromPacket(ByteBuf buf) {
        return AEManaStack.fromNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public long getStackSize() {
        return this.stackSize;
    }

    @Override
    public AEManaStack setStackSize(long l) {
        this.stackSize = (int)l;
        return this;
    }

    @Override
    public long getCountRequestable() {
        return 0;
    }

    @Override
    public AEManaStack setCountRequestable(long l) {
        return this;
    }

    @Override
    public boolean isCraftable() {
        return false;
    }

    @Override
    public AEManaStack setCraftable(boolean b) {
        return this;
    }

    @Override
    public AEManaStack reset() {
        this.setStackSize(0);
        this.setCountRequestable(0);
        this.setCraftable(false);
        return this;
    }

    @Override
    public boolean isMeaningful() {
        return (this.getStackSize() != 0);
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
    public void add(IAEManaStack option) {
        if (option == null) return;
        this.incStackSize(option.getStackSize());
        this.setCountRequestable(this.getCountRequestable() + option.getCountRequestable());
        this.setCraftable(this.isCraftable() || option.isCraftable());
    }

    @Override
    public void writeToNBT(NBTTagCompound t) {
        t.setLong("#Amount", this.getStackSize());
    }

    @Override
    public void writeToPacket(ByteBuf buf) {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public IAEManaStack copy() {
        return new AEManaStack(this);
    }

    @Override
    public IAEManaStack empty() {
        IAEManaStack copy = this.copy();
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
    public IStorageChannel<IAEManaStack> getChannel() {
        return AEApi.instance().storage().getStorageChannel(IManaChannel.class);
    }

    @Override
    public ItemStack asItemStackRepresentation() {
        return null;
    }

    @Override
    public boolean fuzzyComparison(IAEManaStack other, FuzzyMode mode) {
        return true;
    }

    @Override
    public int compareTo(IAEManaStack o) {
        int diff = this.hashCode() - o.hashCode();
        return Integer.compare(diff, 0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IAEManaStack;
    }
}
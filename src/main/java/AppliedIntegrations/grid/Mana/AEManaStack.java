package AppliedIntegrations.grid.Mana;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.API.Botania.IManaStorageChannel;
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
public class AEManaStack implements IAEManaStack, Comparable<IAEManaStack> {
    private long stackSize;
    private long countRequestable;
    private boolean isCraftable;
    private int hash;

    public AEManaStack(int amount) {
        this.setStackSize(amount);
        this.setCraftable(false);
        this.setCountRequestable(0);
        this.hash = 0;
    }

    private AEManaStack(AEManaStack stack) {
        this.setStackSize(stack.getStackSize());
        this.setCraftable(false);
        this.setCountRequestable(0);
        this.hash = stack.hash;
    }

    public static IAEManaStack fromNBT(NBTTagCompound t) {
        AEManaStack ae = new AEManaStack(t.getInteger("ManaAmount"));
        ae.setCountRequestable(t.getLong("Req"));

        return ae;
    }

    public static IAEManaStack fromPacket(ByteBuf buf) {
        return AEManaStack.fromNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public long getStackSize() {
        return this.stackSize;
    }

    @Override
    public IAEManaStack setStackSize(long l) {
        this.stackSize = l;
        return this;
    }

    @Override
    public long getCountRequestable() {
        return this.countRequestable;
    }

    @Override
    public IAEManaStack setCountRequestable(long l) {
        this.countRequestable = l;
        return this;
    }

    @Override
    public boolean isCraftable() {
        return this.isCraftable;
    }

    @Override
    public IAEManaStack setCraftable(boolean b) {
        this.isCraftable = b;
        return this;
    }

    @Override
    public IAEManaStack reset() {
        this.setStackSize(0);
        this.setCountRequestable(0);
        this.setCraftable(false);
        return this;
    }

    @Override
    public boolean isMeaningful() {
        return (this.getStackSize() != 0) || this.countRequestable > 0 || this.isCraftable;
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
        t.setByte("Count", (byte) 0);
        t.setInteger("ManaAmount", (int)this.getStackSize());
        t.setLong("Req", this.getCountRequestable());
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
        return AEApi.instance().storage().getStorageChannel(IManaStorageChannel.class);
    }

    @Override
    public ItemStack asItemStackRepresentation() {
        return null;
    }

    @Override
    public boolean fuzzyComparison(IAEManaStack other, FuzzyMode mode) {
        // Always equal by type
        return true;
    }

    @Override
    public int compareTo(IAEManaStack o) {
        // Always equal by type
        return 0;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AEManaStack) {
            return true;
        }
        return false;
    }
}

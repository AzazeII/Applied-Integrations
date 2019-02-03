package AppliedIntegrations.Utils;

import AppliedIntegrations.API.IInventoryHost;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

/**
 * @Author Azazell
 */
public class AIGridNodeInventory implements IInventory {

    public ItemStack[] slots;
    public String customName;
    private int stackLimit;
    private IInventoryHost receiver;

    public AIGridNodeInventory(String _customName, int _size, int _stackLimit) {
        this(_customName, _size, _stackLimit, null);
    }

    public AIGridNodeInventory(String _customName, int _size, int _stackLimit,
                               IInventoryHost _receiver) {
        this.slots = new ItemStack[_size];
        this.customName = _customName;
        this.stackLimit = _stackLimit;
        this.receiver = _receiver;
    }

    @Override
    public ItemStack decrStackSize(int slotId, int amount) {
        if (this.slots[slotId] == null)
            return null;
        ItemStack itemstack;
        if (this.slots[slotId].getCount() <= amount) {
            itemstack = this.slots[slotId];
            this.slots[slotId] = null;
            markDirty();
            return itemstack;
        } else {
            ItemStack temp = this.slots[slotId];
            itemstack = temp.splitStack(amount);
            this.slots[slotId] = temp;
            if (temp.getCount() == 0) {
                this.slots[slotId] = null;
            } else {
                this.slots[slotId] = temp;
            }
            markDirty();
            return itemstack;
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return this.stackLimit;
    }

    @Override
    public int getSizeInventory() {
        return this.slots.length;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.slots[i];
    }

    public ItemStack incrStackSize(int slotId, int amount) {
        ItemStack slot = this.slots[slotId];
        if (slot == null)
            return null;
        int stackLimit = getInventoryStackLimit();
        if (stackLimit > slot.getMaxStackSize())
            stackLimit = slot.getMaxStackSize();
        ItemStack added = slot.copy();
        added.setCount(slot.getCount() + amount > stackLimit ? stackLimit : amount);
        slot.setCount(slot.getCount() + added.getCount());
        return added;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public void markDirty() {
        if (this.receiver != null)
            this.receiver.onInventoryChanged();
    }

    public void readFromNBT(NBTTagList nbtList) {
        if(nbtList == null){
            for(int i = 0; i < slots.length; i++){
                slots[i] = null;
            }
            return;
        }
        for (int i = 0; i < nbtList.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbtList.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < this.slots.length) {
                this.slots[j] = new ItemStack(nbttagcompound);
            }
        }
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemstack) {
        if (itemstack != null && itemstack.getCount() > getInventoryStackLimit()) {
            itemstack.setCount(getInventoryStackLimit());
        }
        this.slots[slotId] = itemstack;

        markDirty();
    }

    public NBTTagList writeToNBT() {
        NBTTagList nbtList = new NBTTagList();

        for (int i = 0; i < this.slots.length; ++i) {
            if (this.slots[i] != null) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                this.slots[i].writeToNBT(nbttagcompound);
                nbtList.appendTag(nbttagcompound);
            }
        }
        return nbtList;
    }

    @Override
    public String getName() {
        return customName;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }
}

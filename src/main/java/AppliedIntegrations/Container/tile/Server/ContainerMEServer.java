package AppliedIntegrations.Container.tile.Server;

import AppliedIntegrations.Tile.Server.TileServerCore;
import appeng.api.AEApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public class ContainerMEServer extends Container
{
    private TileServerCore master;


    public ContainerMEServer(EntityPlayer player, TileServerCore master) {


        this.master = master;

        this.bindPlayerInventory(player.inventory);
        this.addDriveSlots(master.inv);
        master.inv.openInventory(player);

    }

    private void addDriveSlots(IInventory master) {
        int i=0;
        for (int y = 0; y<5; y++){
            for(int x = 0; x<6; x++){
                if(master != null) {
                    this.addSlotToContainer( new SlotMEServer(master, i, 35 + x * 18, y * 18 - 1 ) );
                    i++;
                }
            }
        }
        i=0;
    }


    protected void bindPlayerInventory(IInventory inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                        8 + j * 18, (i * 18 + 149)-47));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, (207)-47));
        }
    }


    @Override
    public boolean canInteractWith(EntityPlayer p) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int i)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (AEApi.instance().registries().cell().isCellHandled(itemstack))
            {
                if (i < 3)
                {
                    if (!mergeItemStack(itemstack1, 3, 38, false))
                    {
                        return null;
                    }
                } else if (!mergeItemStack(itemstack1, 0, 3, false))
                {
                    return null;
                }
                if (itemstack1.getCount() == 0)
                {
                    slot.putStack(null);
                } else
                {
                    slot.onSlotChanged();
                }
            }
        }
        return itemstack;
    }
}

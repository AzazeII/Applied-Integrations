package AppliedIntegrations.Container.tile.Server;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotRestrictive;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.tile.Server.TileServerCore;
import appeng.api.AEApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraft.init.Items.AIR;

/**
 * @Author Azazell
 */
public class ContainerMEServer extends ContainerWithPlayerInventory {
    private static final int DRIVE_SLOT_ROWS = 5;
    private static final int DRIVE_SLOT_COLUMNS = 6;

    private static final int CARD_SLOT_ROWS = 6;

    private TileServerCore master;

    public ContainerMEServer(EntityPlayer player, TileServerCore master) {
        super(player);

        // Update master
        this.master = master;

        // Bind player slots
        super.bindPlayerInventory(player.inventory, 102,160 );

        // Bind drive slots
        this.addDriveSlots(master.inv);

        // Bind card slots
        this.addCardSlots(master.cardInv);
    }

    private void addCardSlots(AIGridNodeInventory cardInv) {
        // Create counter
        int i=0;

        // Iterate for Y
        for (int y = 0; y < CARD_SLOT_ROWS; y++){
            // Check not null
            if(cardInv != null) {
                // Add ME server slot
                this.addSlotToContainer( new SlotRestrictive(cardInv, i, 188, y * 18 - 7  ) {
                    // Override icon getter for this slot
                    @SideOnly(Side.CLIENT)
                    public String getSlotTexture() {
                        return AppliedIntegrations.modid + ":gui/slots/network_card_slot";
                    }
                });

                // Add to counter
                i++;
            }
        }
    }

    private void addDriveSlots(AIGridNodeInventory driveInv) {
        // Create counter
        int i=0;

        // Iterate for Y
        for (int y = 0; y < DRIVE_SLOT_ROWS; y++){
            // Iterate for X
            for(int x = 0; x < DRIVE_SLOT_COLUMNS; x++){
                // Check not null
                if(driveInv != null) {
                    // Add ME server slot
                    this.addSlotToContainer( new SlotMEServer(driveInv, i, 35 + x * 18, y * 18 - 1 ) );

                    // Add to counter
                    i++;
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer p) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int i) {
        // Create item stack null pointer
        ItemStack itemstack = null;

        // Get slot
        Slot slot = inventorySlots.get(i);

        // Check not null
        if (slot != null && slot.getHasStack()) {
            // Create other stack
            ItemStack itemstack1 = slot.getStack();

            // Copy stack
            itemstack = itemstack1.copy();

            // Check if cell in stack is handled
            if (AEApi.instance().registries().cell().isCellHandled(itemstack)) {
                if (i < 3) {
                    if (!mergeItemStack(itemstack1, 3, 38, false)) {
                        return new ItemStack(AIR);
                    }
                } else if (!mergeItemStack(itemstack1, 0, 3, false)) {
                    return new ItemStack(AIR);
                }

                if (itemstack1.getCount() == 0) {
                    slot.putStack(new ItemStack(AIR));
                } else {
                    slot.onSlotChanged();
                }
            }
        }
        return itemstack;
    }
}

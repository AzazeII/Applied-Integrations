package AppliedIntegrations.Container.tile.Server;

import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Container.slot.SlotMEServer;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import AppliedIntegrations.tile.Server.TileServerDrive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerServerDrive extends ContainerWithPlayerInventory {
    private static final int DRIVE_SLOT_ROWS = 3;
    private static final int DRIVE_SLOT_COLUMNS = 2;

    public ContainerServerDrive(EntityPlayer player, TileServerDrive drive) {
        super(player);

        // Bind card slots
        this.addDriveSlots(drive.driveInv);

        // Bind player slots
        super.bindPlayerInventory(player.inventory, 66,124 );
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
                    this.addSlotToContainer( new SlotMEServer(driveInv, i, 72 + x * 18, y * 18 - 6) );

                    // Add to counter
                    i++;
                }
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber ) {
        // Ignored
        return ItemStack.EMPTY;
    }
}

package AppliedIntegrations.Container.tile.Server;

import AppliedIntegrations.Container.ContainerWithPlayerInventory;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerServerDrive extends ContainerWithPlayerInventory {
    private static final int DRIVE_SLOT_ROWS = 3;
    private static final int DRIVE_SLOT_COLUMNS = 2;

    public ContainerServerDrive(EntityPlayer player) {
        super(player);
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
}

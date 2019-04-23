package AppliedIntegrations.Container.part;

import AppliedIntegrations.Container.ContainerWithUpgradeSlots;
import AppliedIntegrations.Parts.AIOPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

import static net.minecraft.init.Items.AIR;

/**
 * @Author Azazell
 */
public class ContainerPartEnergyIOBus extends ContainerWithUpgradeSlots {
    // Number of upgrade slots
    private static int NUMBER_OF_UPGRADE_SLOTS = 4;

    // X of upgrades
    private static int UPGRADE_X_POS = 187;

    // Y of upgrades
    private static int UPGRADE_Y_POS = 8;

    private final AIOPart part;
    public ContainerPartEnergyIOBus(final AIOPart part, final EntityPlayer player ) {
        super(part, player);

        // Set the part
        this.part = part;

        // Add upgrade slots
        this.addUpgradeSlots(part.getUpgradeInventory(), NUMBER_OF_UPGRADE_SLOTS,
                UPGRADE_X_POS, UPGRADE_Y_POS);

        // Bind to the player's inventory
        this.bindPlayerInventory(player.inventory, 102, 160);

        // Register listener
        this.part.addListener(this);
    }

    @Override
    public void onContainerClosed( @Nonnull final EntityPlayer player ) {
        // Pass s. trace to super
        super.onContainerClosed(player);

        // Remove listener
        this.part.removeListener(this);
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }
}

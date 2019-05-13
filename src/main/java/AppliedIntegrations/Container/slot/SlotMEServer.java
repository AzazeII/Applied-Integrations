package AppliedIntegrations.Container.slot;

import AppliedIntegrations.AppliedIntegrations;
import appeng.api.AEApi;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @Author Azazell
 */
public class SlotMEServer extends Slot {
    public SlotMEServer(IInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
    }

    @Override
    public boolean isItemValid( final ItemStack stack ) {
        return AEApi.instance().registries().cell().isCellHandled(stack);
    }

    // Override icon getter for this slot
    @SideOnly(Side.CLIENT)
    public String getSlotTexture() {
        return AppliedIntegrations.modid + ":gui/slots/server_cell_slot";
    }
}

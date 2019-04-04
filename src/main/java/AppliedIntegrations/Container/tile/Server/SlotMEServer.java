package AppliedIntegrations.Container.tile.Server;

import appeng.api.AEApi;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public class SlotMEServer extends Slot {
    public SlotMEServer(IInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
    }
    @Override
    public boolean isItemValid( final ItemStack stack )
    {
        return AEApi.instance().registries().cell().isCellHandled(stack);
    }
}

package AppliedIntegrations.Container.slot;

import AppliedIntegrations.AppliedIntegrations;
import appeng.api.AEApi;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.storage.ICellHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SlotMEServer extends Slot {
    public SlotMEServer(IInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
        this.texture = new ResourceLocation(AppliedIntegrations.modid+":textures/gui/CellTexture.png");
    }
    @Override
    public boolean isItemValid( final ItemStack stack )
    {
        return AEApi.instance().registries().cell().isCellHandled(stack);
    }
}

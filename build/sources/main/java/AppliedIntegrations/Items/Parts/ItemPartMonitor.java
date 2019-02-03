package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.PartEnergyStorageMonitor;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartMonitor extends ItemPartAIBase {
    public ItemPartMonitor(String id) {
        super(id);
    }

    @Nullable
    @Override
    public IPart createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyStorageMonitor();
    }
}

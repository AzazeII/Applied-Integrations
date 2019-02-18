package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyStorageMonitor;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartMonitor extends ItemPartAIBase<PartEnergyStorageMonitor> {
    public ItemPartMonitor(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyStorageMonitor createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyStorageMonitor();
    }
}

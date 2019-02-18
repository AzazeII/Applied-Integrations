package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyExport;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartExport extends ItemPartAIBase<PartEnergyExport> {
    public ItemPartExport(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyExport createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyExport();
    }
}

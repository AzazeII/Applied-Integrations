package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.IO.PartEnergyImport;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartImport extends ItemPartAIBase {

    public ItemPartImport(String id) {
        super(id);
    }

    @Nullable
    @Override
    public IPart createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyImport();
    }
}

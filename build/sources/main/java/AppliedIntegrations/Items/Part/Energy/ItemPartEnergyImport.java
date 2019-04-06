package AppliedIntegrations.Items.Part.Energy;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyImport;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartEnergyImport extends ItemPartAIBase<PartEnergyImport> {
    public ItemPartEnergyImport(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyImport createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyImport();
    }
}

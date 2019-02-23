package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyAnnihilation;
import AppliedIntegrations.Parts.Energy.PartEnergyExport;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartAnnihilationPlate extends ItemPartAIBase<PartEnergyAnnihilation> {
    public ItemPartAnnihilationPlate(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyAnnihilation createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyAnnihilation();
    }
}

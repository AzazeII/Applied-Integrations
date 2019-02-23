package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyFormation;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartFormationPlate extends ItemPartAIBase<PartEnergyFormation> {
    public ItemPartFormationPlate(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyFormation createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyFormation();
    }
}

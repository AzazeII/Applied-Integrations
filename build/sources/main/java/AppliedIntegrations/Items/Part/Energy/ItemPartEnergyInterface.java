package AppliedIntegrations.Items.Part.Energy;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartEnergyInterface extends ItemPartAIBase<PartEnergyInterface> {
    public ItemPartEnergyInterface(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyInterface createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyInterface();
    }
}

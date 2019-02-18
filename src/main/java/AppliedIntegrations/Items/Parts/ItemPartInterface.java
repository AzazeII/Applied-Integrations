package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartInterface extends ItemPartAIBase<PartEnergyInterface> {

    public ItemPartInterface(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyInterface createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyInterface();
    }
}

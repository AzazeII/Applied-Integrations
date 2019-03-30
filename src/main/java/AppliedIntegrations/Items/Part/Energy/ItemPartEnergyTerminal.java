package AppliedIntegrations.Items.Part.Energy;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartEnergyTerminal extends ItemPartAIBase<PartEnergyTerminal> {
    public ItemPartEnergyTerminal(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyTerminal createPartFromItemStack(ItemStack itemStack) {
        return null;
    }
}
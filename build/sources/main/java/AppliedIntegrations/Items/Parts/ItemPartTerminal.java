package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyTerminal;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartTerminal extends ItemPartAIBase<PartEnergyTerminal> {
    public ItemPartTerminal(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyTerminal createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyTerminal();
    }
}

package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.PartEnergyTerminal;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartTerminal extends ItemPartAIBase {
    public ItemPartTerminal(String id) {
        super(id);
    }

    @Nullable
    @Override
    public IPart createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyTerminal();
    }
}

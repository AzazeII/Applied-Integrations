package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyStorage;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartStorage extends ItemPartAIBase<PartEnergyStorage> {

    public ItemPartStorage(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyStorage createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyStorage();
    }
}

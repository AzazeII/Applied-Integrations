package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.EnergyStorageBus.PartEnergyStorage;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartStorage extends ItemPartAIBase {

    public ItemPartStorage(String id) {
        super(id);
    }

    @Nullable
    @Override
    public IPart createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyStorage();
    }
}

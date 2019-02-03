package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.EnergyInterface.PartEnergyInterface;
import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartInterface extends ItemPartAIBase {

    public ItemPartInterface(String id) {
        super(id);
    }

    @Nullable
    @Override
    public IPart createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyInterface();
    }
}

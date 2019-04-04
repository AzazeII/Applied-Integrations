package AppliedIntegrations.Items.Part.Energy;

import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyAnnihilation;
import AppliedIntegrations.Parts.Energy.PartEnergyInterface;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartEnergyAnnihilation extends ItemPartAIBase<PartEnergyAnnihilation> {
    public ItemPartEnergyAnnihilation(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyAnnihilation createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyAnnihilation();
    }
}

package AppliedIntegrations.Items.Part.Mana;

import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Botania.PartManaInterface;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartManaInterface extends ItemPartAIBase<PartManaInterface> implements IBotaniaIntegrated {
    public ItemPartManaInterface(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartManaInterface createPartFromItemStack(ItemStack itemStack) {
        return new PartManaInterface();
    }
}

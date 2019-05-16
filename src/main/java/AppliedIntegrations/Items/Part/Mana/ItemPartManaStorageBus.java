package AppliedIntegrations.Items.Part.Mana;

import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Botania.PartManaStorageBus;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartManaStorageBus extends ItemPartAIBase<PartManaStorageBus> implements IBotaniaIntegrated {
	public ItemPartManaStorageBus(String registry) {
		super(registry);
	}

	@Nullable
	@Override
	public PartManaStorageBus createPartFromItemStack(ItemStack itemStack) {
		return new PartManaStorageBus();
	}
}

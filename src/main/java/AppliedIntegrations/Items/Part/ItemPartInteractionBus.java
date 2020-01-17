package AppliedIntegrations.Items.Part;


import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Interaction.PartInteraction;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartInteractionBus extends ItemPartAIBase<PartInteraction> {
	public ItemPartInteractionBus(String registry) {
		super(registry);
	}

	@Nullable
	@Override
	public PartInteraction createPartFromItemStack(ItemStack is) {
		return new PartInteraction();
	}
}

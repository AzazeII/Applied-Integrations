package AppliedIntegrations.Items.Part;


import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.PartInteractionPlane;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartInteractionPlane extends ItemPartAIBase<PartInteractionPlane> {

	public ItemPartInteractionPlane(String registry) {
		super(registry);
	}

	@Nullable
	@Override
	public PartInteractionPlane createPartFromItemStack(ItemStack is) {
		return new PartInteractionPlane();
	}
}

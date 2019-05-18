package AppliedIntegrations.Items.Part.P2P;


import AppliedIntegrations.Parts.P2P.PartWillP2PTunnel;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartP2PWill extends ItemPartP2PTunnel<PartWillP2PTunnel> {
	public ItemPartP2PWill(String registry) {
		super(registry);
	}

	@Nullable
	@Override
	public PartWillP2PTunnel createPartFromItemStack(ItemStack is) {
		return new PartWillP2PTunnel(is);
	}
}

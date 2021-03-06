package AppliedIntegrations.Items.Part.P2P;


import AppliedIntegrations.Parts.P2P.PartStarlightP2PTunnel;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartP2PStarlight extends ItemPartP2PTunnel<PartStarlightP2PTunnel> {

	public ItemPartP2PStarlight(String registry) {

		super(registry);
	}

	@Nullable
	@Override
	public PartStarlightP2PTunnel createPartFromItemStack(ItemStack is) {
		return new PartStarlightP2PTunnel(is);
	}
}

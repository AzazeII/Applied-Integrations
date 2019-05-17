package AppliedIntegrations.Items.Part.P2P;


import AppliedIntegrations.Parts.P2P.PartManaP2PTunnel;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartP2PMana extends ItemPartP2PTunnel<PartManaP2PTunnel> {
	public ItemPartP2PMana(String manaP2PPartItem) {
		super(manaP2PPartItem);
	}

	@Nullable
	@Override
	public PartManaP2PTunnel createPartFromItemStack(ItemStack is) {
		return new PartManaP2PTunnel(is);
	}
}

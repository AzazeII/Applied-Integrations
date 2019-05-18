package AppliedIntegrations.Items.Part.P2P;


import AppliedIntegrations.Parts.P2P.PartXNetP2PTunnel;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartP2PXnet extends ItemPartP2PTunnel<PartXNetP2PTunnel> {
	public ItemPartP2PXnet(String xnetP2PPartItem) {
		super(xnetP2PPartItem);
	}

	@Nullable
	@Override
	public PartXNetP2PTunnel createPartFromItemStack(ItemStack is) {
		return new PartXNetP2PTunnel(is);
	}
}

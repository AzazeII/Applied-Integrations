package AppliedIntegrations.Items.Part.P2P;


import AppliedIntegrations.Integration.XNet.IXnetIntegrated;
import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Parts.P2P.PartXNetP2PTunnel;
import appeng.api.parts.IPartItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartP2PXnet extends AIItemRegistrable implements IPartItem<PartXNetP2PTunnel>, IXnetIntegrated {
	public ItemPartP2PXnet(String xnetP2PPartItem) {
		super(xnetP2PPartItem);
	}

	@Nullable
	@Override
	public PartXNetP2PTunnel createPartFromItemStack(ItemStack is) {
		return new PartXNetP2PTunnel(is);
	}
}

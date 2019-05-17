package AppliedIntegrations.Items.Part.P2P;


import AppliedIntegrations.Integration.Embers.IEmberIntegrated;
import AppliedIntegrations.Parts.P2P.PartEmberP2PTunnel;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartP2PEmber extends ItemPartP2PTunnel<PartEmberP2PTunnel> implements IEmberIntegrated {
	public ItemPartP2PEmber(String name) {
		super(name);
	}

	@Nullable
	@Override
	public PartEmberP2PTunnel createPartFromItemStack(ItemStack is) {
		return new PartEmberP2PTunnel(is);
	}
}

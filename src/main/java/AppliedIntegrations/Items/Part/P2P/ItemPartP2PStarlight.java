package AppliedIntegrations.Items.Part.P2P;


import AppliedIntegrations.Integration.AstralSorcery.IAstralIntegrated;
import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.P2P.PartStarlightP2PTunnel;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @Author Azazell
 */
public class ItemPartP2PStarlight extends ItemPartAIBase<PartStarlightP2PTunnel> implements IAstralIntegrated {

	public ItemPartP2PStarlight(String registry) {

		super(registry);
	}

	@Nullable
	@Override
	public PartStarlightP2PTunnel createPartFromItemStack(ItemStack itemStack) {

		return new PartStarlightP2PTunnel();
	}
}

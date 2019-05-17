package AppliedIntegrations.Parts.P2P;


import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.parts.IPartModel;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public class PartXNetP2PTunnel extends AIPartP2PTunnel<PartXNetP2PTunnel> {
	private static final AIP2PModels MODELS = new AIP2PModels(PartModelEnum.P2P_XNET.getFirstModel());

	public PartXNetP2PTunnel(ItemStack is) {
		super(is);
	}

	@Override
	public IPartModel getStaticModels() {
		return MODELS.getModel( this.isPowered(), this.isActive() );
	}
}

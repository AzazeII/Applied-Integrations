package AppliedIntegrations.Parts.P2P;


import AppliedIntegrations.Integration.AstralSorcery.IAstralIntegrated;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.parts.IPartModel;
import net.minecraft.item.ItemStack;

// TODO: 2019-02-17 Integrations with Astral sorcery

/**
 * @Author Azazell
 */
public class PartStarlightP2PTunnel extends AIPartP2PTunnel<PartStarlightP2PTunnel> implements IAstralIntegrated {
	private static final AIP2PModels MODELS = new AIP2PModels(PartModelEnum.P2P_STARLIGHT.getFirstModel());

	public PartStarlightP2PTunnel(ItemStack is) {
		super(is);
	}

	@Override
	public IPartModel getStaticModels() {
		return MODELS.getModel( this.isPowered(), this.isActive() );
	}
}

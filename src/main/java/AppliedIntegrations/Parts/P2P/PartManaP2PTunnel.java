package AppliedIntegrations.Parts.P2P;


import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import AppliedIntegrations.Parts.PartModelEnum;
import appeng.api.parts.IPartModel;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public class PartManaP2PTunnel extends AIPartP2PTunnel<PartManaP2PTunnel> implements IBotaniaIntegrated {
	private static final AIP2PModels MODELS = new AIP2PModels(PartModelEnum.P2P_MANA.getFirstModel());

	public PartManaP2PTunnel(ItemStack is) {
		super(is);
	}

	@Override
	public IPartModel getStaticModels() {
		return MODELS.getModel( this.isPowered(), this.isActive() );
	}
}

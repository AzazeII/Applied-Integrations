package AppliedIntegrations.Parts.P2P;


import AppliedIntegrations.Integration.Embers.IEmberIntegrated;
import appeng.parts.p2p.PartP2PTunnel;
import net.minecraft.item.ItemStack;

// TODO: 2019-02-17 Integration with Embers

/**
 * @Author Azazell
 */
public class PartEmberP2PTunnel extends PartP2PTunnel<PartEmberP2PTunnel> implements IEmberIntegrated {
	public PartEmberP2PTunnel(ItemStack is) {
		super(is);
	}
}

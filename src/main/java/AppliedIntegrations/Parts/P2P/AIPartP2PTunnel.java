package AppliedIntegrations.Parts.P2P;


import appeng.parts.p2p.PartP2PTunnel;
import net.minecraft.item.ItemStack;

/**
 * @Author Azazell
 */
public abstract class AIPartP2PTunnel<T extends AIPartP2PTunnel<T>> extends PartP2PTunnel<T> {
	public AIPartP2PTunnel(ItemStack is) {
		super(is);
	}
}

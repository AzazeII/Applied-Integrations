package AppliedIntegrations.Parts.P2P;

import AppliedIntegrations.Integration.Botania.IBotaniaIntegrated;
import appeng.parts.p2p.PartP2PTunnel;
import net.minecraft.item.ItemStack;

public class PartManaP2PTunnel extends PartP2PTunnel<PartManaP2PTunnel> implements IBotaniaIntegrated {
    public PartManaP2PTunnel(ItemStack is) {
        super(is);
    }
}

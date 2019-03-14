package AppliedIntegrations.Parts.P2P;

import AppliedIntegrations.Integration.AstralSorcery.IAstralIntegrated;
import AppliedIntegrations.Parts.PartEnum;
import AppliedIntegrations.Utils.AIGridNodeInventory;
import appeng.parts.p2p.PartP2PTunnel;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

// TODO: 2019-02-17 Integrations with Astral sorcery
public class PartStarlightP2PTunnel extends AIP2PTunnel<PartStarlightP2PTunnel> implements IAstralIntegrated {
    public PartStarlightP2PTunnel() {
        super(PartEnum.P2PStarlight);
    }


    @Override
    protected AIGridNodeInventory getUpgradeInventory() {
        return null;
    }

    @Override
    public double getIdlePowerUsage() {
        return 0;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public void onEntityCollision(Entity entity) {

    }
}

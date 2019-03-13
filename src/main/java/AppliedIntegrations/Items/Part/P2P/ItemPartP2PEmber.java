package AppliedIntegrations.Items.Part.P2P;

import AppliedIntegrations.Integration.Embers.IEmberIntegrated;
import AppliedIntegrations.Items.AIItemRegistrable;
import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.P2P.PartEmberP2PTunnel;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartP2PEmber extends ItemPartAIBase<PartEmberP2PTunnel> implements IEmberIntegrated {
    public ItemPartP2PEmber(String emberP2PPartItem) {
        super(emberP2PPartItem);
    }

    @Nullable
    @Override
    public PartEmberP2PTunnel createPartFromItemStack(ItemStack itemStack) {
        return new PartEmberP2PTunnel();
    }
}

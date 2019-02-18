package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.Energy.PartEnergyImport;
import appeng.api.parts.IPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPartImport extends ItemPartAIBase<PartEnergyImport> {

    public ItemPartImport(String id) {
        super(id);
    }

    @Nullable
    @Override
    public PartEnergyImport createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyImport();
    }
}

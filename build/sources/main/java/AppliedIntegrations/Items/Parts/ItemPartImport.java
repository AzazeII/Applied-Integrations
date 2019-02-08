package AppliedIntegrations.Items.Parts;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Items.ItemPartAIBase;
import AppliedIntegrations.Parts.IO.PartEnergyImport;
import appeng.api.AEApi;
import appeng.api.parts.IPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;

public class ItemPartImport extends ItemPartAIBase {

    public ItemPartImport(String id) {
        super(id);
    }

    @Nullable
    @Override
    public IPart createPartFromItemStack(ItemStack itemStack) {
        return new PartEnergyImport();
    }

    //@Override
    //public void registerModel() {
        //Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this, 0, new ModelResourceLocation(AppliedIntegrations.modid + ":part/import"));
    //`}
}

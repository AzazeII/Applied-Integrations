package AppliedIntegrations.Items.multiTool;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Blocks.BlocksEnum;
import AppliedIntegrations.Items.ItemEnum;
import appeng.api.implementations.items.IAEWrench;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Optional;

@Optional.InterfaceList(value = { // ()____()
        @Optional.Interface(iface = "ic2.api.energy.item.IElectricItem",modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IEnergizedItem",modid = "Mekanism",striprefs = true),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyContainerItem", modid = "CoFHAPI",striprefs = true),
        @Optional.Interface(iface = "ic2.api.item.ISpecialElectricItem", modid = "IC2",striprefs = true),
        @Optional.Interface(iface = "ic2.api.item.IElectricItemManager", modid = "IC2",striprefs = true),
        @Optional.Interface( iface = "cofh.api.energy.IEnergyContainerItem", modid = "CoFHAPI", striprefs = true),
        @Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraft|Core", striprefs = true)
})

public class toolChaosManipulator extends AEBasePoweredItem implements IAEWrench {
    public toolChaosManipulator() {

        super(10000000D);

        this.setRegistryName("ToolChaos.reg");

        this.setCreativeTab(AppliedIntegrations.AI);

        this.setMaxStackSize(1);
        new OverlayEntropyManipulator(); // Iniciate event bus register
    }


    @Override
    public boolean canWrench(ItemStack itemStack, EntityPlayer entityPlayer, BlockPos blockPos) {
        return true;
    }

    public void registerModel(){
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(this,0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}

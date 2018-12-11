package AppliedIntegrations.Items.multiTool;

import AppliedIntegrations.AppliedIntegrations;
import AppliedIntegrations.Utils.EffectiveSide;
import appeng.api.implementations.items.IAEWrench;
import appeng.api.implementations.items.IMemoryCard;
import appeng.api.implementations.items.MemoryCardMessages;
import appeng.core.localization.PlayerMessages;
import appeng.integration.IntegrationType;
import appeng.items.tools.ToolMemoryCard;
import appeng.core.features.AEFeature;
import appeng.items.tools.ToolMemoryCard;
import appeng.items.tools.ToolNetworkTool;
import appeng.items.tools.powered.ToolColorApplicator;
import appeng.items.tools.powered.ToolEntropyManipulator;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import appeng.items.tools.powered.powersink.AERootPoweredItem;
import appeng.transformer.annotations.Integration;
import appeng.util.Platform;
import cofh.api.energy.EnergyStorage;
import cpw.mods.fml.common.Optional;
import ic2.core.item.tool.ItemToolWrench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.EnumSet;

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

        super(10000000D, com.google.common.base.Optional.<String>absent());

        this.setFeature(EnumSet.of(AEFeature.EntropyManipulator,AEFeature.ColorApplicator,AEFeature.NetworkTool, AEFeature.PoweredTools));

        this.setCreativeTab(AppliedIntegrations.AI);

        this.setMaxStackSize(1);

        this.setTextureName(AppliedIntegrations.modid+":ChaosManipulator");

        new OverlayEntropyManipulator(); // Iniciate event bus register
    }


    @Override
    public boolean canWrench(ItemStack wrench, EntityPlayer player, int x, int y, int z) {
        return true;
    }

}

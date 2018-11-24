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

public class toolChaosManipulator extends AEBasePoweredItem implements IMemoryCard,IAEWrench {
    private toolModes mode = toolModes.networkTool;
    public toolChaosManipulator() {

        super(10000000D, com.google.common.base.Optional.<String>absent());

        this.setFeature(EnumSet.of(AEFeature.EntropyManipulator,AEFeature.ColorApplicator,AEFeature.NetworkTool, AEFeature.PoweredTools));

        this.setCreativeTab(AppliedIntegrations.AI);

        this.setMaxStackSize(1);

        this.setTextureName(AppliedIntegrations.modid+":ChaosManipulator");

        new OverlayEntropyManipulator(); // Iniciate event bus register
    }
    public toolModes getMode(){return this.mode;}
    @Override
    public boolean onItemUse(ItemStack item, EntityPlayer p, World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        switch (mode){
            case entropyManipulator:
                return new ToolEntropyManipulator().onItemUse(item,p,w,x,y,z,side,hitX,hitY,hitZ);
            case memoryCard:
                return new ToolMemoryCard().onItemUse(item,p,w,x,y,z,side,hitX,hitY,hitZ);
            case networkTool:
                return new ToolNetworkTool().onItemUse(item,p,w,x,y,z,side,hitX,hitY,hitZ);
            case colorApplicator:
                return new ToolColorApplicator().onItemUse(item,p,w,x,y,z,side,hitX,hitY,hitZ);
        }
        return true;
    }
    public void nextMode(boolean rev) {
        this.mode = this.mode.getNext(mode,rev);
    }

    @Override
    public boolean canWrench(ItemStack wrench, EntityPlayer player, int x, int y, int z) {
        return this.mode == toolModes.networkTool;
    }

    @Override
    public void setMemoryCardContents(ItemStack is, String SettingsName, NBTTagCompound data) {
        if(this.mode == toolModes.memoryCard) {
            final NBTTagCompound c = Platform.openNbtData(is);
            c.setString("configurationManipulator", SettingsName);
            c.setTag("Data", data);
        }
    }

    @Override
    public String getSettingsName(ItemStack is) {
        if(this.mode == toolModes.memoryCard) {
            return "configurationManipulator";
        }
        return null;
    }

    @Override
    public NBTTagCompound getData(ItemStack is) {
        if(this.mode == toolModes.memoryCard) {
            final NBTTagCompound c = Platform.openNbtData(is);
            NBTTagCompound o = c.getCompoundTag("Data");
            if (o == null) {
                o = new NBTTagCompound();
            }
            return (NBTTagCompound) o.copy();
        }
        return null;
    }

    @Override
    public void notifyUser(EntityPlayer player, MemoryCardMessages msg) {
        if(EffectiveSide.isClientSide())
        {
            return;
        }

        switch( msg )
        {
            case SETTINGS_CLEARED:
                player.addChatMessage( PlayerMessages.SettingCleared.get() );
                break;
            case INVALID_MACHINE:
                player.addChatMessage( PlayerMessages.InvalidMachine.get() );
                break;
            case SETTINGS_LOADED:
                player.addChatMessage( PlayerMessages.LoadedSettings.get() );
                break;
            case SETTINGS_SAVED:
                player.addChatMessage( PlayerMessages.SavedSettings.get() );
                break;
            default:
        }
    }
}

package AppliedIntegrations.API;

import AppliedIntegrations.API.Storage.EnumCapabilityType;
import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Helpers.IntegrationsHelper;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.Utils.AILog;
import appeng.api.AEApi;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;

import appeng.api.parts.IPartItem;
import appeng.items.parts.ItemPart;
import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.IElectricItem;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.ImmutablePair;
import teamroots.embers.api.item.IEmberChargedTool;

import java.util.List;
import java.util.Vector;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.*;

/**
 * @Author Azazell
 */

@Optional.InterfaceList(value = {
        @Optional.Interface(iface = "teamroots.embers.api.item.IEmberChargedTool", modid = "embers", striprefs = true),
        @Optional.Interface(iface = "ic2.api.item.IElectricItem", modid = "ic2", striprefs = true),
        @Optional.Interface(iface = "mekanism.api.energy.IEnergizedItem", modid = "mekanism", striprefs = true),
        @Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = "redstoneflux", striprefs = true)
})
public class Utils {
    public static LiquidAIEnergy getEnergyFromItemStack(ItemStack itemStack) {
        if (itemStack == null)
            return null;

        Item item = itemStack.getItem();

        if(item instanceof IPartItem){
            IPart part = ((IPartItem)item).createPartFromItemStack(itemStack);
            return getEnergyFromPart(part);
        }else if(item instanceof ItemBlock){
            Block blk = ((ItemBlock) item).getBlock();
            if(blk instanceof BlockContainer){
                BlockContainer blockContainer = (BlockContainer)blk;
                if(blockContainer.hasTileEntity()){
                    return getEnergyFromContainer(blockContainer.createTileEntity(null, blockContainer.getDefaultState()));
                }
            }
        }
        return getEnergyFromItem(item);
    }

    public static LiquidAIEnergy getEnergyFromItem(Item item){
        // Check for rfAPI loaded, and item can handle RF
        if(IntegrationsHelper.instance.isLoaded(RF) && item instanceof IEnergyContainerItem){
            return RF;
        // Check for EU Api loaded, and item can handle EU
        }else if(IntegrationsHelper.instance.isLoaded(EU) && item instanceof IElectricItem){
            return EU;
        // Check for joule API loaded, and item can handle J
        }else if(IntegrationsHelper.instance.isLoaded(J) && item instanceof IEnergizedItem){
            return J;
        // Check for Ember API loaded, and item can handle Ember
        }else if(IntegrationsHelper.instance.isLoaded(Ember) && item instanceof IEmberChargedTool){
            return Ember;
        }
        return null;
    }

    /**
     * @param part part to check
     * @return first energy handled by IPart
     */
    public static LiquidAIEnergy getEnergyFromPart(IPart part){
        // Iterate over all energies, to get handled one
        for(LiquidAIEnergy energy : LiquidAIEnergy.energies.values()){
            // Get capability enum type from energy
            if(EnumCapabilityType.fromEnergy(energy) != null){
                // Record type
                EnumCapabilityType type = EnumCapabilityType.fromEnergy(energy);
                // Iterate over
                for(Capability capability : type.getCapabilityWithModCheck()){
                    // Check if part has capability
                    if(part.hasCapability(capability))
                        // return
                        return type.energy;
                }
            }
        }

        return null;
    }

    /**
     * @param tile tile to check
     * @return first energy handled by TileEntity
     */
    public static LiquidAIEnergy getEnergyFromContainer(TileEntity tile) {
        // Iterate over all energies, to get handled one
        for(LiquidAIEnergy energy : LiquidAIEnergy.energies.values()){
            // Get capability enum type from energy
            if(EnumCapabilityType.fromEnergy(energy) != null){
                // Record type
                EnumCapabilityType type = EnumCapabilityType.fromEnergy(energy);
                // Iterate over
                for(Capability capability : type.getCapabilityWithModCheck()){
                    // Check if part has capability
                    if(tile.hasCapability(capability, null))
                        // return
                        return type.energy;
                }
            }
        }

        return null;
    }

    @Deprecated
    public static IAEEnergyStack ConvertToAEFluidStack(final LiquidAIEnergy Energy, final long fluidAmount )
    {
        IAEEnergyStack Stack;
        Stack = AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class).createStack( new FluidStack( Energy, 1 ) );
        Stack.setStackSize( fluidAmount );
        return Stack;
    }

    public static int getEnergyInContainer(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if(itemStack==null)
            return 0;
        if(Loader.isModLoaded("redstoneflux") && item instanceof IEnergyContainerItem){
            return itemStack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
        }else if(Loader.isModLoaded("ic2") && itemStack.getItem() instanceof IElectricItem){
            return 0;
        }else if(Loader.isModLoaded("MekanismAPI|energy") && itemStack.hasCapability(Capabilities.ENERGY_STORAGE_CAPABILITY, null)){
            return (int)((IEnergizedItem) itemStack.getItem()).getEnergy(itemStack);
        }
            return 0;
    }

    public static ImmutablePair<Integer,ItemStack> extractFromContainer(ItemStack itemStack, int amount) {
        Item energyItem = itemStack.getItem();
        if(itemStack==null)
            return null;
        if(Loader.isModLoaded("redstoneflux") && itemStack.getItem() instanceof IEnergyContainerItem){
            return new ImmutablePair<Integer, ItemStack>(
                    itemStack.getCapability(CapabilityEnergy.ENERGY, null).extractEnergy(amount,false),itemStack.copy());
        }else if(Loader.isModLoaded("ic2") && energyItem instanceof IElectricItem){
            return null;
        }else if(Loader.isModLoaded("MekanismAPI|energy") && itemStack.hasCapability(Capabilities.ENERGY_STORAGE_CAPABILITY, null)){
            ((IEnergizedItem) energyItem).setEnergy(itemStack,((IEnergizedItem)energyItem).getEnergy(itemStack)-amount);
            return new ImmutablePair<Integer, ItemStack>(amount,itemStack.copy());
        }
        return null;
    }

    public static int getContainerCapacity(ItemStack itemStack) {
        Item energyItem = itemStack.getItem();
        if(itemStack==null)
            return 0;
        if(Loader.isModLoaded("redstoneflux") && itemStack.getItem() instanceof IEnergyContainerItem){
            return itemStack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
        }else if(Loader.isModLoaded("ic2") && energyItem instanceof IElectricItem){
            return  0;
        }else if(Loader.isModLoaded("MekanismAPI|energy") && itemStack.hasCapability(Capabilities.ENERGY_STORAGE_CAPABILITY, null)){
            return (int)((IEnergizedItem) energyItem).getEnergy(itemStack);
        }
        return 0;
    }

    public static ImmutablePair<Integer, ItemStack> injectInContainer(ItemStack itemStack, int amount) {
        Item energyItem = itemStack.getItem();
        if(itemStack==null)
            return null;
        if(Loader.isModLoaded("redstoneflux") && itemStack.getItem() instanceof IEnergyContainerItem){
            return new ImmutablePair<Integer, ItemStack>((itemStack.getCapability(CapabilityEnergy.ENERGY, null)
                    .receiveEnergy(amount, false)),itemStack.copy());
        }else if(Loader.isModLoaded("ic2") && energyItem instanceof IElectricItem){
            return null;
        }else if(Loader.isModLoaded("MekanismAPI|energy") && itemStack.hasCapability(Capabilities.ENERGY_STORAGE_CAPABILITY, null)){
            ((IEnergizedItem) energyItem).setEnergy(itemStack,((IEnergizedItem)energyItem).getEnergy(itemStack)+amount);
            return new ImmutablePair<Integer, ItemStack>(amount,itemStack.copy());
        }
        return null;
    }

    public static int getContainerMaxCapacity(ItemStack itemStack) {
        Item energyItem = itemStack.getItem();
        if(itemStack==null)
            return 0;
        if(Loader.isModLoaded("redstoneflux") && itemStack.getItem() instanceof IEnergyContainerItem){
            return itemStack.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
        }else if(Loader.isModLoaded("ic2") && energyItem instanceof IElectricItem){
            return  0;
        }else if(Loader.isModLoaded("MekanismAPI|energy") && itemStack.hasCapability(Capabilities.ENERGY_STORAGE_CAPABILITY, null)){
            return (int)((IEnergizedItem) energyItem).getMaxEnergy(itemStack);
        }
        return 0;
    }

    public static AIPart getPartByParams(AppliedCoord coord){
        return getPartByParams(new BlockPos(coord.x,coord.y,coord.z),coord.side,coord.getWorld());
    }
    public static AIPart getPartByParams(BlockPos pos, EnumFacing side, World worldObj) {

        World world = worldObj;

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            if (world == null) {
                world = Minecraft.getMinecraft().world;
            }
        }
        TileEntity entity = world.getTileEntity(pos);

        return (AIPart) (((IPartHost) entity).getPart(side));
    }
}

package AppliedIntegrations.API;

import AppliedIntegrations.API.Storage.IAEEnergyStack;
import AppliedIntegrations.API.Storage.IEnergyTunnel;
import AppliedIntegrations.Parts.AIPart;
import appeng.api.AEApi;
import appeng.api.parts.IPartHost;
import appeng.api.storage.data.IAEFluidStack;

import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyReceiver;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.IElectricItem;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.ImmutablePair;

import static AppliedIntegrations.API.LiquidAIEnergy.EU;
import static AppliedIntegrations.API.LiquidAIEnergy.J;
import static AppliedIntegrations.API.LiquidAIEnergy.RF;

/**
 * @Author Azazell
 */
public class Utils {
    public static LiquidAIEnergy getEnergyFromItemStack(ItemStack itemStack) {
        if (itemStack == null)
            return null;
        Item energyItem = itemStack.getItem();
        if (energyItem instanceof IEnergizedItem) {
            return J;
        }else if (energyItem instanceof IEnergyContainerItem || energyItem instanceof IEnergyReceiver) {
            return RF;
        } else if (energyItem instanceof IElectricItem || energyItem instanceof IEnergySink || energyItem instanceof IEnergySource) {
            return EU;
        }
        return null;
    }

    public static LiquidAIEnergy getEnergyFromContainer(TileEntity tile) {
        if (tile == null)
            return null;
        if (tile instanceof IStrictEnergyAcceptor) {
            return J;
        } else if (tile instanceof IEnergyReceiver) {
            return RF;
        } else if (tile instanceof IEnergySink)
            return EU;
        return null;
    }

    public static IAEEnergyStack ConvertToAEFluidStack(final LiquidAIEnergy Energy, final long fluidAmount )
    {
        IAEEnergyStack Stack;
        Stack = AEApi.instance().storage().getStorageChannel(IEnergyTunnel.class).createStack( new FluidStack( Energy, 1 ) );
        Stack.setStackSize( fluidAmount );
        return Stack;
    }


    public static int getEnergyInContainer(ItemStack container) {
        Item energyItem = container.getItem();
        if(container==null)
            return 0;
        if(energyItem instanceof IEnergyContainerItem){
            return (int)((IEnergyContainerItem) energyItem).getEnergyStored(container);
        }else if(energyItem instanceof IElectricItem){
            return 0;
        }else if(energyItem instanceof IEnergizedItem){
            return (int)((IEnergizedItem) energyItem).getEnergy(container);
        }
            return 0;
    }

    public static ImmutablePair<Integer,ItemStack> extractFromContainer(ItemStack container, int amount) {
        Item energyItem = container.getItem();
        if(container==null)
            return null;
        if(energyItem instanceof IEnergyContainerItem){
            return new ImmutablePair<Integer, ItemStack>(((IEnergyContainerItem) energyItem).extractEnergy(container,amount,false),container.copy());
        }else if(energyItem instanceof IElectricItem){
            return null;
        }else if(energyItem instanceof IEnergizedItem){
            ((IEnergizedItem) energyItem).setEnergy(container,((IEnergizedItem)energyItem).getEnergy(container)-amount);
            return new ImmutablePair<Integer, ItemStack>(amount,container.copy());
        }
        return null;
    }

    public static int getContainerCapacity(ItemStack container) {
        Item energyItem = container.getItem();
        if(container==null)
            return 0;
        if(energyItem instanceof IEnergyContainerItem){
            return  (int)((IEnergyContainerItem) energyItem).getEnergyStored(container);
        }else if(energyItem instanceof IElectricItem){
            return  0;
        }else if(energyItem instanceof IEnergizedItem){
            return (int)((IEnergizedItem) energyItem).getEnergy(container);
        }
        return 0;
    }

    public static ImmutablePair<Integer, ItemStack> injectInContainer(ItemStack container, int amount) {
        Item energyItem = container.getItem();
        if(container==null)
            return null;
        if(energyItem instanceof IEnergyContainerItem){
            return new ImmutablePair<Integer, ItemStack>(((IEnergyContainerItem) energyItem).receiveEnergy(container,amount,false),container.copy());
        }else if(energyItem instanceof IElectricItem){
            return null;
        }else if(energyItem instanceof IEnergizedItem){
            ((IEnergizedItem) energyItem).setEnergy(container,((IEnergizedItem)energyItem).getEnergy(container)+amount);
            return new ImmutablePair<Integer, ItemStack>(amount,container.copy());
        }
        return null;
    }

    public static int getContainerMaxCapacity(ItemStack container) {
        Item energyItem = container.getItem();
        if(container==null)
            return 0;
        if(energyItem instanceof IEnergyContainerItem){
            return  (int)((IEnergyContainerItem) energyItem).getMaxEnergyStored(container);
        }else if(energyItem instanceof IElectricItem){
            return  0;
        }else if(energyItem instanceof IEnergizedItem){
            return (int)((IEnergizedItem) energyItem).getMaxEnergy(container);
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

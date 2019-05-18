package AppliedIntegrations.Helpers.Energy;


import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.api.Botania.IAEManaStack;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.IEnergyStorageChannel;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.EnumCapabilityType;
import AppliedIntegrations.grid.Mana.AEManaStack;
import AppliedIntegrations.tile.AITile;
import appeng.api.AEApi;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartItem;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.IElectricItem;
import mekanism.api.energy.IEnergizedItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;

import static AppliedIntegrations.grid.Implementation.AIEnergy.*;

/**
 * @Author Azazell
 */

@Optional.InterfaceList(value = {@Optional.Interface(iface = "teamroots.embers.api.item.IEmberChargedTool", modid = "embers", striprefs = true), @Optional.Interface(iface = "ic2.api.item.IElectricItem", modid = "ic2", striprefs = true), @Optional.Interface(iface = "mekanism.api.energy.IEnergizedItem", modid = "mekanism", striprefs = true)})
public class Utils {
	public static IAEStack<IAEEnergyStack> getEnergyStackFromItemStack(ItemStack itemStack) {
		// Get energy stack
		EnergyStack stack = new EnergyStack(getEnergyFromItemStack(itemStack), 1);

		// Check not null and meaningful
		if (stack.getEnergy() == null) {
			return null;
		}

		return AEEnergyStack.fromStack(stack);
	}

	public static LiquidAIEnergy getEnergyFromItemStack(ItemStack itemStack) {

		if (itemStack == null) {
			return null;
		}

		Item item = itemStack.getItem();

		if (item instanceof IPartItem) {
			IPart part = ((IPartItem) item).createPartFromItemStack(itemStack);
			return getEnergyFromPart(part);
		} else if (item instanceof ItemBlock) {
			Block blk = ((ItemBlock) item).getBlock();
			if (blk.hasTileEntity(blk.getDefaultState())) {
				return getEnergyFromContainer(blk.createTileEntity(null, blk.getDefaultState()));
			}
		}
		return getEnergyFromItem(item);
	}

	/**
	 * @param part host to check
	 * @return first energy handled by IPart
	 */
	public static LiquidAIEnergy getEnergyFromPart(IPart part) {
		// Iterate over all energies, to get handled one
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			// Get capability enum type from energy
			if (EnumCapabilityType.fromEnergy(energy) != null) {
				// Record type
				EnumCapabilityType type = EnumCapabilityType.fromEnergy(energy);

				// Check not null
				if (type == null) {
					return null;
				}

				// Check has capability
				if (type.getCapabilityWithModCheck() == null) {
					return null;
				}

				// Iterate over
				for (Capability capability : type.getCapabilityWithModCheck()) {
					// Check if host has capability
					if (part.hasCapability(capability))
					// return
					{
						return type.energy;
					}
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
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			// Get capability enum type from energy
			if (EnumCapabilityType.fromEnergy(energy) != null) {
				// Record type
				EnumCapabilityType type = EnumCapabilityType.fromEnergy(energy);

				// Check not null
				if (type == null) {
					continue;
				}

				// Check not null
				if (type.getCapabilityWithModCheck() == null) {
					continue;
				}

				// Iterate over
				for (Capability capability : type.getCapabilityWithModCheck()) {
					// Check if host has capability
					if (tile.hasCapability(capability, null))
					// return
					{
						return type.energy;
					}
				}
			}
		}

		return null;
	}

	public static LiquidAIEnergy getEnergyFromItem(Item item) {
		// Check for EU Api loaded, and item can handle EU
		if (IntegrationsHelper.instance.isLoaded(EU) && item instanceof IElectricItem) {
			return EU;
			// Check for joule api loaded, and item can handle J
		} else if (IntegrationsHelper.instance.isLoaded(J) && item instanceof IEnergizedItem) {
			return J;
			// Check for Ember api loaded, and item can handle Ember
		} else if (Loader.isModLoaded("redstoneflux") && item instanceof IEnergyContainerItem) {
			return RF;
		}
		return null;
	}

	@Deprecated
	public static IAEEnergyStack ConvertToAEFluidStack(final LiquidAIEnergy Energy, final long fluidAmount) {

		IAEEnergyStack Stack;
		Stack = AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class).createStack(new FluidStack(Energy, 1));
		Stack.setStackSize(fluidAmount);
		return Stack;
	}

	public static ISyncHost getSyncHostByParams(BlockPos pos, AEPartLocation side, World obj) {

		if (side == AEPartLocation.INTERNAL) {
			return getTileByParams(pos, obj);
		} else if (side != null) {
			return getPartByParams(pos, side.getFacing(), obj);
		}
		return null;
	}

	public static AITile getTileByParams(BlockPos pos, World worldObj) {

		World world = getClientOrServerWorld(worldObj);

		// Check if tile instance of AIpart, depending on it return null or part
		return world.getTileEntity(pos) instanceof AITile ? (AITile) world.getTileEntity(pos) : null;
	}

	public static AIPart getPartByParams(BlockPos pos, EnumFacing side, World worldObj) {

		World world = getClientOrServerWorld(worldObj);

		TileEntity entity = world.getTileEntity(pos);

		return (AIPart) (((IPartHost) entity).getPart(side));
	}

	private static World getClientOrServerWorld(World w) {

		World world = w;

		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			if (world == null) {
				world = Minecraft.getMinecraft().world;
			}
		}

		return world;
	}

	public static IAEManaStack getManaFromItemStack(ItemStack itemStack) {
		return new AEManaStack(0);
	}
}

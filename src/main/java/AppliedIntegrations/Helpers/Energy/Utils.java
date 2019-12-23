package AppliedIntegrations.Helpers.Energy;
import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.Parts.AIPart;
import AppliedIntegrations.api.Botania.IAEManaStack;
import AppliedIntegrations.api.ISyncHost;
import AppliedIntegrations.api.Storage.EnergyStack;
import AppliedIntegrations.api.Storage.IAEEnergyStack;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.AEEnergyStack;
import AppliedIntegrations.grid.EnumCapabilityType;
import AppliedIntegrations.grid.Mana.AEManaStack;
import AppliedIntegrations.tile.AITile;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartItem;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AEPartLocation;
import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.IElectricItem;
import ic2.api.tile.IEnergyStorage;
import mekanism.api.energy.IEnergizedItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nonnull;

import static AppliedIntegrations.grid.Implementation.AIEnergy.*;

/**
 * @Author Azazell
 */
@Optional.InterfaceList(value = {@Optional.Interface(iface = "teamroots.embers.api.item.IEmberChargedTool", modid = "embers", striprefs = true), @Optional.Interface(iface = "ic2.api.item.IElectricItem", modid = "ic2", striprefs = true), @Optional.Interface(iface = "mekanism.api.energy.IEnergizedItem", modid = "mekanism", striprefs = true)})
public class Utils {
	public static IAEStack<IAEEnergyStack> getEnergyStackFromItemStack(ItemStack itemStack, World world) {
		EnergyStack stack = new EnergyStack(getEnergyFromItemStack(itemStack, world), 1);

		// Check not null and meaningful
		if (stack.getEnergy() == null) {
			return null;
		}

		return AEEnergyStack.fromStack(stack);
	}

	public static LiquidAIEnergy getEnergyFromItemStack(ItemStack itemStack, World world) {
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
				final TileEntity tileEntity = blk.createTileEntity(world, blk.getDefaultState());

				// Double validate tile nonnull since SOME MODS(looking on IC2) sometime initialize it by null
				if (tileEntity != null) {
					return getEnergyFromContainer(tileEntity);
				}

				return null;
			}
		}

		return getEnergyFromItem(item);
	}

	/**
	 * @param part host to check
	 * @return first energy handled by IPart
	 */
	private static LiquidAIEnergy getEnergyFromPart(IPart part) {
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
					if (part.hasCapability(capability)) {
						// return
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
	private static LiquidAIEnergy getEnergyFromContainer(TileEntity tile) {
		// Iterate over all energies, to get handled one
		for (LiquidAIEnergy energy : LiquidAIEnergy.energies.values()) {
			// Get capability enum type from energy
			if (EnumCapabilityType.fromEnergy(energy) != null) {
				// Record type
				EnumCapabilityType type = EnumCapabilityType.fromEnergy(energy);
				if (type == null) {
					continue;
				}

				if (type.getCapabilityWithModCheck() == null) {
					// Check if it's EU which is special case because it has no 1.12.2 capability
					if (type == EnumCapabilityType.EU) {
						if (tile instanceof IEnergyStorage) {
							return type.energy;
						}
					}

					continue;
				}

				// Iterate over
				for (Capability capability : type.getCapabilityWithModCheck()) {
					// Check if host has capability
					if (tile.hasCapability(capability, null)) {
						// return
						return type.energy;
					}
				}
			}
		}

		return null;
	}

	private static LiquidAIEnergy getEnergyFromItem(Item item) {
		// For RF one we need to actually check for loaded COFH|API, so FE determination is for top-layer methods under this
		if (IntegrationsHelper.instance.isLoaded(EU, false) && item instanceof IElectricItem) {
			return EU;
		} else if (IntegrationsHelper.instance.isLoaded(J, false) && item instanceof IEnergizedItem) {
			return J;
		} else if (IntegrationsHelper.instance.isLoaded(RF, true) && item instanceof IEnergyContainerItem) {
			return RF;
		}
		return null;
	}

	public static ISyncHost getSyncHostByParams(@Nonnull BlockPos pos, @Nonnull AEPartLocation side, @Nonnull World obj) {
		if (side == AEPartLocation.INTERNAL) {
			return getTileByParams(pos, obj);
		} else {
			return getPartByParams(pos, side.getFacing(), obj);
		}
	}

	public static AITile getTileByParams(BlockPos pos, World world) {
		// Check if tile instance of AITile, depending on it return null or tile
		return world.getTileEntity(pos) instanceof AITile ? (AITile) world.getTileEntity(pos) : null;
	}

	public static AIPart getPartByParams(@Nonnull BlockPos pos, @Nonnull EnumFacing side, @Nonnull World world) {
		return (AIPart) (((IPartHost) world.getTileEntity(pos)).getPart(side));
	}

	public static IAEManaStack getManaFromItemStack(ItemStack itemStack) {
		return new AEManaStack(0);
	}
}

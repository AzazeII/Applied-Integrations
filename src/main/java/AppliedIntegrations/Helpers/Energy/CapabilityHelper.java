package AppliedIntegrations.Helpers.Energy;
import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.EnumCapabilityType;
import appeng.api.util.AEPartLocation;
import ic2.core.block.comp.Energy;
import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyOutputter;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.common.capabilities.Capabilities;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.Vector;

import static AppliedIntegrations.api.Storage.LiquidAIEnergy.energies;
import static AppliedIntegrations.grid.Implementation.AIEnergy.*;

/**
 * @Author Azazell
 */
public class CapabilityHelper {

	private final AEPartLocation side;

	private TileEntity capabilityHandler;

	private Vector<Capability> capabilities = new Vector<>();
	private Vector<Class> hostClasses = new Vector<>();

	public CapabilityHelper(TileEntity capabilityHandler, AEPartLocation side) {
		this.capabilityHandler = capabilityHandler;
		this.side = side;

		if (capabilityHandler != null) {
			for (EnumCapabilityType capabilityType : EnumCapabilityType.values) {
				if (capabilityType == null) {
					continue;
				}

				if (capabilityType.getCapabilityWithModCheck() != null) {
					for (Capability capability : capabilityType.getCapabilityWithModCheck()) {
						if (capabilityHandler.hasCapability(capability, side.getFacing())) {
							capabilities.add(capability);
						}
					}
				}

				// Special case for EU2's custom capabilities
				if (capabilityType.getHostClassesWithModCheck() != null) {
					for (Class clazz : capabilityType.getHostClassesWithModCheck()) {
						if (clazz.isInstance(capabilityHandler)) {
							hostClasses.add(clazz);
						}
					}
				}
			}
		}
	}

	/**
	 * @param energy energy to check capability for
	 * @return Pair of number represeting max storage, and number type;
	 * Allowed types: integer, double, long
	 */
	public int getMaxStored(LiquidAIEnergy energy) {
		for (Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
			if (capabilities.contains(capability)) {
				if (capability == CapabilityEnergy.ENERGY) {
					IEnergyStorage energyStorageCapability = (IEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					return energyStorageCapability.getMaxEnergyStored();
				} else if (IntegrationsHelper.instance.isLoaded(J, false) && capability == Capabilities.ENERGY_STORAGE_CAPABILITY) {
					IStrictEnergyStorage storage = (IStrictEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) storage.getMaxEnergy();
				} else if (IntegrationsHelper.instance.isLoaded(TESLA, false) && capability == TeslaCapabilities.CAPABILITY_HOLDER) {
					ITeslaHolder teslaHolderCapability = (ITeslaHolder) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) teslaHolderCapability.getCapacity();
				}
			}
		}

		if (IntegrationsHelper.instance.isLoaded(EU, false)) {
			if (capabilityHandler instanceof ic2.core.block.machine.tileentity.TileEntityElectricMachine) {
				ic2.core.block.machine.tileentity.TileEntityElectricMachine electricMachine =
						(ic2.core.block.machine.tileentity.TileEntityElectricMachine) capabilityHandler;
				Energy capability = electricMachine.getComponent(Energy.class);
				return (int) capability.getCapacity();
			} else if (capabilityHandler instanceof ic2.api.tile.IEnergyStorage) {
				ic2.api.tile.IEnergyStorage iEnergyStorage = (ic2.api.tile.IEnergyStorage) capabilityHandler;
				return iEnergyStorage.getCapacity();
			}
		}

		return 0;
	}

	/**
	 * @param energy energy to check capability for
	 * @return Pair of number representing current storage, and number type;
	 * Allowed types: integer, double, long
	 */
	public int getStored(LiquidAIEnergy energy) {
		if (energy == null) {
			return 0;
		}

		if (EnumCapabilityType.fromEnergy(energy) == null) {
			return 0;
		}

		for (Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
			if (capabilities.contains(capability)) {
				if (capability == CapabilityEnergy.ENERGY) {
					IEnergyStorage energyStorageCapability = (IEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					return energyStorageCapability.getEnergyStored();
				} else if (IntegrationsHelper.instance.isLoaded(J, false) && capability == Capabilities.ENERGY_STORAGE_CAPABILITY) {
					IStrictEnergyStorage storage = (IStrictEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) storage.getEnergy();
				} else if (IntegrationsHelper.instance.isLoaded(TESLA, false) && capability == TeslaCapabilities.CAPABILITY_HOLDER) {
					ITeslaHolder teslaHolderCapability = (ITeslaHolder) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) teslaHolderCapability.getStoredPower();
				}
			}
		}

		if (IntegrationsHelper.instance.isLoaded(EU, false)) {
			if (capabilityHandler instanceof ic2.core.block.machine.tileentity.TileEntityElectricMachine) {
				ic2.core.block.machine.tileentity.TileEntityElectricMachine electricMachine = (ic2.core.block.machine.tileentity.TileEntityElectricMachine) capabilityHandler;
				Energy capability = electricMachine.getComponent(Energy.class);
				return (int) capability.getEnergy();
			} else if (capabilityHandler instanceof ic2.api.tile.IEnergyStorage) {
				ic2.api.tile.IEnergyStorage iEnergyStorage = (ic2.api.tile.IEnergyStorage) capabilityHandler;
				return iEnergyStorage.getStored();
			}
		}

		return 0;
	}

	public int receiveEnergy(Number val, boolean simulate, LiquidAIEnergy energy) {
		final int intVal = val.intValue();
		for (Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
			if (capabilities.contains(capability)) {
				if (capability == CapabilityEnergy.ENERGY) {
					IEnergyStorage energyStorageCapability = (IEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					return energyStorageCapability.receiveEnergy(intVal, simulate);
				} else if (IntegrationsHelper.instance.isLoaded(J, false) && capability == Capabilities.ENERGY_ACCEPTOR_CAPABILITY) {
					IStrictEnergyAcceptor storage = (IStrictEnergyAcceptor) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) storage.acceptEnergy(side.getFacing(), val.doubleValue(), !simulate);
				} else if (IntegrationsHelper.instance.isLoaded(TESLA, false) && (capability == TeslaCapabilities.CAPABILITY_CONSUMER)) {
					ITeslaConsumer teslaHolderCapability = (ITeslaConsumer) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) teslaHolderCapability.givePower(val.longValue(), simulate);
				}
			}
		}

		if (IntegrationsHelper.instance.isLoaded(EU, false)) {
			if (capabilityHandler instanceof ic2.core.block.machine.tileentity.TileEntityElectricMachine) {
				ic2.core.block.machine.tileentity.TileEntityElectricMachine electricMachine =
						(ic2.core.block.machine.tileentity.TileEntityElectricMachine) capabilityHandler;
				Energy capability = electricMachine.getComponent(Energy.class);

				// Don't check for enough energy as we did in extract. IC2 api does it for us
				// Return energy back if it was simulation
				double added = capability.addEnergy(intVal);
				if (simulate) {
					capability.addEnergy(-added);
				}

				return (int) added;
			} else if (capabilityHandler instanceof ic2.api.tile.IEnergyStorage) {
				ic2.api.tile.IEnergyStorage iEnergyStorage = (ic2.api.tile.IEnergyStorage) capabilityHandler;

				// Don't check for enough energy as we did in extract. IC2 api does it for us
				int storedBefore = iEnergyStorage.getStored();
				iEnergyStorage.addEnergy(intVal);
				int storedAfter = iEnergyStorage.getStored();

				// Return energy back if it was simulation
				if (simulate) {
					iEnergyStorage.setStored(storedBefore);
				}

				return storedAfter - storedBefore;
			}
		}

		return 0;
	}

	public int extractEnergy(Number val, boolean simulate, LiquidAIEnergy energy) {
		if (energy == null) {
			return 0;
		}

		if (EnumCapabilityType.fromEnergy(energy) == null) {
			return 0;
		}

		final int intVal = val.intValue();
		for (Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
			if (capabilities.contains(capability)) {
				if (capability == CapabilityEnergy.ENERGY) {
					IEnergyStorage energyStorageCapability = (IEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					return energyStorageCapability.extractEnergy(intVal, simulate);
				} else if (IntegrationsHelper.instance.isLoaded(J, false) && capability == Capabilities.ENERGY_OUTPUTTER_CAPABILITY) {
					IStrictEnergyOutputter storage = (IStrictEnergyOutputter) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) storage.pullEnergy(side.getFacing(), val.doubleValue(), simulate);
				} else if (IntegrationsHelper.instance.isLoaded(TESLA, false) && capability == TeslaCapabilities.CAPABILITY_PRODUCER) {
					ITeslaProducer teslaHolderCapability = (ITeslaProducer) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) teslaHolderCapability.takePower(val.longValue(), simulate);
				}
			}
		}


		if (IntegrationsHelper.instance.isLoaded(EU, false)) {
			if (capabilityHandler instanceof ic2.core.block.machine.tileentity.TileEntityElectricMachine) {
				ic2.core.block.machine.tileentity.TileEntityElectricMachine electricMachine =
						(ic2.core.block.machine.tileentity.TileEntityElectricMachine) capabilityHandler;
				Energy capability = electricMachine.getComponent(Energy.class);
				if (capability.getEnergy() < intVal) {
					return 0;
				}

				double added = capability.addEnergy(-intVal);

				// Return energy back if it was simulation
				if (simulate) {
					capability.addEnergy(-added);
				}

				return (int) added;
			} else if (capabilityHandler instanceof ic2.api.tile.IEnergyStorage) {
				ic2.api.tile.IEnergyStorage iEnergyStorage = (ic2.api.tile.IEnergyStorage) capabilityHandler;
				if (iEnergyStorage.getStored() < intVal) {
					return 0;
				}

				int storedBefore = iEnergyStorage.getStored();
				iEnergyStorage.addEnergy(-intVal);
				int storedAfter = iEnergyStorage.getStored();

				// Return energy back if it was simulation
				if (simulate) {
					iEnergyStorage.setStored(storedBefore);
				}

				return storedAfter - storedBefore;
			}
		}

		return 0;
	}

	public long extractAllStored(int max) {
		int extracted = 0;
		for (LiquidAIEnergy energy : energies.values()) {
			extracted += extractEnergy(Math.min(getStored(energy), max), false, energy);
		}

		return extracted;
	}

	public boolean operatesEnergy(LiquidAIEnergy energy) {
		if (EnumCapabilityType.fromEnergy(energy) == null) {
			return false;
		}

		EnumCapabilityType type = EnumCapabilityType.fromEnergy(energy);
		if (type == null) {
			return false;
		}

		// capabilities#containsAny
		if (type.getCapabilityWithModCheck() != null) {
			for (Capability capability : capabilities) {
				for (Capability otherCapability : type.getCapabilityWithModCheck()) {
					if (capability.equals(otherCapability)) {
						return true;
					}
				}
			}
		}

		if (type.getHostClassesWithModCheck() != null) {
			// Special case for EU2's custom capabilities
			for (Class clazz : hostClasses) {
				for (Class otherClazz : type.getHostClassesWithModCheck()) {
					if (clazz.equals(otherClazz)) {
						return true;
					}
				}
			}
		}

		return false;
	}
}

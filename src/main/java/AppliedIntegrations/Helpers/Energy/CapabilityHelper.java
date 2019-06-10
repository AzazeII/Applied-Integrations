package AppliedIntegrations.Helpers.Energy;


import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.EnumCapabilityType;
import appeng.api.util.AEPartLocation;
import ic2.api.energy.tile.IEnergySink;
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

	public CapabilityHelper(TileEntity capabilityHandler, AEPartLocation side) {

		this.capabilityHandler = capabilityHandler;
		this.side = side;

		if (capabilityHandler != null) {
			// Iterate over capability types
			for (EnumCapabilityType capabilityType : EnumCapabilityType.values) {
				// Iterate over capabilities linked with this type
				for (Capability capability : capabilityType.capabilities) {
					// Check if handler have this capability
					if (capabilityHandler.hasCapability(capability, side.getFacing())) {
						// Add capability to list
						capabilities.add(capability);
					}
				}
			}
		}
	}

	/**
	 * @param energy energy to check capability for
	 * @return Pair of number represeting max storaga, and number type;
	 * Allowed types: integer, double, long
	 */
	public int getMaxStored(LiquidAIEnergy energy) {

		for (Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
			if (capabilities.contains(capability)) {
				if (capability == CapabilityEnergy.ENERGY) {
					IEnergyStorage energyStorageCapability = (IEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					;
					return energyStorageCapability.getMaxEnergyStored();
				} else if (IntegrationsHelper.instance.isLoaded(J) && capability == Capabilities.ENERGY_STORAGE_CAPABILITY) {
					IStrictEnergyStorage storage = (IStrictEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) storage.getMaxEnergy();
				} else if (IntegrationsHelper.instance.isLoaded(TESLA) && capability == TeslaCapabilities.CAPABILITY_HOLDER) {
					ITeslaHolder teslaHolderCapability = (ITeslaHolder) capabilityHandler.getCapability(capability, side.getFacing());
					;
					return (int) teslaHolderCapability.getCapacity();
				}
			}
		}

		if (IntegrationsHelper.instance.isLoaded(EU) && capabilityHandler instanceof IEnergySink) {
			// sink = (IEnergySink)capabilityHandler;
			// sink.getDemandedEnergy());
		}

		return 0;
	}

	public int receiveEnergy(Number val, boolean simulate, LiquidAIEnergy energy) {
		// Iterate for each capability from energy
		for (Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
			// Check if our capabilities contains this capability
			if (capabilities.contains(capability)) {
				// Check if capability is RF capability
				if (capability == CapabilityEnergy.ENERGY) {
					// Create RF Storage
					IEnergyStorage energyStorageCapability = (IEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());

					// Receive energy
					return energyStorageCapability.receiveEnergy(val.intValue(), simulate);
				} else if (IntegrationsHelper.instance.isLoaded(J) && capability == Capabilities.ENERGY_ACCEPTOR_CAPABILITY) {
					// Create joule storage
					IStrictEnergyAcceptor storage = (IStrictEnergyAcceptor) capabilityHandler.getCapability(capability, side.getFacing());

					// Receive energy
					return (int) storage.acceptEnergy(side.getFacing(), val.doubleValue(), !simulate);
				} else if (IntegrationsHelper.instance.isLoaded(TESLA) && (capability == TeslaCapabilities.CAPABILITY_CONSUMER)) {
					// Create TESLA storage
					ITeslaConsumer teslaHolderCapability = (ITeslaConsumer) capabilityHandler.getCapability(capability, side.getFacing());

					// Receive energy
					return (int) teslaHolderCapability.givePower(val.longValue(), simulate);
				}
			}
		}

		if (IntegrationsHelper.instance.isLoaded(EU) && capabilityHandler instanceof IEnergySink) {
			// sink = (IEnergySink)capabilityHandler;
			// sink.getDemandedEnergy());
		}

		return 0;
	}

	public long extractAllStored(int max) {
		// All energy extracted
		int extracted = 0;

		// Iterate over all energies
		for (LiquidAIEnergy energy : energies.values()) {
			// Extract it
			extracted += extractEnergy(Math.min(getStored(energy), max), false, energy);
		}

		AILog.chatLog("Extracted: " + extracted);
		return extracted;
	}

	public int extractEnergy(Number val, boolean simulate, LiquidAIEnergy energy) {
		// Check not null
		if (energy == null) {
			return 0;
		}

		// Check has type
		if (EnumCapabilityType.fromEnergy(energy) == null) {
			return 0;
		}

		// Iterate over all capabilities from this energy
		for (Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
			// Check if tile has this capability
			if (capabilities.contains(capability)) {
				// Check if capability belong to RF system
				if (capability == CapabilityEnergy.ENERGY) {
					// Get storage
					IEnergyStorage energyStorageCapability = (IEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());

					// Extract energy
					return energyStorageCapability.extractEnergy(val.intValue(), simulate);

					// Check if capability belong to Ember system
				} else if (IntegrationsHelper.instance.isLoaded(J) && capability == Capabilities.ENERGY_OUTPUTTER_CAPABILITY) {
					// Get storage
					IStrictEnergyOutputter storage = (IStrictEnergyOutputter) capabilityHandler.getCapability(capability, side.getFacing());

					// Extract energy
					return (int) storage.pullEnergy(side.getFacing(), val.doubleValue(), simulate);

					// Check if capability belong to TESLA system
				} else if (IntegrationsHelper.instance.isLoaded(TESLA) && capability == TeslaCapabilities.CAPABILITY_PRODUCER) {
					// Get storage
					ITeslaProducer teslaHolderCapability = (ITeslaProducer) capabilityHandler.getCapability(capability, side.getFacing());

					// Extract energy
					return (int) teslaHolderCapability.takePower(val.longValue(), simulate);
				}
			}
		}

		if (IntegrationsHelper.instance.isLoaded(EU) && capabilityHandler instanceof IEnergySink) {
			// sink = (IEnergySink)capabilityHandler;
			// sink.getDemandedEnergy());
		}

		return 0;
	}

	/**
	 * @param energy energy to check capability for
	 * @return Pair of number representing current storage, and number type;
	 * Allowed types: integer, double, long
	 */
	public int getStored(LiquidAIEnergy energy) {
		// Check not null
		if (energy == null) {
			return 0;
		}

		// Check has type
		if (EnumCapabilityType.fromEnergy(energy) == null) {
			return 0;
		}

		for (Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
			if (capabilities.contains(capability)) {
				if (capability == CapabilityEnergy.ENERGY) {
					IEnergyStorage energyStorageCapability = (IEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					return energyStorageCapability.getEnergyStored();
				} else if (IntegrationsHelper.instance.isLoaded(J) && capability == Capabilities.ENERGY_STORAGE_CAPABILITY) {
					IStrictEnergyStorage storage = (IStrictEnergyStorage) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) storage.getEnergy();
				} else if (IntegrationsHelper.instance.isLoaded(TESLA) && capability == TeslaCapabilities.CAPABILITY_HOLDER) {
					ITeslaHolder teslaHolderCapability = (ITeslaHolder) capabilityHandler.getCapability(capability, side.getFacing());
					return (int) teslaHolderCapability.getStoredPower();
				}
			}
		}

		if (IntegrationsHelper.instance.isLoaded(EU) && capabilityHandler instanceof IEnergySink) {
			IEnergySink sink = (IEnergySink) capabilityHandler;
			return 0;
		}

		return 0;
	}

	public boolean operatesEnergy(LiquidAIEnergy energy) {
		// Check not null
		if (EnumCapabilityType.fromEnergy(energy) == null) {
			return false;
		}

		// Get vector
		Vector<Capability> capabilities = EnumCapabilityType.fromEnergy(energy).getCapabilityWithModCheck();

		// Check not null
		if (capabilities == null) {
			return false;
		}

		//Iterate over it's capabilities
		return this.capabilities.contains(capabilities.firstElement());
	}
}

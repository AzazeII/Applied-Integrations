package AppliedIntegrations.grid;
import AppliedIntegrations.Integration.IntegrationsHelper;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.Implementation.AIEnergy;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import mekanism.common.capabilities.Capabilities;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Vector;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "mekanism.common.capabilities.Capabilities", modid = "mekanism", striprefs = true), @Optional.Interface(iface = "teamroots.embers.power.EmberCapabilityProvider", modid = "embers", striprefs = true), @Optional.Interface(iface = "net.darkhax.tesla.capability.TeslaCapabilities", modid = "tesla", striprefs = true)})
/**
 * @Author Azazell
 */
public class EnumCapabilityType {
	public static EnumCapabilityType FE;
	public static EnumCapabilityType Joules;
	public static EnumCapabilityType EU;
	public static EnumCapabilityType Tesla;
	public static Vector<EnumCapabilityType> values = new Vector<>();
	public LiquidAIEnergy energy;
	public Vector<Class> hostClasses = new Vector<>();
	public Vector<Capability> capabilities = new Vector<>();

	static {
		FE = new EnumCapabilityType(AIEnergy.RF, CapabilityEnergy.ENERGY);
		if (IntegrationsHelper.instance.isLoaded(AIEnergy.J, false)) {
			Joules = new EnumCapabilityType(AIEnergy.J, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, Capabilities.ENERGY_STORAGE_CAPABILITY, Capabilities.ENERGY_OUTPUTTER_CAPABILITY);
		}
		if (IntegrationsHelper.instance.isLoaded(AIEnergy.EU, false)) {
			// IEnergyStorage as basic machine which uses EU
			// TileEntityElectricMachine as class being overrode by all EU machines
			EU = new EnumCapabilityType(AIEnergy.EU, ic2.api.tile.IEnergyStorage.class, TileEntityElectricMachine.class);
		}
		if (IntegrationsHelper.instance.isLoaded(AIEnergy.TESLA, false)) {
			Tesla = new EnumCapabilityType(AIEnergy.TESLA, TeslaCapabilities.CAPABILITY_CONSUMER, TeslaCapabilities.CAPABILITY_HOLDER, TeslaCapabilities.CAPABILITY_PRODUCER);
		}
	}

	private EnumCapabilityType(LiquidAIEnergy energy) {
		this.energy = energy;
		EnumCapabilityType.values.add(this);
	}

	/**
	 * Mods like EU doesn't implement new 1.12.2 capability system, we can use class-check instead for this
	 */
	private EnumCapabilityType(LiquidAIEnergy energy, @Nullable Class... hostClasses) {
		this(energy);
		if (hostClasses != null) {
			this.hostClasses.addAll(Arrays.asList(hostClasses));
		}
	}

	private EnumCapabilityType(LiquidAIEnergy energy, @Nullable Capability... capability) {
		this(energy);

		// Check not null
		if (capability != null) {
			this.capabilities.addAll(Arrays.asList(capability));
		}
	}

	public static EnumCapabilityType fromEnergy(LiquidAIEnergy energy) {
		for (EnumCapabilityType type : values) {
			if (type.energy == energy) {
				return type;
			}
		}

		return null;
	}

	/**
	 * Class def not found-safe input capability getter
	 * @return Null or capabilities used for energy input of this type
	 */
	public Capability getInputCapability() {
		// Check energy api loaded
		if (IntegrationsHelper.instance.isLoaded(this.energy, false)) {
			if (capabilities != null && !capabilities.isEmpty()) {
				return capabilities.firstElement();
			}
		}
		return null;
	}

	/**
	 * Class def not found safe-getter
	 * @return null or host classes of this type
	 */
	public Vector<Class> getHostClassesWithModCheck() {
		if (IntegrationsHelper.instance.isLoaded(this.energy, false)) {
			if (hostClasses != null && !hostClasses.isEmpty()) {
				return this.hostClasses;
			}
		}
		return null;
	}

	/**
	 * Class def not found safe-getter
	 * @return null or capabilities of this type
	 */
	public Vector<Capability> getCapabilityWithModCheck() {
		if (IntegrationsHelper.instance.isLoaded(this.energy, false)) {
			// Check not null and not empty
			if (capabilities != null && !capabilities.isEmpty()) {
				return this.capabilities;
			}
		}
		return null;
	}

	/**
	 * Class def not found safe getter
	 * @return Null or capabilities used for energy output of this type
	 */
	public Capability getOutputCapabilities() {
		// Check energy api loaded
		if (IntegrationsHelper.instance.isLoaded(this.energy, false)) {
			// Check not null and not empty
			if (capabilities != null && !capabilities.isEmpty()) {
				return capabilities.lastElement();
			}
		}
		return null;
	}

	public boolean isUsesType(TileEntity tileEntity, EnumFacing side) {
		if (getCapabilityWithModCheck() != null) {
			for (Capability capability : getCapabilityWithModCheck()) {
				if (tileEntity.hasCapability(capability, side)) {
					return true;
				}
			}
		}

		// Special case for custom capability(components) system of IC2
		if (getHostClassesWithModCheck() != null) {
			for (Class clazz : getHostClassesWithModCheck()) {
				if (clazz.isInstance(tileEntity)) {
					return true;
				}
			}
		}

		return false;
	}
}

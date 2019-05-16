package AppliedIntegrations.grid;


import AppliedIntegrations.Helpers.IntegrationsHelper;
import AppliedIntegrations.api.Storage.LiquidAIEnergy;
import AppliedIntegrations.grid.Implementation.AIEnergy;
import mekanism.common.capabilities.Capabilities;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;
import teamroots.embers.power.EmberCapabilityProvider;

import javax.annotation.Nullable;
import java.util.Vector;

@Optional.InterfaceList(value = {@Optional.Interface(iface = "mekanism.common.capabilities.Capabilities", modid = "mekanism", striprefs = true), @Optional.Interface(iface = "teamroots.embers.power.EmberCapabilityProvider", modid = "embers", striprefs = true), @Optional.Interface(iface = "net.darkhax.tesla.capability.TeslaCapabilities", modid = "tesla", striprefs = true)})
/**
 * @Author Azazell
 */ public class EnumCapabilityType {
	public static EnumCapabilityType FE;

	public static EnumCapabilityType Joules;

	public static EnumCapabilityType EU;

	public static EnumCapabilityType Ember;

	public static EnumCapabilityType Tesla;

	public static Vector<EnumCapabilityType> values = new Vector<>();

	public LiquidAIEnergy energy;

	public Vector<Capability> capabilities = new Vector<>();

	static {
		FE = new EnumCapabilityType(AIEnergy.RF, CapabilityEnergy.ENERGY);
		if (IntegrationsHelper.instance.isLoaded(AIEnergy.J)) {
			Joules = new EnumCapabilityType(AIEnergy.J, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, Capabilities.ENERGY_STORAGE_CAPABILITY, Capabilities.ENERGY_OUTPUTTER_CAPABILITY);
		}
		if (IntegrationsHelper.instance.isLoaded(AIEnergy.EU)) {
			EU = new EnumCapabilityType(AIEnergy.EU, null);
		}
		if (IntegrationsHelper.instance.isLoaded(AIEnergy.Ember)) {
			Ember = new EnumCapabilityType(AIEnergy.Ember, EmberCapabilityProvider.emberCapability);
		}
		if (IntegrationsHelper.instance.isLoaded(AIEnergy.TESLA)) {
			Tesla = new EnumCapabilityType(AIEnergy.TESLA, TeslaCapabilities.CAPABILITY_CONSUMER, TeslaCapabilities.CAPABILITY_HOLDER, TeslaCapabilities.CAPABILITY_PRODUCER);
		}
	}

	EnumCapabilityType(LiquidAIEnergy energy, @Nullable Capability... capability) {

		this.energy = energy;

		// Check not null
		if (capability != null)
		// Iterate over capabilities
		{
			for (Capability capability1 : capability)
				// Add capability
				this.capabilities.add(capability1);
		}

		values.add(this);
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
	 * Class def not found safe input capability getter
	 *
	 * @return
	 */
	public Capability getInputCapability() {
		// Check energy api loaded
		if (IntegrationsHelper.instance.isLoaded(this.energy))
		// Check not null and not empty
		{
			if (capabilities != null && !capabilities.isEmpty()) {
				return capabilities.firstElement();
			}
		}
		return null;
	}

	/**
	 * Class def not found safe getter
	 *
	 * @return null or capabilities of this type
	 */
	public Vector<Capability> getCapabilityWithModCheck() {

		if (IntegrationsHelper.instance.isLoaded(this.energy))
		// Check not null and not empty
		{
			if (capabilities != null && !capabilities.isEmpty()) {
				return this.capabilities;
			}
		}
		return null;
	}

	/**
	 * Class def not found safe getter
	 *
	 * @return null or output of this type
	 */
	public Capability getOutputCapabilities() {
		// Check energy api loaded
		if (IntegrationsHelper.instance.isLoaded(this.energy))
		// Check not null and not empty
		{
			if (capabilities != null && !capabilities.isEmpty()) {
				return capabilities.lastElement();
			}
		}
		return null;
	}
}

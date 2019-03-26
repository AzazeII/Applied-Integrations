package AppliedIntegrations.API.Storage;

import AppliedIntegrations.Helpers.IntegrationsHelper;
import mekanism.common.capabilities.Capabilities;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import teamroots.embers.power.EmberCapabilityProvider;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.Ember;

@Optional.InterfaceList(value = {
        @Optional.Interface(iface = "mekanism.common.capabilities.Capabilities", modid = "mekanism", striprefs = true),
        @Optional.Interface(iface = "teamroots.embers.power.EmberCapabilityProvider", modid = "embers", striprefs = true),
        @Optional.Interface(iface = "net.darkhax.tesla.capability.TeslaCapabilities", modid = "tesla", striprefs = true)
})
/**
 * @Author Azazell
 */
public class EnumCapabilityType {
    public static EnumCapabilityType FE;
    public static EnumCapabilityType Joules;
    public static EnumCapabilityType EU;
    public static EnumCapabilityType Ember;
    public static EnumCapabilityType Tesla;

    public static Vector<EnumCapabilityType> values = new Vector<>();
    public LiquidAIEnergy energy;
    public Vector<Capability> capabilities = new Vector<>();

    static{
        FE = new EnumCapabilityType(LiquidAIEnergy.RF, CapabilityEnergy.ENERGY);
        if(IntegrationsHelper.instance.isLoaded(LiquidAIEnergy.J))
            Joules = new EnumCapabilityType(LiquidAIEnergy.J, Capabilities.ENERGY_ACCEPTOR_CAPABILITY, Capabilities.ENERGY_STORAGE_CAPABILITY, Capabilities.ENERGY_OUTPUTTER_CAPABILITY);
        if(IntegrationsHelper.instance.isLoaded(LiquidAIEnergy.EU))
            EU = new EnumCapabilityType(LiquidAIEnergy.EU, null);
        if(IntegrationsHelper.instance.isLoaded(LiquidAIEnergy.Ember))
            Ember = new EnumCapabilityType(LiquidAIEnergy.Ember, EmberCapabilityProvider.emberCapability);
        if(IntegrationsHelper.instance.isLoaded(LiquidAIEnergy.TESLA))
            Tesla = new EnumCapabilityType(LiquidAIEnergy.TESLA, TeslaCapabilities.CAPABILITY_CONSUMER, TeslaCapabilities.CAPABILITY_HOLDER, TeslaCapabilities.CAPABILITY_PRODUCER);
    }

    EnumCapabilityType(LiquidAIEnergy energy, @Nullable Capability... capability){
        this.energy = energy;
        for(Capability capability1 : capability)
            this.capabilities.add(capability1);
        values.add(this);
    }

    public static EnumCapabilityType fromEnergy(LiquidAIEnergy energy) {
        for(EnumCapabilityType type : values){
            if(type.energy == energy)
                return type;
        }
        return null;
    }

    /**
     * Class def not found safe input capability getter
     * @return
     */
    public Capability getInputCapability(){
        if(IntegrationsHelper.instance.isLoaded(this.energy))
            return capabilities.firstElement();
        return null;
    }

    /**
     * Class def not found safe getter
     * @return null or capabilities of this type
     */
    public Vector<Capability> getCapabilityWithModCheck(){
        if(IntegrationsHelper.instance.isLoaded(this.energy))
            return this.capabilities;
        return null;
    }

    public Capability getOutputCapabilities() {
        if(IntegrationsHelper.instance.isLoaded(this.energy))
            return capabilities.lastElement();
        return null;
    }
}

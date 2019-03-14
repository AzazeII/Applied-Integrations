package AppliedIntegrations.API.Storage;

import AppliedIntegrations.Helpers.IntegrationsHelper;
import AppliedIntegrations.Utils.AILog;
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
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.tuple.Pair;
import teamroots.embers.power.EmberCapabilityProvider;
import teamroots.embers.power.IEmberCapability;

import java.util.Vector;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.*;

public class CapabilityHelper {

    private final AEPartLocation side;
    private TileEntity capabilityHandler;
    private Vector<Capability> capabilities = new Vector<>();

    public CapabilityHelper(TileEntity capabilityHandler, AEPartLocation side){
        this.capabilityHandler = capabilityHandler;
        this.side = side;

        if(capabilityHandler != null) {
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
     * @param energy
     * energy to check capability for
     * @return
     * Pair of number representing current storage, and number type;
     * Allowed types: integer, double, long
     */
    public int getStored(LiquidAIEnergy energy){
        for(Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
            AILog.info("Iterating over capabilities, current: " + capability.getName());
            if (capabilities.contains(capability)){
                if(capability == CapabilityEnergy.ENERGY){
                    AILog.info(capabilityHandler.toString() + " has " + capability.getName());
                    IEnergyStorage energyStorageCapability = (IEnergyStorage)capabilityHandler.getCapability(capability, side.getFacing());
                    return energyStorageCapability.getEnergyStored();
                }else if(IntegrationsHelper.instance.isLoaded(Ember) && capability == EmberCapabilityProvider.emberCapability){
                    IEmberCapability emberCapability = (IEmberCapability)capabilityHandler.getCapability(capability, side.getFacing());
                    return (int)emberCapability.getEmber();
                }else if(IntegrationsHelper.instance.isLoaded(J) && capability == Capabilities.ENERGY_STORAGE_CAPABILITY){
                    IStrictEnergyStorage storage = (IStrictEnergyStorage)capabilityHandler.getCapability(capability, side.getFacing());
                    return (int)storage.getEnergy();
                }else if(IntegrationsHelper.instance.isLoaded(TESLA) && capability == TeslaCapabilities.CAPABILITY_HOLDER){
                    ITeslaHolder teslaHolderCapability = (ITeslaHolder)capabilityHandler.getCapability(capability, side.getFacing());
                    return (int)teslaHolderCapability.getStoredPower();
                }
            }
        }

        if(IntegrationsHelper.instance.isLoaded(EU) && capabilityHandler instanceof IEnergySink){
            IEnergySink sink = (IEnergySink)capabilityHandler;
            return 0;
        }

        return 0;
    }

    /**
     * @param energy
     * energy to check capability for
     * @return
     * Pair of number represeting max storaga, and number type;
     * Allowed types: integer, double, long
     */
    public int getMaxStored(LiquidAIEnergy energy){
        for(Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
            if (capabilities.contains(capability)){
                if(capability == CapabilityEnergy.ENERGY){
                    IEnergyStorage energyStorageCapability = (IEnergyStorage)capabilityHandler.getCapability(capability, side.getFacing());;
                    return energyStorageCapability.getMaxEnergyStored();
                }else if(IntegrationsHelper.instance.isLoaded(Ember) && capability == EmberCapabilityProvider.emberCapability){
                    IEmberCapability emberCapability = (IEmberCapability)capabilityHandler.getCapability(capability, side.getFacing());;
                    return (int)emberCapability.getEmberCapacity();
                }else if(IntegrationsHelper.instance.isLoaded(J) && capability == Capabilities.ENERGY_STORAGE_CAPABILITY){
                    IStrictEnergyStorage storage = (IStrictEnergyStorage)capabilityHandler.getCapability(capability, side.getFacing());
                    return (int)storage.getMaxEnergy();
                }else if(IntegrationsHelper.instance.isLoaded(TESLA) && capability == TeslaCapabilities.CAPABILITY_HOLDER){
                    ITeslaHolder teslaHolderCapability = (ITeslaHolder)capabilityHandler.getCapability(capability, side.getFacing());;
                    return (int)teslaHolderCapability.getCapacity();
                }
            }
        }

        if(IntegrationsHelper.instance.isLoaded(EU) && capabilityHandler instanceof IEnergySink){
            // sink = (IEnergySink)capabilityHandler;
            // sink.getDemandedEnergy());
        }

        return 0;
    }


    public int receiveEnergy(Number val, boolean simulate, LiquidAIEnergy energy){
        for(Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
            if (capabilities.contains(capability)){
                if(capability == CapabilityEnergy.ENERGY){
                    IEnergyStorage energyStorageCapability = (IEnergyStorage)capabilityHandler.getCapability(capability, side.getFacing());

                    return energyStorageCapability.receiveEnergy(val.intValue(), simulate);

                }else if(IntegrationsHelper.instance.isLoaded(Ember) && capability == EmberCapabilityProvider.emberCapability){
                    IEmberCapability emberCapability = (IEmberCapability)capabilityHandler.getCapability(capability, side.getFacing());;
                    return (int)emberCapability.addAmount(val.doubleValue(), simulate);

                }else if(IntegrationsHelper.instance.isLoaded(J) && capability == Capabilities.ENERGY_ACCEPTOR_CAPABILITY){
                    IStrictEnergyAcceptor storage = (IStrictEnergyAcceptor)capabilityHandler.getCapability(capability, side.getFacing());;
                    return (int)storage.acceptEnergy(side.getFacing(), val.doubleValue(), !simulate);

                }else if(IntegrationsHelper.instance.isLoaded(TESLA) && (capability == TeslaCapabilities.CAPABILITY_CONSUMER)){
                    ITeslaConsumer teslaHolderCapability = (ITeslaConsumer) capabilityHandler.getCapability(capability, side.getFacing());;
                    return (int)teslaHolderCapability.givePower(val.longValue(), simulate);

                }
            }
        }

        if(IntegrationsHelper.instance.isLoaded(EU) && capabilityHandler instanceof IEnergySink){
            // sink = (IEnergySink)capabilityHandler;
            // sink.getDemandedEnergy());
        }

        return 0;
    }

    public int extractEnergy(Number val, boolean simulate, LiquidAIEnergy energy){
        for(Capability capability : EnumCapabilityType.fromEnergy(energy).capabilities) {
            if (capabilities.contains(capability)){
                if(capability == CapabilityEnergy.ENERGY){
                    IEnergyStorage energyStorageCapability = (IEnergyStorage)capabilityHandler.getCapability(capability, side.getFacing());;
                    return energyStorageCapability.extractEnergy(val.intValue(), simulate);

                }else if(IntegrationsHelper.instance.isLoaded(Ember) && capability == EmberCapabilityProvider.emberCapability){
                    IEmberCapability emberCapability = (IEmberCapability)capabilityHandler.getCapability(capability, side.getFacing());;
                    return (int)emberCapability.removeAmount(val.doubleValue(), simulate);

                }else if(IntegrationsHelper.instance.isLoaded(J) && capability == Capabilities.ENERGY_OUTPUTTER_CAPABILITY){
                    IStrictEnergyOutputter storage = (IStrictEnergyOutputter)capabilityHandler.getCapability(capability, side.getFacing());;
                    return (int)storage.pullEnergy(side.getFacing(), val.doubleValue(), !simulate);

                }else if(IntegrationsHelper.instance.isLoaded(TESLA) && capability == TeslaCapabilities.CAPABILITY_PRODUCER){
                    ITeslaProducer teslaHolderCapability = (ITeslaProducer)capabilityHandler.getCapability(capability, side.getFacing());;
                    return (int)teslaHolderCapability.takePower(val.longValue(), simulate);
                }
            }
        }

        if(IntegrationsHelper.instance.isLoaded(EU) && capabilityHandler instanceof IEnergySink){
            // sink = (IEnergySink)capabilityHandler;
            // sink.getDemandedEnergy());
        }

        return 0;
    }
}

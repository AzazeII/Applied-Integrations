package AppliedIntegrations.Helpers;

import AppliedIntegrations.API.*;
import AppliedIntegrations.API.Storage.EnergyStack;
import AppliedIntegrations.API.Storage.EnumCapabilityType;
import AppliedIntegrations.API.Storage.LiquidAIEnergy;
import AppliedIntegrations.Utils.AILog;
import AppliedIntegrations.tile.TileEnergyInterface;
import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEPartLocation;
import appeng.capabilities.Capabilities;
import appeng.me.helpers.IGridProxyable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import teamroots.embers.power.EmberCapabilityProvider;

import java.util.LinkedList;
import java.util.List;

import static AppliedIntegrations.API.Storage.LiquidAIEnergy.*;
import static AppliedIntegrations.API.Storage.LiquidAIEnergy.Ember;
import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;
import static appeng.api.util.AEPartLocation.INTERNAL;

/**
 * Class handler for both tile interface, and part interface
 */
public class InterfaceDuality implements IInterfaceDuality{

    public boolean debug;
    private IEnergyInterface owner;
    private List<LiquidAIEnergy> initializedStorages = new LinkedList<>();

    public InterfaceDuality(IEnergyInterface owner){
        this.owner = owner;

        // RF always Initialized, as FE
        initializedStorages.add(RF);
        if(IntegrationsHelper.instance.isLoaded(EU))
            initializedStorages.add(EU);
        if(IntegrationsHelper.instance.isLoaded(J))
            initializedStorages.add(J);
        if(IntegrationsHelper.instance.isLoaded(Ember))
            initializedStorages.add(Ember);
    }

    @Override
    public double getMaxTransfer(AEPartLocation side) {
        return owner.getMaxTransfer(side);
    }

    @Override
    public LiquidAIEnergy getFilteredEnergy(AEPartLocation side) {
        return owner.getFilteredEnergy(side);
    }

    @Override
    public IInterfaceStorageDuality getEnergyStorage(LiquidAIEnergy energy, AEPartLocation side) {
        return owner.getEnergyStorage(energy, side);
    }

    @Override
    public void DoInjectDualityWork(Actionable action) throws NullNodeConnectionException {
        IGridNode node = owner.getGridNode();
        if (node == null) {
            throw new NullNodeConnectionException();
        }

        // Iterate over all sides(only for interface block)
        for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
            // Is it modulate, or simulate?
            if (action == Actionable.MODULATE) {
                // Iterate over allowed energy type
                for(EnumCapabilityType energyType : EnumCapabilityType.values){
                    // Get energy from type
                    LiquidAIEnergy energy = energyType.energy;
                    // Check if storage available;
                    if(isStorageInitialized(energy)){
                        // Split value to integer
                        Number num = (Number)getEnergyStorage(energy, side).getStored();
                        Integer stored = num.intValue();
                        // Check if there is energy exists and energy not filtered
                        if(stored > 0 && this.getFilteredEnergy(side) != energy){
                            // Find minimum value between energy stored and max transfer
                            int ValuedReceive = (int) Math.min(stored, this.getMaxTransfer(side));
                            // Find amount of energy that can be injected
                            int InjectedAmount = owner.InjectEnergy(new EnergyStack(energy, ValuedReceive), SIMULATE);

                            // Inject energy in ME Network
                            owner.InjectEnergy(new EnergyStack(energy, InjectedAmount), MODULATE);
                            // Remove injected amount from interface storage
                            owner.getEnergyStorage(energy, side).modifyEnergyStored(-InjectedAmount);
                        }
                    }

                }
            }
            if(!(owner instanceof TileEnergyInterface)){
                // Break if owner is partEnergyInterface (iterate only one time)
                debug = false;
                break;
            }
        }
    }

    /**
     * check if energy storage initialized (mod with capability for this storage loaded)
     *
     * @return
     */
    private boolean isStorageInitialized(LiquidAIEnergy energy) {
        return initializedStorages.contains(energy);
    }

    @Override
    public void DoExtractDualityWork(Actionable action) throws NullNodeConnectionException {
        IGridNode node = owner.getGridNode();
        if (node == null) {
            throw new NullNodeConnectionException();
        }

        for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
            if (action == Actionable.MODULATE) {
                if (getFilteredEnergy(side) != null) {
                    Class<?> T = getEnergyStorage(getFilteredEnergy(side), INTERNAL).getTypeClass();
                    IInterfaceStorageDuality interfaceStorageDuality = getEnergyStorage(getFilteredEnergy(side), INTERNAL);

                    if (T != Long.class) {
                        // Get int value from stored amount
                        int stored = ((Number)interfaceStorageDuality.getStored()).intValue();
                        // Extract energy from drive array
                        int extracted = owner.ExtractEnergy(new EnergyStack(getFilteredEnergy(side), (int)getMaxTransfer(side)), SIMULATE);

                        // Check if storage can store new energy
                        if( extracted + stored <= ((Number)interfaceStorageDuality.getMaxStored()).intValue()){
                            // Drain energy from network
                            AILog.info("Drained: " + owner.ExtractEnergy(new EnergyStack(getFilteredEnergy(side), extracted), MODULATE));

                            // Give energy to tile's storage
                            interfaceStorageDuality.modifyEnergyStored(extracted);
                        }

                    }else{
                        // TODO: 2019-02-27 Add tesla extraction
                    }
                }
            }
            if(!(owner instanceof TileEnergyInterface)){
                // Break if owner is partEnergyInterface (iterate only one time)
                break;
            }
        }
    }

    private void transferEnergy(LiquidAIEnergy filteredEnergy, int Amount) {
        /*if(filteredEnergy == RF){
            if(owner.getFacingTile().hasCapability(Capabilities.FORGE_ENERGY, owner.getSide().getOpposite().getFacing())){
                IEnergyStorage capability = getFacingTile().getCapability(Capabilities.FORGE_ENERGY, getSide().getOpposite().getFacing());
                capability.receiveEnergy(Amount,false);
            }
        }else if(filteredEnergy == EU){
            if(this.getFacingTile() instanceof IEnergySink){
                IEnergySink receiver = (IEnergySink)this.getFacingTile();
                receiver.injectEnergy(this.getSide().getFacing(),(double)Amount,4);
            }
        }else if(filteredEnergy == J){
            if(this.getFacingTile() instanceof IStrictEnergyAcceptor){
                IStrictEnergyAcceptor receiver = (IStrictEnergyAcceptor)this.getFacingTile();
                receiver.acceptEnergy(this.getSide().getFacing(),Amount, false);
            }
        }*/
    }

    public <T> T getCapability(Capability<T> capability, AEPartLocation side) {
        if( capability == Capabilities.FORGE_ENERGY ) {
            // FE (RF) Capability
            return (T) this.getEnergyStorage(RF, side);
            // Ember capability
        }else if(IntegrationsHelper.instance.isLoaded(Ember) && capability == EmberCapabilityProvider.emberCapability){
            return (T) this.getEnergyStorage(Ember, side);
            // Joule capability
        }else if(IntegrationsHelper.instance.isLoaded(J) && capability == mekanism.common.capabilities.Capabilities.ENERGY_STORAGE_CAPABILITY ||
                capability == mekanism.common.capabilities.Capabilities.ENERGY_ACCEPTOR_CAPABILITY ||
                capability == mekanism.common.capabilities.Capabilities.ENERGY_OUTPUTTER_CAPABILITY){
            return (T) this.getEnergyStorage(J, side);
            // EU capability
        }
        return null;
    }

    public boolean hasCapability(Capability<?> capability) {
        // Register FE capability
        if (capability == Capabilities.FORGE_ENERGY) {
            return true;
        } else if (IntegrationsHelper.instance.isLoaded(Ember) && capability == EmberCapabilityProvider.emberCapability) {
            return true;
        } else if (IntegrationsHelper.instance.isLoaded(J)){
            if(capability == mekanism.common.capabilities.Capabilities.ENERGY_STORAGE_CAPABILITY ||
                capability == mekanism.common.capabilities.Capabilities.ENERGY_ACCEPTOR_CAPABILITY ||
                capability == mekanism.common.capabilities.Capabilities.ENERGY_OUTPUTTER_CAPABILITY)
                 return true;
        }
        return false;
    }

    // RF Api
                /*if ((int)this.getEnergyStorage(RF, side).getStored() > 0 && this.getFilteredEnergy(side) != RF) {
                    // We can cast double to int, as RFAPI only operates int-energy
                    int ValuedReceive = (int) Math.min((int)this.getEnergyStorage(RF, side).getStored(), this.getMaxTransfer(side));
                    int Diff = owner.InjectEnergy(new EnergyStack(RF, ValuedReceive), SIMULATE) - ValuedReceive;
                    if (Diff == 0) {
                        int amountToReflect = owner.InjectEnergy(new EnergyStack(RF, ValuedReceive + Diff), MODULATE);
                        // Insert only that amount, which network can inject
                        this.getEnergyStorage(RF, side).modifyEnergyStored(-amountToReflect);
                    }
                }
                // IC2
                if (isStorageInitialized(EU) && (double)this.getEnergyStorage(EU, side).getStored() > 0 && this.getFilteredEnergy(side) != EU) {

                    int ValuedReceive = (int) Math.min((double)this.getEnergyStorage(EU, side).getStored(), this.getMaxTransfer(side));
                    int Diff = owner.InjectEnergy(new EnergyStack(EU, ValuedReceive), SIMULATE) - ValuedReceive;
                    if (Diff == 0) {
                        // Insert only that amount, which network can inject
                        ValuedReceive = Math.min(ValuedReceive, owner.InjectEnergy(new EnergyStack(EU, ValuedReceive + Diff), SIMULATE));
                        this.getEnergyStorage(EU, side).modifyEnergyStored(-owner.InjectEnergy(new EnergyStack(EU,
                                ValuedReceive + Diff), MODULATE));
                    }
                }
                // Mekanism
                if (isStorageInitialized(J) && (double)this.getEnergyStorage(J, side).getStored() > 0 && this.getFilteredEnergy(side) != J) {

                    int ValuedReceive = (int) Math.min((double)this.getEnergyStorage(J, side).getStored(), this.getMaxTransfer(side));
                    int Diff = owner.InjectEnergy(new EnergyStack(J, ValuedReceive), SIMULATE) - ValuedReceive;
                    if (Diff == 0) {
                        // Insert only that amount, which network can inject
                        ValuedReceive = Math.min(ValuedReceive, owner.InjectEnergy(new EnergyStack(J, ValuedReceive + Diff), SIMULATE));
                        this.getEnergyStorage(J, side).modifyEnergyStored(-owner.InjectEnergy(new EnergyStack(J,
                                ValuedReceive + Diff), MODULATE));
                    }
                }

                // Embers
                if (isStorageInitialized(Ember) && (double)this.getEnergyStorage(Ember, side).getStored() > 0 && this.getFilteredEnergy(side) != Ember) {

                    int ValuedReceive = (int) Math.min((double)this.getEnergyStorage(Ember, side).getStored(), this.getMaxTransfer(side));
                    int Diff = owner.InjectEnergy(new EnergyStack(Ember, ValuedReceive), SIMULATE) - ValuedReceive;
                    if (Diff == 0) {
                        // Insert only that amount, which network can inject
                        ValuedReceive = Math.min(ValuedReceive, owner.InjectEnergy(new EnergyStack(Ember, ValuedReceive + Diff), SIMULATE));
                        this.getEnergyStorage(Ember, side).modifyEnergyStored(-owner.InjectEnergy(new EnergyStack(Ember,
                                ValuedReceive + Diff), MODULATE));
                    }
                }
                // Rotary Craft Commented, until RC 1.12.2 will be released
			/*if (this.WattPower > 0 && this.FilteredEnergy != WA) {
				Long ValuedReceive = Math.min(WattPower, this.maxTransfer);
				// Energy Inject not supports Long
				if (this.WattPower < Integer.MAX_VALUE) {
					// But Storage in ae is still can be long
					int Diff = owner.InjectEnergy(new EnergyStack(WA, ValuedReceive.intValue()), SIMULATE) - ValuedReceive.intValue();
					if (Diff == 0) {
						this.WattPower -= ValuedReceive;
						owner.InjectEnergy(new EnergyStack(WA, ValuedReceive.intValue()), MODULATE);
					}
				} else {
					// Then inject energy by fractions of WattPower / Integer.MaxValue
					for (float i = 0; i < WattPower / Integer.MAX_VALUE; i++) {
							if (WattPower / Integer.MAX_VALUE > 1){
								ValuedReceive = ValuedReceive;
							}else if ((WattPower / Integer.MAX_VALUE > 0 && WattPower / Integer.MAX_VALUE < 1)){
								ValuedReceive *= (WattPower / Integer.MAX_VALUE);
							}
							int Diff = owner.InjectEnergy(new EnergyStack(WA, ValuedReceive.intValue()), SIMULATE) - ValuedReceive.intValue();
							if (Diff == 0) {
								this.WattPower -= ValuedReceive;
								owner.InjectEnergy(new EnergyStack(WA, ValuedReceive.intValue()), MODULATE);

							}

					}
				}

			}*/
}

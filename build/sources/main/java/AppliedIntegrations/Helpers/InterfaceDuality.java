package AppliedIntegrations.Helpers;

import AppliedIntegrations.API.*;
import AppliedIntegrations.tile.TileEnergyInterface;
import appeng.api.config.Actionable;
import appeng.api.exceptions.NullNodeConnectionException;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEPartLocation;
import appeng.capabilities.Capabilities;
import ic2.api.energy.tile.IEnergySink;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import teamroots.embers.power.EmberCapabilityProvider;

import static AppliedIntegrations.API.LiquidAIEnergy.*;
import static AppliedIntegrations.API.LiquidAIEnergy.Ember;
import static appeng.api.config.Actionable.MODULATE;
import static appeng.api.config.Actionable.SIMULATE;
import static appeng.api.util.AEPartLocation.INTERNAL;

/**
 * Class handler for both tile interface, and part interface
 */
public class InterfaceDuality implements IInterfaceDuality{

    private IEnergyInterface owner;
    public InterfaceDuality(IEnergyInterface owner){
        this.owner = owner;
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

        for(AEPartLocation side : AEPartLocation.SIDE_LOCATIONS) {
            // Is it modulate, or matrix?
            if (action == Actionable.MODULATE) {
                // RF Api
                if (this.getEnergyStorage(RF, side).getStored() > 0 && this.getEnergyStorage(J, side).getStored() == 0 && this.getFilteredEnergy(side) != RF) {
                    // We can cast double to int, as RFAPI only operates int-energy
                    int ValuedReceive = (int) Math.min(this.getEnergyStorage(RF, side).getStored(), this.getMaxTransfer(side));
                    int Diff = owner.InjectEnergy(new EnergyStack(RF, ValuedReceive), SIMULATE) - ValuedReceive;
                    if (Diff == 0) {
                        int amountToReflect = owner.InjectEnergy(new EnergyStack(RF, ValuedReceive + Diff), MODULATE);
                        // Insert only that amount, which network can inject
                        this.getEnergyStorage(RF, side).modifyEnergyStored(-amountToReflect);
                    }
                }
                // IC2
                if (this.getEnergyStorage(EU, side).getStored() > 0 && this.getFilteredEnergy(side) != EU) {

                    int ValuedReceive = (int) Math.min(this.getEnergyStorage(EU, side).getStored(), this.getMaxTransfer(side));
                    int Diff = owner.InjectEnergy(new EnergyStack(EU, ValuedReceive), SIMULATE) - ValuedReceive;
                    if (Diff == 0) {
                        // Insert only that amount, which network can inject
                        ValuedReceive = Math.min(ValuedReceive, owner.InjectEnergy(new EnergyStack(EU, ValuedReceive + Diff), SIMULATE));
                        this.getEnergyStorage(EU, side).modifyEnergyStored(-owner.InjectEnergy(new EnergyStack(EU,
                                ValuedReceive + Diff), MODULATE));
                    }
                }
                // Mekanism
                if (this.getEnergyStorage(J, side).getStored() > 0 && this.getFilteredEnergy(side) != J) {

                    int ValuedReceive = (int) Math.min(this.getEnergyStorage(J, side).getStored(), this.getMaxTransfer(side));
                    int Diff = owner.InjectEnergy(new EnergyStack(J, ValuedReceive), SIMULATE) - ValuedReceive;
                    if (Diff == 0) {
                        // Insert only that amount, which network can inject
                        ValuedReceive = Math.min(ValuedReceive, owner.InjectEnergy(new EnergyStack(J, ValuedReceive + Diff), SIMULATE));
                        this.getEnergyStorage(J, side).modifyEnergyStored(-owner.InjectEnergy(new EnergyStack(J,
                                ValuedReceive + Diff), MODULATE));
                    }
                }

                // Embers
                if (this.getEnergyStorage(Ember, side).getStored() > 0 && this.getFilteredEnergy(side) != Ember) {

                    int ValuedReceive = (int) Math.min(this.getEnergyStorage(Ember, side).getStored(), this.getMaxTransfer(side));
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
            if(!(owner instanceof TileEnergyInterface)){
                // Break if owner is partEnergyInterface (iterate only one time)
                break;
            }
        }
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
                    int valuedReceive = (int) Math.min(this.getEnergyStorage(getFilteredEnergy(side), INTERNAL).getStored(), this.getMaxTransfer(side));
                    int diff = valuedReceive - owner.ExtractEnergy(new EnergyStack(getFilteredEnergy(side), valuedReceive), SIMULATE);
                    if (diff == 0) {
                        this.getEnergyStorage(this.getFilteredEnergy(side), INTERNAL).modifyEnergyStored(owner.ExtractEnergy(new
                                EnergyStack(getFilteredEnergy(side), valuedReceive), MODULATE));
                        //transferEnergy(this.FilteredEnergy, valuedReceive);
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
        // FE (RF) Capability
        if( capability == Capabilities.FORGE_ENERGY ) {
            return (T) this.getEnergyStorage(RF, side);
            // Ember capability
        }else if(capability == EmberCapabilityProvider.emberCapability){
            return (T) this.getEnergyStorage(Ember, side);
            // Joule capability
        }else if(capability == mekanism.common.capabilities.Capabilities.ENERGY_STORAGE_CAPABILITY ||
                capability == mekanism.common.capabilities.Capabilities.ENERGY_ACCEPTOR_CAPABILITY ||
                capability == mekanism.common.capabilities.Capabilities.ENERGY_OUTPUTTER_CAPABILITY){
            return (T) this.getEnergyStorage(J, side);
            // EU capability
        }else if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            //return (T) upgradeInventory;
        }
        return null;
    }

    public boolean hasCapability(Capability<?> capability) {
        // Register FE capability
        if (capability == Capabilities.FORGE_ENERGY) {
            return true;
        } else if (capability == EmberCapabilityProvider.emberCapability) {
            return true;
        } else if (capability == mekanism.common.capabilities.Capabilities.ENERGY_STORAGE_CAPABILITY ||
                capability == mekanism.common.capabilities.Capabilities.ENERGY_ACCEPTOR_CAPABILITY ||
                capability == mekanism.common.capabilities.Capabilities.ENERGY_OUTPUTTER_CAPABILITY) {
            return true;
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            //return true;
        }
        return false;
    }
}

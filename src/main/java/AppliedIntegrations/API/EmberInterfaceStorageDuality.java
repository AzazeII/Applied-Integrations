package AppliedIntegrations.API;

import AppliedIntegrations.API.IInterfaceStorageDuality;
import AppliedIntegrations.AppliedIntegrations;
import net.minecraftforge.fml.common.Optional;
import teamroots.embers.power.DefaultEmberCapability;

/**
 * Marking class
 */
@net.minecraftforge.fml.common.Optional.InterfaceList(value = { //0_lol
        @Optional.Interface(iface = "teamroots.embers.power.IEmberCapability", modid = "embers", striprefs = true),
        @Optional.Interface(iface = "teamroots.embers.power.DefaultEmberCapability", modid = "embers", striprefs = true)})
public class EmberInterfaceStorageDuality extends DefaultEmberCapability implements IInterfaceStorageDuality {
    @Override
    @Optional.Method(modid = "embers")
    public int getEnergyStored() {
        return (int)getEmber();
    }

    @Override
    @Optional.Method(modid = "embers")
    public int getMaxEnergyStored() {
        return (int)getEmberCapacity();
    }

    @Override
    @Optional.Method(modid = "embers")
    public int receiveEnergy(int amount, boolean b) {
        return (int)addAmount(amount, b);
    }

    @Override
    @Optional.Method(modid = "embers")
    public void modifyEnergyStored(int i) {

    }
}

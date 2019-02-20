package AppliedIntegrations.API;

import net.minecraftforge.fml.common.Optional;
import teamroots.embers.power.DefaultEmberCapability;

/**
 * Marking class
 */
@net.minecraftforge.fml.common.Optional.InterfaceList(value = {
        @Optional.Interface(iface = "teamroots.embers.power.IEmberCapability", modid = "embers", striprefs = true),
        @Optional.Interface(iface = "teamroots.embers.power.DefaultEmberCapability", modid = "embers", striprefs = true)})
public class EmberInterfaceStorageDuality extends DefaultEmberCapability implements IInterfaceStorageDuality {
    @Override
    public int getEnergyStored() {
        return (int)getEmber();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int)getEmberCapacity();
    }

    @Override
    public int receiveEnergy(int amount, boolean b) {
        return (int)addAmount(amount, b);
    }

    @Override
    public void modifyEnergyStored(int i) {
        if(i == 0)
            return;
        if(i > 0)
            addAmount(i, true);
        else
            removeAmount(i, true);
    }
}

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
    public void modifyEnergyStored(int i) {
        if(i == 0)
            return;
        if(i > 0)
            addAmount(i, true);
        else
            removeAmount(i, true);
    }

    @Override
    public double getStored() {
        return getEmber();
    }

    @Override
    public double getMaxStored() {
        return getEmberCapacity();
    }

    @Override
    public double receive(double value, boolean simulate) {
        return addAmount(value, !simulate);
    }

    @Override
    public double extract(double value, boolean simulate) {
        return removeAmount(value, !simulate);
    }
}

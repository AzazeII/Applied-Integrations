package AppliedIntegrations.API.Capabilities;

import net.minecraftforge.fml.common.Optional;
import teamroots.embers.power.DefaultEmberCapability;

/**
 * @Author Azazell
 * Marking class
 */
@net.minecraftforge.fml.common.Optional.InterfaceList(value = {
        @Optional.Interface(iface = "teamroots.embers.power.IEmberCapability", modid = "embers", striprefs = true),
        @Optional.Interface(iface = "teamroots.embers.power.DefaultEmberCapability", modid = "embers", striprefs = true)})
public class EmberInterfaceStorageDuality extends DefaultEmberCapability implements IInterfaceStorageDuality<Double>, InbtStorage {
    @Override
    public void modifyEnergyStored(int i) {
        if(i == 0)
            return;
        addAmount(i, true);
    }

    @Override
    public Class<Double> getTypeClass() {
        return Double.class;
    }

    @Override
    public Double getStored() {
        return getEmber();
    }

    @Override
    public Double getMaxStored() {
        return getEmberCapacity();
    }

    @Override
    public Double receive(Double value, boolean simulate) {
        return addAmount(value, !simulate);
    }

    @Override
    public Double extract(Double value, boolean simulate) {
        return removeAmount(value, !simulate);
    }
}

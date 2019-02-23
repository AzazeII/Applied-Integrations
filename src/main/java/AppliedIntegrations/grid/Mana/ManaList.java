package AppliedIntegrations.grid.Mana;

import AppliedIntegrations.API.Botania.IAEManaStack;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IItemList;

import java.util.Collection;
import java.util.Iterator;

public class ManaList implements IItemList<IAEManaStack> {
    @Override
    public void addStorage(IAEManaStack iaeManaStack) {

    }

    @Override
    public void addCrafting(IAEManaStack iaeManaStack) {

    }

    @Override
    public void addRequestable(IAEManaStack iaeManaStack) {

    }

    @Override
    public IAEManaStack getFirstItem() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterator<IAEManaStack> iterator() {
        return null;
    }

    @Override
    public void resetStatus() {

    }

    @Override
    public void add(IAEManaStack iaeManaStack) {

    }

    @Override
    public IAEManaStack findPrecise(IAEManaStack iaeManaStack) {
        return null;
    }

    @Override
    public Collection<IAEManaStack> findFuzzy(IAEManaStack iaeManaStack, FuzzyMode fuzzyMode) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

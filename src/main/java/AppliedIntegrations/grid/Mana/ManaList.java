package AppliedIntegrations.grid.Mana;

import AppliedIntegrations.API.Botania.IAEManaStack;
import AppliedIntegrations.grid.EnergyIterator;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IItemList;

import java.util.*;

public class ManaList implements IItemList<IAEManaStack> {
    private final Map<IAEManaStack, IAEManaStack> records = new HashMap<>();

    @Override
    public void addStorage(IAEManaStack option) {
        if (option == null)
            return;

        IAEManaStack st = this.getEnergyRecord(option);
        if (st != null) {
            st.incStackSize(option.getStackSize());
            return;
        }

        IAEManaStack opt = option.copy();
        this.putEnergyRecord(opt);
    }

    @Override
    public void addCrafting(IAEManaStack option) {
        if (option == null)
            return;

        IAEManaStack st = this.getEnergyRecord(option);
        if (st != null) {
            st.setCraftable(true);
            return;
        }

        IAEManaStack opt = option.copy();
        opt.setStackSize(0);
        opt.setCraftable(true);
        this.putEnergyRecord(opt);
    }

    @Override
    public void addRequestable(IAEManaStack option) {
        if (option == null)
            return;

        IAEManaStack st = this.getEnergyRecord(option);
        if (st != null) {
            st.setCountRequestable(st.getCountRequestable() + option.getCountRequestable());
            return;
        }

        IAEManaStack opt = option.copy();
        opt.setStackSize(0);
        opt.setCraftable(false);
        opt.setCountRequestable(option.getCountRequestable());
        this.putEnergyRecord(opt);
    }

    @Override
    public IAEManaStack getFirstItem() {
        for (IAEManaStack stack : this)
            return stack;
        return null;
    }

    @Override
    public int size() {
        return this.records.values().size();
    }

    @Override
    public Iterator<IAEManaStack> iterator() {
        return new EnergyIterator<>(this.records.values().iterator());
    }

    @Override
    public void resetStatus() {
        for (IAEManaStack s : this)
            s.reset();
    }

    @Override
    public void add(IAEManaStack option) {
        if (option == null)
            return;

        IAEManaStack stack = this.getEnergyRecord(option);
        if (stack != null) {
            stack.add(option);
            return;
        }

        IAEManaStack opt = option.copy();
        this.putEnergyRecord(opt);
    }

    @Override
    public IAEManaStack findPrecise(IAEManaStack stack) {
        return stack == null ? null : this.getEnergyRecord(stack);
    }

    @Override
    public Collection<IAEManaStack> findFuzzy(IAEManaStack stack, FuzzyMode mode) {
        return stack == null ? Collections.emptyList() : Collections.singletonList(this.findPrecise(stack));
    }

    @Override
    public boolean isEmpty() {
        return !this.iterator().hasNext();
    }

    private IAEManaStack getEnergyRecord(IAEManaStack stack) {
        return this.records.get(stack);
    }

    private IAEManaStack putEnergyRecord(IAEManaStack stack) {
        return this.records.put(stack, stack);
    }
}

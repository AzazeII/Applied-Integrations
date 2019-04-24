package AppliedIntegrations.grid.Mana;

import AppliedIntegrations.api.Botania.IAEManaStack;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IItemList;

import java.util.*;

/**
 * @Author Azazell
 */
public class ManaList implements IItemList<IAEManaStack> {
    private final Map<IAEManaStack, IAEManaStack> records = new HashMap<>();

    @Override
    public void addStorage(IAEManaStack option) {
        if (option == null)
            return;

        IAEManaStack st = this.getManaRecord(option);
        if (st != null) {
            st.incStackSize(option.getStackSize());
            return;
        }

        IAEManaStack opt = option.copy();
        this.putManaRecord(opt);
    }

    @Override
    public void addCrafting(IAEManaStack option) {
        if (option == null)
            return;

        IAEManaStack st = this.getManaRecord(option);
        if (st != null) {
            st.setCraftable(true);
            return;
        }

        IAEManaStack opt = option.copy();
        opt.setStackSize(0);
        opt.setCraftable(true);
        this.putManaRecord(opt);
    }

    @Override
    public void addRequestable(IAEManaStack option) {
        if (option == null)
            return;

        IAEManaStack st = this.getManaRecord(option);
        if (st != null) {
            st.setCountRequestable(st.getCountRequestable() + option.getCountRequestable());
            return;
        }

        IAEManaStack opt = option.copy();
        opt.setStackSize(0);
        opt.setCraftable(false);
        opt.setCountRequestable(option.getCountRequestable());
        this.putManaRecord(opt);
    }

    @Override
    public IAEManaStack getFirstItem() {
        return iterator().hasNext()? iterator().next() : null;
    }

    @Override
    public int size() {
        return this.records.values().size();
    }

    @Override
    public Iterator<IAEManaStack> iterator() {
        return new ManaIterator<IAEManaStack>(this.records.values().iterator());
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

        IAEManaStack stack = this.getManaRecord(option);
        if (stack != null) {
            stack.add(option);
            return;
        }

        IAEManaStack opt = option.copy();
        this.putManaRecord(opt);
    }

    @Override
    public IAEManaStack findPrecise(IAEManaStack stack) {
        return stack == null ? null : this.getManaRecord(stack);
    }

    @Override
    public Collection<IAEManaStack> findFuzzy(IAEManaStack stack, FuzzyMode mode) {
        return stack == null ? Collections.emptyList() : Collections.singletonList(this.findPrecise(stack));
    }

    @Override
    public boolean isEmpty() {
        return !this.iterator().hasNext();
    }

    private IAEManaStack getManaRecord(IAEManaStack stack) {
        return this.records.get(stack);
    }

    private IAEManaStack putManaRecord(IAEManaStack stack) {
        return this.records.put(stack, stack);
    }
}

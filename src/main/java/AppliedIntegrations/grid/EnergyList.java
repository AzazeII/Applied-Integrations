package AppliedIntegrations.grid;


import AppliedIntegrations.api.Storage.IAEEnergyStack;
import appeng.api.config.FuzzyMode;
import appeng.api.storage.data.IItemList;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @Author Azazell
 */
public class EnergyList implements IItemList<IAEEnergyStack> {

	private final Map<IAEEnergyStack, IAEEnergyStack> records = new HashMap<>();

	@Override
	public void addStorage(IAEEnergyStack option) {

		if (option == null) {
			return;
		}

		IAEEnergyStack st = this.getEnergyRecord(option);
		if (st != null) {
			st.incStackSize(option.getStackSize());
			return;
		}

		IAEEnergyStack opt = option.copy();
		this.putEnergyRecord(opt);
	}

	@Override
	public void addCrafting(IAEEnergyStack option) {

		if (option == null) {
			return;
		}

		IAEEnergyStack st = this.getEnergyRecord(option);
		if (st != null) {
			st.setCraftable(true);
			return;
		}

		IAEEnergyStack opt = option.copy();
		opt.setStackSize(0);
		opt.setCraftable(true);
		this.putEnergyRecord(opt);
	}

	@Override
	public void addRequestable(IAEEnergyStack option) {

		if (option == null) {
			return;
		}

		IAEEnergyStack st = this.getEnergyRecord(option);
		if (st != null) {
			st.setCountRequestable(st.getCountRequestable() + option.getCountRequestable());
			return;
		}

		IAEEnergyStack opt = option.copy();
		opt.setStackSize(0);
		opt.setCraftable(false);
		opt.setCountRequestable(option.getCountRequestable());
		this.putEnergyRecord(opt);
	}

	@Override
	public IAEEnergyStack getFirstItem() {

		return iterator().hasNext() ? iterator().next() : null;
	}

	@Override
	public int size() {

		return this.records.values().size();
	}

	@Nonnull
	@Override
	public Iterator<IAEEnergyStack> iterator() {

		return new EnergyIterator<>(this.records.values().iterator());
	}

	@Override
	public void resetStatus() {

		for (IAEEnergyStack s : this)
			s.reset();
	}

	private IAEEnergyStack getEnergyRecord(IAEEnergyStack stack) {

		return this.records.get(stack);
	}

	private IAEEnergyStack putEnergyRecord(IAEEnergyStack stack) {

		return this.records.put(stack, stack);
	}

	@Override
	public void add(IAEEnergyStack option) {

		if (option == null) {
			return;
		}

		IAEEnergyStack stack = this.getEnergyRecord(option);
		if (stack != null) {
			stack.add(option);
			return;
		}

		IAEEnergyStack opt = option.copy();
		this.putEnergyRecord(opt);
	}

	@Override
	public IAEEnergyStack findPrecise(IAEEnergyStack stack) {

		return stack == null ? null : this.getEnergyRecord(stack);
	}

	@Override
	public Collection<IAEEnergyStack> findFuzzy(IAEEnergyStack stack, FuzzyMode mode) {

		return stack == null ? Collections.emptyList() : Collections.singletonList(this.findPrecise(stack));
	}

	@Override
	public boolean isEmpty() {

		return !this.iterator().hasNext();
	}
}

package AppliedIntegrations.api.Storage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Azazell
 */
public class EnergyRepo implements IEnergyRepo {
	/**
	 * The actual cache of energies.
	 */
	private final Map<LiquidAIEnergy, IEnergyStack> cache;

	/**
	 * Creates the repository.
	 */
	public EnergyRepo() {
		// Create the cache
		this.cache = new ConcurrentHashMap<LiquidAIEnergy, IEnergyStack>();
	}

	@Override
	public Set<LiquidAIEnergy> energySet() {
		return this.cache.keySet();
	}

	@Override
	public void clear() {
		this.cache.clear();
	}

	@Override
	public boolean containsEnergy(final LiquidAIEnergy Energy) {
		return this.cache.containsKey(Energy);
	}

	@Override
	public void copyFrom(final Collection<IEnergyStack> stacks) {
		// Clear
		this.clear();

		// Null check
		if (stacks == null) {
			return;
		}

		// Add each
		for (IEnergyStack stack : stacks) {
			this.cache.put(stack.getEnergy(), stack.copy());
		}
	}

	@Override
	public IEnergyStack get(final LiquidAIEnergy Energy) {
		return null;
	}

	@Override
	public Collection<IEnergyStack> getAll() {
		return this.cache.values();
	}

	@Override
	public IEnergyStack getOrDefault(final LiquidAIEnergy Energy, final IEnergyStack defaultValue) {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return this.cache.isEmpty();
	}

	@Override
	public IEnergyStack postChange(final LiquidAIEnergy Energy, final long change) {
		// Is the energy null?
		if (Energy == null) {
			// No changes
			return null;
		}
		// Get the current stack
		IEnergyStack current = this.cache.get(Energy);

		// Is their nothing currently stored?
		if (current == null) {
			// Create a new stack
			IEnergyStack newStack = null;

			// Set craftability


			// Is the change positive?
			if (change > 0) {
				// Create a new stack
				newStack = new EnergyStack(Energy, change);
			}

			// Is there a new stack?
			if (newStack != null) {
				// Add the stack
				this.cache.put(newStack.getEnergy(), newStack);
			}

			// Done
			return null;
		}

		// There is something currently stored
		IEnergyStack previous = current.copy();

		// Set craftability
		// Calculate the new amount
		long previousAmount = previous.getStackSize();
		long newAmount = Math.max(0L, previousAmount + change);

		// Stack drained and is not craftable?
		if ((newAmount == 0)) {
			// Remove it
			this.cache.remove(Energy);
		} else {
			// Update the amount & craftability
			current.setStackSize(newAmount);
		}

		// Return the previous stack
		return previous;
	}

	@Override
	public IEnergyStack postChange(final IEnergyStack change) {
		// Null check
		if (change == null) {
			return null;
		}

		return this.postChange(change.getEnergy(), change.getStackSize());
	}

	@Override
	public IEnergyStack remove(final LiquidAIEnergy energy) {
		return this.cache.remove(energy);
	}

	@Override
	public IEnergyStack setEnergy(final LiquidAIEnergy energy, final long amount) {
		if (energy == null) {
			return null;
		}

		// Set the stack, and return the old one.
		return this.cache.put(energy, new EnergyStack(energy, amount));

	}

	@Override
	public IEnergyStack setEnergy(final IEnergyStack stack) {
		// Null check
		if (stack == null) {
			return null;
		}

		return this.setEnergy(stack.getEnergy(), stack.getStackSize());
	}

	@Override
	public int size() {
		return this.cache.size();
	}
}

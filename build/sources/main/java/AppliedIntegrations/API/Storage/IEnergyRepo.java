package AppliedIntegrations.API.Storage;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public interface IEnergyRepo
{

    /**
     * Returns a set of the stored energies.
     *
     * @return
     */
    @Nonnull
    Set<LiquidAIEnergy> energySet();

    /**
     * Removes all entries in the repo.
     */
    void clear();

    /**
     * Returns true if the repo has the energy stored.
     *
     * @param energy
     * @return
     */
    boolean containsEnergy( @Nonnull LiquidAIEnergy energy );

    /**
     * Sets the repo to match the specified collection.
     * Any existing data is removed.
     *
     * @param stacks
     */
    void copyFrom( @Nonnull Collection<IEnergyStack> stacks );

    /**
     * Gets the energy stack associated with the energy or null.
     */
    IEnergyStack get( @Nonnull LiquidAIEnergy energy );

    /**
     * Returns all energy information stored in the repo.
     *
     * @return
     */
    @Nonnull
    Collection<IEnergyStack> getAll();

    /**
     * Gets the energy stack associated with the energy or specified the default value.
     */
    IEnergyStack getOrDefault(@Nonnull LiquidAIEnergy energy, @Nullable IEnergyStack defaultValue );

    /**
     * Returns true if the repo is empty.
     *
     * @return
     */
    boolean isEmpty();

    /**
     * Changes the energy in the repo by the specified values.
     *
     * @param energy
     * @param change
     * If this value is null and there is a stored value, its crafting status will remain the same.
     * Otherwise it is set to false.
     * @return The previous stack, if there was one.
     */
    @Nullable
    IEnergyStack postChange(@Nonnull LiquidAIEnergy energy, long change );

    /**
     * Changes the energy in the repo by the specified energy stack.
     *
     * @param change
     * @return The previous stack, if there was one.
     */
    @Nullable
    IEnergyStack postChange( @Nonnull IEnergyStack change );

    /**
     * Removes an energy from the repo.<br>
     * Returns the removed stack, if there was one removed.
     *
     * @param energy
     * @return
     */
    @Nullable
    IEnergyStack remove( LiquidAIEnergy energy );

    /**
     * Sets the energy in the repo by the specified values.
     *
     * @param energy
     * @param amount
     * @return The previous stack, if there was one.
     */
    @Nullable
    IEnergyStack setEnergy(@Nonnull LiquidAIEnergy energy, long amount );

    /**
     * Sets the energy in the repo to the specified energy stack.
     *
     * @param stack
     * @return The previous stack, if there was one.
     */
    @Nullable
    IEnergyStack setEnergy( @Nonnull IEnergyStack stack );

    /**
     * Returns the number of unique energies stored.
     *
     * @return
     */
    int size();
}

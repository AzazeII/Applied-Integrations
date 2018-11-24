package AppliedIntegrations.grid;

import AppliedIntegrations.API.*;
import AppliedIntegrations.API.Grid.IMEEnergyMonitor;
import AppliedIntegrations.API.Grid.IMEEnergyMonitorReceiver;
import AppliedIntegrations.API.Storage.EnergyRepo;
import AppliedIntegrations.API.Storage.IEnergyRepo;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import com.google.common.collect.ImmutableList;

import java.lang.ref.WeakReference;
import java.util.*;
/**
 * @Author Azazell
 */
public class EnergyMonitor
        implements IMEEnergyMonitor, IMEMonitorHandlerReceiver<IAEFluidStack>
{
    /**
     * The amount of power required to transfer 1 energy.
     */
    public static final double AE_PER_ENERGY = 0.3;

    /**
     * Objects who wish to be notified of any changes.
     */
    protected final HashMap<IMEEnergyMonitorReceiver, Object> listeners;

    /**
     * The grids fluid monitor.
     */
    protected IMEMonitor<IAEFluidStack> fluidMonitor;

    /**
     * The grids energy manager.
     */
    protected IEnergyGrid energyGrid;

    /**
     * Collection backed by the cache that shows the energy amounts in the network. Is created the first time it is needed.<br>
     * Any changes to the cache are reflected in the view.
     */
    private Collection<IEnergyStack> cacheView;

    /**
     * Used to validate the state of the fluid listener, can not be null
     */
    private WeakReference<Object> token;

    /**
     * Cache of energy.
     */
    private final IEnergyRepo cache;

    /**
     * When true the full storage list needs to be pulled to update the cache.
     */
    protected boolean cacheNeedsUpdate = false;

    public EnergyMonitor()
    {
        // Create the cache
        this.cache = new EnergyRepo();

        // Create the listeners table
        this.listeners = new HashMap<IMEEnergyMonitorReceiver, Object>();
    }

    /**
     * Wraps the specified fluid monitor and energy grid.
     *
     * @param fluidMonitor
     * Fluid monitor to listen to
     * @param energyGrid
     * Energy grid to extract power from
     * @param validationToken
     * Used to validate the state of the fluid listener, can not be null
     */
    public EnergyMonitor( final IMEMonitor<IAEFluidStack> fluidMonitor, final IEnergyGrid energyGrid, final Object validationToken )
    {
        // Call default constructor
        this();

        // Call wrap
        this.wrap( fluidMonitor, energyGrid, validationToken );
    }

    /**
     *
     * @param amount
     * @param mode
     * @param source
     * @return Amount that was <strong>not</strong> injected
     */
    private long injectEnergySafely(final LiquidAIEnergy energy, final long amount, final Actionable mode, final BaseActionSource source,
                                    final LiquidAIEnergy energyGas )
    {
        // Create the fluid request
        IAEFluidStack fluidRequest = Utils.ConvertToAEFluidStack( energyGas, amount );

        // Inject fluid
        IAEFluidStack fluidRejected = this.fluidMonitor.injectItems( fluidRequest, mode, source );

        // Was any rejected?
        if( ( fluidRejected != null ) && ( fluidRejected.getStackSize() > 0 ) )
        {
            if( fluidRejected.getStackSize() == fluidRequest.getStackSize() )
            {
                // All energy was rejected
                return amount;
            }

            return fluidRejected.getStackSize();
        }

        // All energy was accepted.
        return 0;
    }

    /**
     * Notifies all listeners of the specified changes.
     *
     * @param changes
     */
    private void notifyListeners( final List<IEnergyStack> changes )
    {
        // Get an immutable copy
        ImmutableList<IEnergyStack> changeList = ImmutableList.copyOf( changes );

        // Get the listener iterator
        Iterator<Map.Entry<IMEEnergyMonitorReceiver, Object>> entryIterator = this.listeners.entrySet().iterator();

        // Inform all listeners of the changes
        while( entryIterator.hasNext() )
        {
            // Get the listener
            Map.Entry<IMEEnergyMonitorReceiver, Object> entry = entryIterator.next();

            // Validate the token
            if( entry.getKey().isValid( entry.getValue() ) )
            {
                // Valid token
                entry.getKey().postChange( this, changeList );
            }
            else
            {
                // Invalid token, remove from listener list
                entryIterator.remove();
            }
        }
    }

    /**
     * Updates the cache to match the contents of the network and updates any
     * listeners of the changes.
     */
    @SuppressWarnings("null")
    protected void updateCacheToMatchNetwork()
    {
        // Changes made to the cache
        List<IEnergyStack> energyChanges = null;

        // The currently stored energies
        Set<LiquidAIEnergy> previousEnergy = null;

        // Are there any listeners?
        boolean hasListeners = ( this.listeners.size() > 0 );
        if( hasListeners )
        {
            // Create the change trackers
            energyChanges = new ArrayList<IEnergyStack>();
            previousEnergy = new HashSet<LiquidAIEnergy>();
            previousEnergy.addAll( this.cache.energySet() );
        }
        else
        {
            // Can safely clear the cache
            this.cache.clear();
        }

        // Is the network powerd?
        if( this.energyGrid.isNetworkPowered() )
        {

            // Get the list of fluids in the network
            IItemList<IAEFluidStack> fluidStackList = this.fluidMonitor.getStorageList();

            // Loop over all fluids
            for( IAEFluidStack fluidStack : fluidStackList )
            {
                // Ensure the fluid is an energy 
                if( !( fluidStack.getFluid() instanceof LiquidAIEnergy) )
                {
                    // Not an energy .
                    continue;
                }

                // Get the  energy
                LiquidAIEnergy energy = ( (LiquidAIEnergy)fluidStack.getFluid() ).getEnergy();

                // Calculate the new amount
                Long newAmount = fluidStack.getStackSize();

                // Update the cache
                IEnergyStack prevStack = this.cache.setEnergy( energy, newAmount );

                // Are there any listeners?
                if( hasListeners )
                {
                    // Remove from the previous mapping
                    previousEnergy.remove( energy );

                    // Calculate the difference
                    long diff = ( newAmount - ( prevStack != null ? prevStack.getStackSize() : 0 ) );

                    if( diff != 0 )
                    {
                        // Add to the changes
                        energyChanges.add( new EnergyStack( energy, diff ) );
                    }
                }
            }
        }

        // Are there any listeners?
        if( hasListeners )
        {
            // Anything left in the previous mapping is no longer present in the network
            for( LiquidAIEnergy energy : previousEnergy )
            {
                energyChanges.add( new EnergyStack( energy, -this.cache.remove( energy ).getStackSize(), false ) );
            }

            // Notify listeners
            this.notifyListeners( energyChanges );
        }

        // Mark the cache as valid
        this.cacheNeedsUpdate = false;

    }

    @Override
    public void addListener( final IMEEnergyMonitorReceiver listener, final Object verificationToken )
    {
        if( verificationToken == null )
        {
            throw new NullPointerException( "Verification token can not be null" );
        }

        // If this is the first listener, and the cache is out of sync, it needs to be updated first
        if( ( this.listeners.size() == 0 ) && ( this.cacheNeedsUpdate ) )
        {
            this.updateCacheToMatchNetwork();
        }

        this.listeners.put( listener, verificationToken );
    }

    /**
     * Detaches from the fluid monitor.
     * Note that the monitor is no longer valid from this point forward.
     */
    public void detach()
    {
        if( this.fluidMonitor != null )
        {
            this.fluidMonitor.removeListener( this );
            this.token = null;
        }
    }

    @Override
    public long extractEnergy(final LiquidAIEnergy energy, final long amount, final Actionable mode, final BaseActionSource source, final boolean powered )
    {
        // Ensure the energy is not null, and the amount is > 0
        if( ( energy == null ) || ( amount <= 0 ) )
        {
            // Invalid arguments
            return 0;
        }
        if( powered )
        {
            // Simulate power extraction
            double powerRequest = EnergyMonitor.AE_PER_ENERGY * amount;
            double powerReceived = this.energyGrid.extractAEPower( powerRequest, Actionable.SIMULATE, PowerMultiplier.CONFIG );

            // Was enough power extracted?
            if( powerReceived < powerRequest )
            {
                // Not enough power
                return 0;
            }
        }

        // Create the fluid request
        IAEFluidStack fluidRequest = Utils.ConvertToAEFluidStack(energy,amount);

        // Attempt the extraction
        IAEFluidStack fluidReceived = this.fluidMonitor.extractItems( fluidRequest, mode, source );

        // Was any fluid received?
        if( ( fluidReceived == null ) || ( fluidReceived.getStackSize() <= 0 ) )
        {
            // Fluid not found.
            return 0;
        }

        // Convert the received fluid into an energy stack
        long extractedAmount = fluidReceived.getStackSize();

        // Extract power if modulating
        if( ( powered ) && ( mode == Actionable.MODULATE ) )
        {
            this.energyGrid.extractAEPower( EnergyMonitor.AE_PER_ENERGY * extractedAmount, Actionable.MODULATE, PowerMultiplier.CONFIG );
        }

        return extractedAmount;

    }

    @Override
    public long getEnergyAmount( final LiquidAIEnergy energy )
    {
        if( !this.energyGrid.isNetworkPowered() )
        {
            return 0;
        }

        // Does the cache need to be updated?
        if( this.cacheNeedsUpdate )
        {
            // Update the cache
            this.updateCacheToMatchNetwork();
        }

        // Does the cache have this energy?
        if( this.cache.containsEnergy( energy ) )
        {
            // Return the amount
            return this.cache.get( energy ).getStackSize();
        }

        return 0;
    }

    @Override
    public Collection<IEnergyStack> getEnergyList()
    {
        if( !this.energyGrid.isNetworkPowered() )
        {
            return new ArrayList<IEnergyStack>();
        }

        // Does the cache need to be updated?
        if( this.cacheNeedsUpdate )
        {
            this.updateCacheToMatchNetwork();
        }

        // Does the view need to be created?
        if( this.cacheView == null )
        {
            this.cacheView = Collections.unmodifiableCollection( this.cache.getAll() );
        }

        return this.cacheView;
    }

    @Override
    public long injectEnergy(final LiquidAIEnergy energy, final long amount, final Actionable mode, final BaseActionSource source, final boolean powered )
    {
        // Ensure the energy is not null, and the amount is > 0
        if( ( energy == null ) || ( amount <= 0 ) )
        {
            // Invalid arguments
            return amount;
        }

        // Get the  form of the energy
        LiquidAIEnergy energyGas = energy;


        // Simulate the injection
        long injectedAmount = amount;
        long rejectedAmount = this.injectEnergySafely( energy, injectedAmount, Actionable.SIMULATE, source, energyGas );
        injectedAmount -= rejectedAmount;

        // Was all rejected?
        if( injectedAmount == 0 )
        {
            return amount;
        }

        // Is this a powered injection?
        if( powered )
        {
            // Simulate power extraction
            double powerRequest = EnergyMonitor.AE_PER_ENERGY * injectedAmount;
            double powerReceived = this.energyGrid.extractAEPower( powerRequest, Actionable.SIMULATE, PowerMultiplier.CONFIG );

            // Was enough power extracted?
            if( powerReceived < powerRequest )
            {
                // Not enough power
                return amount;
            }
        }

        // Modulating?
        if( mode == Actionable.MODULATE )
        {
            // Inject
            rejectedAmount = this.injectEnergySafely( energy, injectedAmount, Actionable.MODULATE, source, energyGas );
            injectedAmount -= rejectedAmount;

            // Adjust and extract power
            if( powered )
            {
                double powerRequest = EnergyMonitor.AE_PER_ENERGY * injectedAmount;
                this.energyGrid.extractAEPower( powerRequest, Actionable.MODULATE, PowerMultiplier.CONFIG );
            }
        }

        return amount - injectedAmount;
    }

    @Override
    public boolean isValid( final Object verificationToken )
    {
        // Has a token been assigned?
        if( this.token != null )
        {
            // Does the token match?
            if( this.token.equals( verificationToken ) )
            {
                // Does the token still exist?
                Object vToken = this.token.get();
                if( vToken != null )
                {
                    // Return true
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onListUpdate()
    {
        // Mark that the cache needs to be updated
        this.cacheNeedsUpdate = true;
    }

    @Override
    public void postChange(	final IBaseMonitor<IAEFluidStack> monitor, final Iterable<IAEFluidStack> fluidChanges,
                               final BaseActionSource actionSource )
    {
        // Ensure the cache is up to date
        if( this.cacheNeedsUpdate )
        {
            // No use updating an out of sync cache
            return;

            /*
             * Note: this should never happen if there are listeners. As the cache will be updated when a list update occurs.
             * If any changes occur between a call to onListUpdate() and onUpdateTick(), those changes will be ignored until the cache is updated.
             */
        }

        // Ensure there was a change
        if( fluidChanges == null )
        {
            return;
        }

        // True if there are any listeners
        boolean hasListeners = ( this.listeners.size() > 0 );

        // Changes made to the cache.
        List<IEnergyStack> energyChanges = null;

        // Search the changes for energy 
        for( IAEFluidStack change : fluidChanges )
        {
            // Is the change an energy ?
            if( ( change.getFluid() instanceof LiquidAIEnergy) )
            {
                // Get the energy
                LiquidAIEnergy energy = ( (LiquidAIEnergy)change.getFluid() ).getEnergy();

                // Calculate the difference
                long changeAmount = change.getStackSize();

                // Update the cache
                IEnergyStack previous = this.cache.postChange( energy, changeAmount );

                // Add to the changes
                if( hasListeners )
                {
                    // Create the change list if needed
                    if( energyChanges == null )
                    {
                        energyChanges = new ArrayList<IEnergyStack>();
                    }

                    // Was there a previous stack?
                    IEnergyStack changeStack;
                    if( previous != null )
                    {
                        // Re-use it, as it is no longer associated with anything
                        // Plus, it carries the crafting info.
                        changeStack = previous;
                        previous.setStackSize( changeAmount );
                    }
                    else
                    {
                        // Create a new stack
                        changeStack = new EnergyStack( energy, changeAmount );
                    }

                    // Add the change
                    energyChanges.add( changeStack );
                }
            }
        }

        // Notify any listeners
        if( ( energyChanges != null ) && ( energyChanges.size() > 0 ) )
        {
            this.notifyListeners( energyChanges );
        }

    }

    @Override
    public void removeListener( final IMEEnergyMonitorReceiver listener )
    {
        this.listeners.remove( listener );
    }

    /**
     * Wraps the specified fluid monitor and energy grid.
     *
     * @param fluidMonitor
     * Fluid monitor to listen to
     * @param energyGrid
     * Energy grid to extract power from
     * @param validationToken
     * Used to validate the state of the fluid listener, can not be null
     */
    public void wrap( final IMEMonitor<IAEFluidStack> fluidMonitor, final IEnergyGrid energyGrid, final Object validationToken )
    {
        // Ensure the token is not null
        if( validationToken != null )
        {
            // Set the token
            this.token = new WeakReference<Object>( validationToken );
        }
        else
        {
            // Throw exception
            throw new NullPointerException( "Validation Token Can Not Be Null" );
        }

        // Set the fluid monitor
        this.fluidMonitor = fluidMonitor;

        // Set the energy grid
        this.energyGrid = energyGrid;

        // Add listener
        this.fluidMonitor.addListener( this, this.token );

        // Mark that the cache needs to be updated
        this.cacheNeedsUpdate = true;
    }
}
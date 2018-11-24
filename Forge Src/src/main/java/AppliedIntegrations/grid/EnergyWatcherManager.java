package AppliedIntegrations.grid;

import AppliedIntegrations.API.Grid.IAIEnergyWatcher;
import AppliedIntegrations.API.Grid.IAIEnergyWatcherHost;
import AppliedIntegrations.API.Grid.IMEEnergyMonitor;
import AppliedIntegrations.API.Grid.IMEEnergyMonitorReceiver;
import AppliedIntegrations.API.IEnergyStack;
import AppliedIntegrations.API.LiquidAIEnergy;
import appeng.api.networking.IGridNode;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
/**
 * @Author Azazell
 */
public class EnergyWatcherManager
        implements IMEEnergyMonitorReceiver
{
    /**
     * Maps Node->Watcher
     */
    private HashMap<IGridNode, IAIEnergyWatcher> watchers = new HashMap<IGridNode, IAIEnergyWatcher>();

    /**
     * Maps Energy -> Watchers
     */
    private HashMap<LiquidAIEnergy, HashSet<IAIEnergyWatcher>> watchedEnergies = new HashMap<LiquidAIEnergy, HashSet<IAIEnergyWatcher>>();

    /**
     * True when the manager is listening for changes.
     */
    private boolean isListeningForChanges = false;

    private final GridEnergyCache gridCache;

    public EnergyWatcherManager( final GridEnergyCache gridCache )
    {
        this.gridCache = gridCache;
    }

    /**
     * Adds a watcher.
     *
     * @param node
     * @param watcher
     */
    public void addWatcher( final IGridNode node, final IAIEnergyWatcher watcher )
    {
        // Add the watcher
        this.watchers.put( node, watcher );

        // Is the manager not listening for changes?
        if( !this.isListeningForChanges )
        {
            // Listen for changes
            this.gridCache.addListener( this, this.gridCache.internalGrid );
            this.isListeningForChanges = true;
        }
    }

    @Override
    public boolean isValid( final Object verificationToken )
    {
        return this.isListeningForChanges && ( verificationToken == this.gridCache.internalGrid );
    }

    /**
     * Called by watchers when a new energy is to be tracked.
     */
    public void onWatcherAddEnergy(final IAIEnergyWatcher watcher, final LiquidAIEnergy energy )
    {
        HashSet<IAIEnergyWatcher> aWatchers;

        // Does the set need to be created?
        if( !this.watchedEnergies.containsKey( energy ) )
        {
            // Create the set
            aWatchers = new HashSet<IAIEnergyWatcher>();
            this.watchedEnergies.put( energy, aWatchers );
        }
        else
        {
            // Get the set
            aWatchers = this.watchedEnergies.get( energy );
        }

        // Add the watcher
        aWatchers.add( watcher );
    }

    /**
     * Called by watchers just before they are cleared.
     *
     * @param watcher
     * @param previouslyTrackedEnergies
     */
    public void onWatcherCleared( final IAIEnergyWatcher watcher, final HashSet<LiquidAIEnergy> previouslyTrackedEnergies )
    {
        for( LiquidAIEnergy energy : previouslyTrackedEnergies )
        {
            this.onWatcherRemoveenergy( watcher, energy );
        }
    }

    /**
     * Called by watchers when an energy is no longer to be tracked.
     *
     * @param watcher
     * @param energy
     */
    public void onWatcherRemoveenergy( final IAIEnergyWatcher watcher, final LiquidAIEnergy energy )
    {
        // Get the set
        HashSet<IAIEnergyWatcher> aWatchers = this.watchedEnergies.get( energy );
        if( aWatchers != null )
        {
            // Remove the watcher
            aWatchers.remove( watcher );

            // Is the set empty?
            if( aWatchers.isEmpty() )
            {
                // Remove the mapping
                this.watchedEnergies.remove( energy );
            }
        }
    }
    @Override
    public void postChange(IMEEnergyMonitor fromMonitor, @Nonnull Iterable<IEnergyStack> changes )
    {
        // Fast bail
        if( this.watchedEnergies.isEmpty() )
        {
            return;
        }

        // Loop over all changes
        for( IEnergyStack change : changes )
        {
            // Is the change being watched for?
            if( this.watchedEnergies.containsKey( change.getEnergy() ) )
            {
                // Get the set
                HashSet<IAIEnergyWatcher> watcherSet = this.watchedEnergies.get( change.getEnergy() );

                // Get the full amount in the system
                long fullAmount = this.gridCache.getEnergyAmount( change.getEnergy() );

                // Update each watcher
                for( IAIEnergyWatcher watcher : watcherSet )
                {
                    // Get the watchers host
                    IAIEnergyWatcherHost host = watcher.getHost();

                    // Update the host
                    if( host != null )
                    {
                        host.onEnergyChange( change.getEnergy(), fullAmount, change.getStackSize() );
                    }
                }
            }
        }
    }

    /**
     * Removes a watcher.
     *
     * @param node
     */
    public void removeWatcher( final IGridNode node )
    {
        // Get the watcher
        IAIEnergyWatcher watcher = this.watchers.get( node );
        if( watcher != null )
        {
            // Clear the watcher
            watcher.clear();

            // Remove the watcher
            this.watchers.remove( node );

            // Is the list empty?
            if( this.watchers.isEmpty() )
            {
                // Ensure the watched energies is also empty
                this.watchedEnergies.clear();

                // Stop listening
                this.gridCache.removeListener( this );
                this.isListeningForChanges = false;
            }
        }
    }
}

package AppliedIntegrations.grid;

import AppliedIntegrations.API.Storage.IEnergyStorageChannel;
import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridStorage;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.energy.IEnergyWatcherHost;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPostCacheConstruction;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

/**
 * @Author Azazell
 */
public class GridEnergyCache
        extends EnergyMonitor
        implements IEnergyAIGrid
{

    /**
     * Grid the cache is part of.
     */
    final IGrid internalGrid;

    /**
     * Manages the watchers
     */
    private final EnergyWatcherManager energyWatcherManger;

    /**
     * The 'result' of energy crafting operations.
     */


    public GridEnergyCache( final IGrid grid )
    {
        // Set the grid
        this.internalGrid = grid;

        // Create the watcher manager
        this.energyWatcherManger = new EnergyWatcherManager( this );


    }

    /**
     * Called by the crafting watcher when an update is needed.
     */
    void markForUpdate()
    {
        this.cacheNeedsUpdate = true;
    }


    @Override
    protected void updateCacheToMatchNetwork()
    {
                      // Call super
                super.updateCacheToMatchNetwork();

                // Is the network powered?
                if( !this.energyGrid.isNetworkPowered() )
                {
                    return;
                }

                // Get the item monitor
                IStorageGrid storage = (IStorageGrid)this.internalGrid.getCache( IStorageGrid.class );
                IMEMonitor<IAEItemStack> itemMonitor = storage.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

                // Get stored items
                IItemList<IAEItemStack> storedItems = itemMonitor.getStorageList();
                if( ( storedItems == null ) || ( storedItems.size() == 0 ) )
                {
                    return;
                }
                for( IAEItemStack stack : storedItems )
                {
                    if( stack == null )
                    {
                        continue;
                    }



            }
        }

    @Override
    public void addNode(final IGridNode gridNode, final IGridHost machine )
    {
        // Does the node wish to watch for changes?
      /*  if( machine instanceof IEnergyWatcherHost)
        {
            // Cast
            IEnergyWatcherHost host = (IEnergyWatcherHost)machine;

            // Create the watcher
            EnergyWatcher watcher = new EnergyWatcher( this.energyWatcherManger, host );

            // Add to the watcher manager
            this.energyWatcherManger.addWatcher( gridNode, watcher );

            // Inform the host it has a watcher
            host.updateWatcher( watcher );
        }*/
    }



    @MENetworkEventSubscribe
    public void onGridCacheReady( final MENetworkPostCacheConstruction event )
    {
        // Get the storage grid
        IStorageGrid storage = (IStorageGrid)this.internalGrid.getCache( IStorageGrid.class );

        // Wrap
        this.wrap( storage.getInventory(AEApi.instance().storage().getStorageChannel(IEnergyStorageChannel.class)), (IEnergyGrid)this.internalGrid.getCache( IEnergyGrid.class ), this.internalGrid );


    }

    @Override
    public void onJoin( final IGridStorage sourceStorage )
    {
        // Mark that the cache needs to be updated
        this.cacheNeedsUpdate = true;
    }

    @Override
    public void onSplit( final IGridStorage destinationStorage )
    {
    }

    @Override
    public void onUpdateTick()
    {
        try
        {
            /*
             * If the cache is invalid and there are listeners this will update the cache to match the network.
             * If there are no listeners the update is deferred until there are listeners, or the cache is accessed.
             */
            if( this.cacheNeedsUpdate && ( this.listeners.size() > 0 ) )
            {
                // Update the cache
                this.updateCacheToMatchNetwork();
            }
        }
        catch( Exception e )
        {
            // Ignored
        }
    }

    @Override
    public void populateGridStorage( final IGridStorage destinationStorage )
    {
        // Ignored
    }

    @Override
    public void removeNode( final IGridNode gridNode, final IGridHost machine )
    {
        if( machine instanceof IEnergyWatcherHost )
        {
            this.energyWatcherManger.removeWatcher( gridNode );
        }
    }
}
